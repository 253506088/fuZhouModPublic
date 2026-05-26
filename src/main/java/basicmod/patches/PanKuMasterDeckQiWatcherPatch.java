package basicmod.patches;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basicmod.cards.demons.CardEarthDemonQi;
import basicmod.cards.demons.CardFireDemonQi;
import basicmod.cards.demons.CardHeavenDemonQi;
import basicmod.cards.demons.CardMoonDemonQi;
import basicmod.cards.demons.CardMountainDemonQi;
import basicmod.cards.demons.CardThunderDemonQi;
import basicmod.cards.demons.CardWaterDemonQi;
import basicmod.cards.demons.CardWindDemonQi;
import basicmod.modifiers.demons.EarthDemonQiModifier;
import basicmod.modifiers.demons.FireDemonQiModifier;
import basicmod.modifiers.demons.HeavenDemonQiModifier;
import basicmod.modifiers.demons.MoonDemonQiModifier;
import basicmod.modifiers.demons.MountainDemonQiModifier;
import basicmod.modifiers.demons.ThunderDemonQiModifier;
import basicmod.modifiers.demons.WaterDemonQiModifier;
import basicmod.modifiers.demons.WindDemonQiModifier;
import basicmod.relics.PanKuBox;
import basicmod.relics.PanKuDemonQiHelper;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

/**
 * 潘库宝盒魔气返还监听器。
 *
 * 这个类不去适配每个删牌、变化牌事件，而是监听玩家主卡组本身：
 * 只要上一轮快照中存在的卡牌 UUID 从主卡组消失，就认为这张旧卡被删除、变化或替换。
 * 如果这张旧卡是恶魔牌，或者带有恶魔魔气附魔，就把对应魔气返还到潘库宝盒。
 *
 * 性能处理：
 * 每帧只轻量比对主卡组 UUID；只有新卡出现、卡消失，或者 Modifier 增删时，才解析魔气信息。
 */
public class PanKuMasterDeckQiWatcherPatch {
    /** 当前正在监听的主卡组对象。换档、换角色或主卡组对象变化时会重建快照。 */
    private static CardGroup watchedDeck = null;

    /** 上一次记录的卡牌魔气快照：卡 UUID -> 这张卡代表的恶魔魔气卡 ID 列表。 */
    private static final HashMap<UUID, ArrayList<String>> qiByCardUuid = new HashMap<>();
    /** 每帧复用的 UUID 集合，避免主卡组监听反复创建 HashSet。 */
    private static final HashSet<UUID> currentUuids = new HashSet<>();
    /** 上一次处理完成时的主卡组数量。 */
    private static int watchedDeckSize = -1;
    /** 上一次处理完成时的主卡组 UUID 指纹。 */
    private static long watchedDeckFingerprint = 0L;

    /**
     * 挂到玩家 update，每帧做一次轻量主卡组 UUID 对比。
     */
    @SpirePatch(clz = AbstractPlayer.class, method = "update")
    public static class WatchMasterDeckPatch {
        /**
         * 玩家每帧更新后，检查主卡组里是否有旧卡消失。
         */
        public static void Postfix(AbstractPlayer __instance) {
            updateMasterDeckSnapshot(__instance);
        }
    }

    /**
     * 监听给卡牌添加 Modifier。
     *
     * 这样潘库附魔刚挂到卡上时，只刷新这一张卡的快照，不需要每帧重扫全部 Modifier。
     */
    @SpirePatch(clz = CardModifierManager.class, method = "addModifier", paramtypez = {AbstractCard.class, AbstractCardModifier.class})
    public static class AddModifierPatch {
        /**
         * Modifier 添加后，刷新这张卡的魔气快照。
         */
        public static void Postfix(AbstractCard card, AbstractCardModifier mod) {
            refreshCardSnapshot(card);
        }
    }

    /**
     * 监听移除某一个指定 Modifier。
     */
    @SpirePatch(clz = CardModifierManager.class, method = "removeSpecificModifier", paramtypez = {AbstractCard.class, AbstractCardModifier.class, boolean.class})
    public static class RemoveSpecificModifierPatch {
        /**
         * 指定 Modifier 移除后，刷新这张卡的魔气快照。
         */
        public static void Postfix(AbstractCard card, AbstractCardModifier mod, boolean includeInherent) {
            refreshCardSnapshot(card);
        }
    }

    /**
     * 监听按 ID 移除 Modifier。
     */
    @SpirePatch(clz = CardModifierManager.class, method = "removeModifiersById", paramtypez = {AbstractCard.class, String.class, boolean.class})
    public static class RemoveModifiersByIdPatch {
        /**
         * 同 ID Modifier 移除后，刷新这张卡的魔气快照。
         */
        public static void Postfix(AbstractCard card, String id, boolean includeInherent) {
            refreshCardSnapshot(card);
        }
    }

    /**
     * 监听清空卡牌所有 Modifier。
     */
    @SpirePatch(clz = CardModifierManager.class, method = "removeAllModifiers", paramtypez = {AbstractCard.class, boolean.class})
    public static class RemoveAllModifiersPatch {
        /**
         * 全部 Modifier 移除后，刷新这张卡的魔气快照。
         */
        public static void Postfix(AbstractCard card, boolean includeInherent) {
            refreshCardSnapshot(card);
        }
    }

    /**
     * 更新主卡组快照，并处理“旧卡 UUID 消失”的情况。
     *
     * 这里每帧只收集 UUID 集合；新卡第一次出现时才读取它的魔气信息。
     */
    private static void updateMasterDeckSnapshot(AbstractPlayer player) {
        if (player == null || player.masterDeck == null) {
            clearSnapshot();
            return;
        }

        if (watchedDeck != player.masterDeck) {
            watchedDeck = player.masterDeck;
            rebuildSnapshot(player.masterDeck);
            return;
        }

        int currentDeckSize = player.masterDeck.group.size();
        long currentDeckFingerprint = calculateDeckFingerprint(player.masterDeck);
        if (currentDeckSize == watchedDeckSize && currentDeckFingerprint == watchedDeckFingerprint) {
            return;
        }

        currentUuids.clear();
        for (AbstractCard card : player.masterDeck.group) {
            currentUuids.add(card.uuid);
            if (!qiByCardUuid.containsKey(card.uuid)) {
                qiByCardUuid.put(card.uuid, getQiCardIDs(card));
            }
        }

        Iterator<UUID> iterator = qiByCardUuid.keySet().iterator();
        while (iterator.hasNext()) {
            UUID knownUuid = iterator.next();
            if (!currentUuids.contains(knownUuid)) {
                returnLostQi(qiByCardUuid.get(knownUuid));
                iterator.remove();
            }
        }

        watchedDeckSize = currentDeckSize;
        watchedDeckFingerprint = currentDeckFingerprint;
    }

    /**
     * 重建整副主卡组的魔气快照。
     *
     * 只在监听对象变化、首次进入有效卡组等场景调用，避免读档或换卡组时误返还。
     */
    private static void rebuildSnapshot(CardGroup deck) {
        qiByCardUuid.clear();
        for (AbstractCard card : deck.group) {
            qiByCardUuid.put(card.uuid, getQiCardIDs(card));
        }
        watchedDeckSize = deck.group.size();
        watchedDeckFingerprint = calculateDeckFingerprint(deck);
    }

    /**
     * 清空监听状态。
     *
     * 玩家或主卡组为空时调用，防止旧档、旧角色的快照污染后续判断。
     */
    private static void clearSnapshot() {
        watchedDeck = null;
        qiByCardUuid.clear();
        currentUuids.clear();
        watchedDeckSize = -1;
        watchedDeckFingerprint = 0L;
    }

    /**
     * 刷新单张卡的魔气快照。
     *
     * 只处理当前主卡组里的卡；战斗临时卡、预览卡、奖励假卡不进入快照。
     */
    private static void refreshCardSnapshot(AbstractCard card) {
        if (card == null || watchedDeck == null || !watchedDeck.contains(card)) {
            return;
        }
        qiByCardUuid.put(card.uuid, getQiCardIDs(card));
    }

    /**
     * 计算主卡组 UUID 指纹。
     *
     * 只读取 UUID，不解析 Modifier；数量相同但卡被替换时也能触发完整差分。
     */
    private static long calculateDeckFingerprint(CardGroup deck) {
        long fingerprint = 1469598103934665603L;
        for (AbstractCard card : deck.group) {
            int uuidHash = card.uuid == null ? 0 : card.uuid.hashCode();
            fingerprint ^= uuidHash;
            fingerprint *= 1099511628211L;
        }
        return fingerprint;
    }

    /**
     * 提取一张卡代表的所有魔气卡 ID。
     *
     * 来源有两种：
     * 1. 卡本身是八大恶魔牌。
     * 2. 卡身上带有某个恶魔魔气 Modifier。
     */
    private static ArrayList<String> getQiCardIDs(AbstractCard card) {
        ArrayList<String> qiCardIDs = new ArrayList<>();
        addQiCardID(qiCardIDs, getQiCardIDByDemonCard(card));

        for (AbstractCardModifier modifier : CardModifierManager.modifiers(card)) {
            addQiCardID(qiCardIDs, getQiCardIDByModifier(modifier));
        }

        return qiCardIDs;
    }

    /**
     * 安全加入魔气 ID，避免同一张卡重复记录同一种魔气。
     */
    private static void addQiCardID(ArrayList<String> qiCardIDs, String qiCardID) {
        if (qiCardID != null && !qiCardIDs.contains(qiCardID)) {
            qiCardIDs.add(qiCardID);
        }
    }

    /**
     * 如果这张卡本身是八大恶魔牌，返回它自己的卡 ID；否则返回 null。
     */
    private static String getQiCardIDByDemonCard(AbstractCard card) {
        if (PanKuDemonQiHelper.createModifierByDemonCard(card) == null) {
            return null;
        }
        return card.cardID;
    }

    /**
     * 把恶魔魔气 Modifier 反查成对应的恶魔牌 ID。
     *
     * 潘库宝盒内部寄存的是恶魔牌 ID，所以返还时也统一用这个 ID。
     */
    private static String getQiCardIDByModifier(AbstractCardModifier modifier) {
        if (modifier instanceof FireDemonQiModifier) {
            return CardFireDemonQi.ID;
        } else if (modifier instanceof WaterDemonQiModifier) {
            return CardWaterDemonQi.ID;
        } else if (modifier instanceof WindDemonQiModifier) {
            return CardWindDemonQi.ID;
        } else if (modifier instanceof ThunderDemonQiModifier) {
            return CardThunderDemonQi.ID;
        } else if (modifier instanceof EarthDemonQiModifier) {
            return CardEarthDemonQi.ID;
        } else if (modifier instanceof MountainDemonQiModifier) {
            return CardMountainDemonQi.ID;
        } else if (modifier instanceof HeavenDemonQiModifier) {
            return CardHeavenDemonQi.ID;
        } else if (modifier instanceof MoonDemonQiModifier) {
            return CardMoonDemonQi.ID;
        }
        return null;
    }

    /**
     * 把消失卡快照中的魔气返还到潘库宝盒。
     *
     * 返还前会检查玩家当前是否仍拥有同元素魔气，避免重复寄存。
     */
    private static void returnLostQi(ArrayList<String> qiCardIDs) {
        if (qiCardIDs == null || qiCardIDs.isEmpty() || AbstractDungeon.player == null) {
            return;
        }

        AbstractRelic relic = AbstractDungeon.player.getRelic(PanKuBox.ID);
        if (!(relic instanceof PanKuBox)) {
            return;
        }

        PanKuBox panKuBox = (PanKuBox) relic;
        boolean returned = false;
        for (String qiCardID : qiCardIDs) {
            if (!isQiStillOwned(qiCardID)) {
                panKuBox.addStoredQi(qiCardID);
                returned = true;
            }
        }
        if (returned) {
            panKuBox.flash();
        }
    }

    /**
     * 判断指定魔气元素当前是否仍被玩家拥有。
     *
     * 这里复用潘库宝盒已有统计规则：主卡组恶魔牌、主卡组附魔、宝盒寄存都会算作拥有。
     */
    private static boolean isQiStillOwned(String qiCardID) {
        AbstractCard demonCard = PanKuDemonQiHelper.getDemonCardCopyByID(qiCardID);
        if (demonCard == null) {
            return false;
        }

        for (Class<?> ownedElement : PanKuBox.getOwnedDemonElements()) {
            if (ownedElement == demonCard.getClass()) {
                return true;
            }
        }
        return false;
    }
}
