package basicmod.relics;

import basemod.abstracts.CustomSavable;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basicmod.BasicMod;
import basicmod.cards.demons.CardEightDemonPossession;
import basicmod.cards.demons.CardEarthDemonQi;
import basicmod.cards.demons.CardFireDemonQi;
import basicmod.cards.demons.CardHeavenDemonQi;
import basicmod.cards.demons.CardMoonDemonQi;
import basicmod.cards.demons.CardMountainDemonQi;
import basicmod.cards.demons.CardThunderDemonQi;
import basicmod.cards.demons.CardWaterDemonQi;
import basicmod.cards.demons.CardWindDemonQi;
import basicmod.enums.CustomTags;
import basicmod.modifiers.demons.AbstractDemonQiModifier;
import basicmod.modifiers.demons.EarthDemonQiModifier;
import basicmod.modifiers.demons.FireDemonQiModifier;
import basicmod.modifiers.demons.HeavenDemonQiModifier;
import basicmod.modifiers.demons.MoonDemonQiModifier;
import basicmod.modifiers.demons.MountainDemonQiModifier;
import basicmod.modifiers.demons.ThunderDemonQiModifier;
import basicmod.modifiers.demons.WaterDemonQiModifier;
import basicmod.modifiers.demons.WindDemonQiModifier;
import basicmod.ui.PanKuEnchantOption;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.lang.reflect.Field;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PanKuBox extends BaseRelic implements CustomSavable<PanKuBox.PanKuSaveData> {
    public static final String ID = BasicMod.makeID(PanKuBox.class.getSimpleName());

    // 寄宿在宝盒中的魔气（存储恶魔卡 cardID）
    private final ArrayList<String> storedQiCardIDs = new ArrayList<>();
    // 奖励页SL恢复用：记录当前节点尚未领取的潘库奖励
    private String pendingRewardQiCardID = null;
    private Integer pendingRewardNodeEncoded = null;
    private boolean hasGrantedCompletionCard = false;
    private boolean completionCardQueued = false;

    public static class PanKuSaveData implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<Integer> nodeData = new ArrayList<>();
        public List<String> storedQiIDs = new ArrayList<>();
        public String pendingRewardQiID = null;
        public Integer pendingRewardNode = null;
        public boolean completionCardGranted = false;
    }

    public PanKuBox() {
        super(ID, "PanKuBox", RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void onEquip() {
        super.onEquip();
        updateCounter();
        // 获得遗物时立刻执行地图标记扫描（中途获取模式）
        markMapNodes(false);
    }

    /**
     * 将筛选房间并标记为恶魔之门的逻辑抽离为一个静态公共方法。
     * 这样不仅可以在楼层最初生成地图时触发，
     * 还能在玩家中途于宝箱或事件获得宝盒时，立刻对前路地图进行标记。
     *
     * @param fromGenerateMap 是否是由生成新楼层地图时触发
     */
    public static void markMapNodes(boolean fromGenerateMap) {
        if (AbstractDungeon.player == null) return;

        AbstractRelic panKuBox = AbstractDungeon.player.getRelic(PanKuBox.ID);
        if (panKuBox instanceof PanKuBox) {
            ((PanKuBox) panKuBox).updateCounter();
            int needed = panKuBox.counter;
            if (needed > 0 && AbstractDungeon.map != null) {
                java.util.List<MapRoomNode> elites = new java.util.ArrayList<>();
                java.util.List<MapRoomNode> monsters = new java.util.ArrayList<>();

                // 核心安全逻辑：判断玩家现在的层数，避免修改到身后或者当前所在格子的房间
                int currentY = -1;
                // 当处于新层生成时，或者尚未选择第一个房间（如在涅奥处拿到），强行从底下扫描（y = -1）。
                // 如果是正常楼层中途获得，记录当前格子的Y坐标。
                if (!fromGenerateMap && AbstractDungeon.getCurrMapNode() != null && AbstractDungeon.firstRoomChosen) {
                    currentY = AbstractDungeon.getCurrMapNode().y;
                }

                // 检索地图上的所有精英和普通房间
                for (java.util.ArrayList<MapRoomNode> row : AbstractDungeon.map) {
                    for (MapRoomNode node : row) {
                        // 扣除掉原本就已经被标记走的份额
                        if (MapNodeDemonPortalField.isDemonPortal.get(node)) {
                            needed--;
                        } else if (node.hasEdges() && node.y > currentY && node.y < 14) {
                            if (node.getRoom() instanceof com.megacrit.cardcrawl.rooms.MonsterRoomElite) {
                                elites.add(node);
                            } else if (node.getRoom() instanceof com.megacrit.cardcrawl.rooms.MonsterRoom) {
                                monsters.add(node);
                            }
                        }
                    }
                }

                if (needed <= 0) return; // 容量足够时退出计算

                // 将剩余的备选精英房和怪物房分别打乱，以达到随机分配的效果
                java.util.Collections.shuffle(elites, new java.util.Random(AbstractDungeon.mapRng.randomLong()));
                java.util.Collections.shuffle(monsters, new java.util.Random(AbstractDungeon.mapRng.randomLong()));

                int marked = 0;
                // 第一梯队：优先挑选中途剩余的精英房
                for (MapRoomNode node : elites) {
                    if (marked < needed) {
                        MapNodeDemonPortalField.isDemonPortal.set(node, true);
                        marked++;
                    }
                }
                // 第二梯队：若凑不够名额，由普通怪物房强行替补变身精英房
                for (MapRoomNode node : monsters) {
                    if (marked < needed) {
                        MapNodeDemonPortalField.isDemonPortal.set(node, true);
                        node.setRoom(new com.megacrit.cardcrawl.rooms.MonsterRoomElite());
                        marked++;
                    }
                }
            }
        }
    }

    @Override
    public void onEnterRoom(com.megacrit.cardcrawl.rooms.AbstractRoom room) {
        // 更新层数以防玩家在此期间获取了恶魔卡
        updateCounter();
    }

    @Override
    public void addCampfireOption(ArrayList<AbstractCampfireOption> options) {
        if (hasStoredQi()) {
            options.add(new PanKuEnchantOption());
        }
    }

    public List<String> getStoredQiCardIDs() {
        return new ArrayList<>(storedQiCardIDs);
    }

    public boolean hasStoredQi() {
        return !storedQiCardIDs.isEmpty();
    }

    public void addStoredQi(String demonCardID) {
        if (demonCardID == null || storedQiCardIDs.contains(demonCardID)) {
            return;
        }
        storedQiCardIDs.add(demonCardID);
        updateCounter();
    }

    public boolean removeStoredQi(String demonCardID) {
        boolean removed = storedQiCardIDs.remove(demonCardID);
        if (removed) {
            updateCounter();
        }
        return removed;
    }

    private Integer getCurrentNodeEncoded() {
        if (AbstractDungeon.getCurrMapNode() == null) {
            return null;
        }
        return AbstractDungeon.getCurrMapNode().y * 100 + AbstractDungeon.getCurrMapNode().x;
    }

    private boolean isPendingRewardForCurrentNode() {
        Integer current = getCurrentNodeEncoded();
        return pendingRewardQiCardID != null && pendingRewardNodeEncoded != null && pendingRewardNodeEncoded.equals(current);
    }

    public void setPendingReward(String demonCardID) {
        this.pendingRewardQiCardID = demonCardID;
        this.pendingRewardNodeEncoded = getCurrentNodeEncoded();
    }

    public void clearPendingReward() {
        this.pendingRewardQiCardID = null;
        this.pendingRewardNodeEncoded = null;
    }

    private boolean hasActivePanKuRewardInCurrentRoom() {
        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().rewards != null) {
            for (com.megacrit.cardcrawl.rewards.RewardItem reward : AbstractDungeon.getCurrRoom().rewards) {
                if (reward.type == PanKuRewardItemPatch.PANKU_REWARD_TYPE && !reward.isDone && !reward.ignoreReward) {
                    return true;
                }
            }
        }
        if (AbstractDungeon.combatRewardScreen != null && AbstractDungeon.combatRewardScreen.rewards != null) {
            for (com.megacrit.cardcrawl.rewards.RewardItem reward : AbstractDungeon.combatRewardScreen.rewards) {
                if (reward.type == PanKuRewardItemPatch.PANKU_REWARD_TYPE && !reward.isDone && !reward.ignoreReward) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判定当前战斗是否属于“真正胜利”：
     * 1) 战斗已结束；
     * 2) 不是烟雾弹逃跑；
     * 3) 当前怪物组已视为死亡/离场。
     */
    public static boolean isTrueCombatVictory() {
        if (AbstractDungeon.getCurrRoom() == null || !AbstractDungeon.getCurrRoom().isBattleOver) {
            return false;
        }
        if (isSmokeBombEscapeInCurrentRoom()) {
            return false;
        }
        return AbstractDungeon.getCurrRoom().monsters != null
                && AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead();
    }

    /**
     * 通过反射读取房间 smoked 标记，兼容字段可见性差异。
     * smoked=true 时表示本场战斗通过烟雾弹逃跑结束，不应视为战胜。
     */
    private static boolean isSmokeBombEscapeInCurrentRoom() {
        if (AbstractDungeon.getCurrRoom() == null) {
            return false;
        }
        Class<?> clazz = AbstractDungeon.getCurrRoom().getClass();
        while (clazz != null) {
            try {
                Field smokedField = clazz.getDeclaredField("smoked");
                smokedField.setAccessible(true);
                Object value = smokedField.get(AbstractDungeon.getCurrRoom());
                if (value instanceof Boolean) {
                    return (Boolean) value;
                }
                return false;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                BasicMod.logger.warn("【潘库宝盒】读取房间smoked标记失败，按非逃跑处理。", e);
                return false;
            }
        }
        return false;
    }

    public void ensurePendingRewardPresentInCurrentRoom() {
        if (!isPendingRewardForCurrentNode() || hasActivePanKuRewardInCurrentRoom()) {
            return;
        }
        if (AbstractDungeon.getCurrRoom() == null || AbstractDungeon.getCurrRoom().rewards == null) {
            return;
        }

        AbstractCard demonCard = PanKuDemonQiHelper.getDemonCardCopyByID(pendingRewardQiCardID);
        AbstractDemonQiModifier qiMod = PanKuDemonQiHelper.createModifierByDemonCard(demonCard);
        if (demonCard == null || qiMod == null) {
            clearPendingReward();
            return;
        }

        AbstractCard enchantPreview = PanKuDemonQiHelper.createRewardEnchantPreviewCard(demonCard);
        AbstractCard storePreview = PanKuDemonQiHelper.createRewardStorePreviewCard(demonCard);
        PanKuRewardItem restored = new PanKuRewardItem(demonCard, enchantPreview, storePreview, qiMod);
        AbstractDungeon.getCurrRoom().rewards.add(restored);
        if (AbstractDungeon.combatRewardScreen != null && AbstractDungeon.combatRewardScreen.rewards != null) {
            if (!AbstractDungeon.combatRewardScreen.rewards.contains(restored)) {
                AbstractDungeon.combatRewardScreen.rewards.add(restored);
            }
        }
        if (AbstractDungeon.combatRewardScreen != null) {
            AbstractDungeon.combatRewardScreen.positionRewards();
        }
    }

    public static java.util.List<Class<?>> getOwnedDemonElements() {
        java.util.List<Class<?>> owned = new java.util.ArrayList<>();
        if (AbstractDungeon.player != null && AbstractDungeon.player.masterDeck != null) {
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c.hasTag(CustomTags.EIGHT_DEMONS)) {
                    if (!owned.contains(c.getClass())) {
                        owned.add(c.getClass());
                    }
                }
                for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
                    if (mod instanceof FireDemonQiModifier) {
                        if (!owned.contains(CardFireDemonQi.class)) owned.add(CardFireDemonQi.class);
                    }
                    if (mod instanceof WaterDemonQiModifier) {
                        if (!owned.contains(CardWaterDemonQi.class)) owned.add(CardWaterDemonQi.class);
                    }
                    if (mod instanceof WindDemonQiModifier) {
                        if (!owned.contains(CardWindDemonQi.class)) owned.add(CardWindDemonQi.class);
                    }
                    if (mod instanceof ThunderDemonQiModifier) {
                        if (!owned.contains(CardThunderDemonQi.class)) owned.add(CardThunderDemonQi.class);
                    }
                    if (mod instanceof EarthDemonQiModifier) {
                        if (!owned.contains(CardEarthDemonQi.class)) owned.add(CardEarthDemonQi.class);
                    }
                    if (mod instanceof MountainDemonQiModifier) {
                        if (!owned.contains(CardMountainDemonQi.class)) owned.add(CardMountainDemonQi.class);
                    }
                    if (mod instanceof HeavenDemonQiModifier) {
                        if (!owned.contains(CardHeavenDemonQi.class)) owned.add(CardHeavenDemonQi.class);
                    }
                    if (mod instanceof MoonDemonQiModifier) {
                        if (!owned.contains(CardMoonDemonQi.class)) owned.add(CardMoonDemonQi.class);
                    }
                }
            }
        }

        if (AbstractDungeon.player != null) {
            AbstractRelic relic = AbstractDungeon.player.getRelic(PanKuBox.ID);
            if (relic instanceof PanKuBox) {
                PanKuBox panKuBox = (PanKuBox) relic;
                for (String storedCardID : panKuBox.storedQiCardIDs) {
                    AbstractCard demonCard = CardLibrary.getCard(storedCardID);
                    if (demonCard != null && !owned.contains(demonCard.getClass())) {
                        owned.add(demonCard.getClass());
                    }
                }
            }
        }
        return owned;
    }

    public void updateCounter() {
        updateCounter(getOwnedDemonElements().size());
    }

    public void updateCounterWithPendingDemon(AbstractCard pendingDemonCard) {
        updateCounter(getOwnedDemonElementCountWithPendingDemon(pendingDemonCard));
    }

    private int getOwnedDemonElementCountWithPendingDemon(AbstractCard pendingDemonCard) {
        java.util.List<Class<?>> owned = getOwnedDemonElements();
        if (pendingDemonCard != null && pendingDemonCard.hasTag(CustomTags.EIGHT_DEMONS) && !owned.contains(pendingDemonCard.getClass())) {
            return owned.size() + 1;
        }
        return owned.size();
    }

    private void updateCounter(int ownedDemonElementCount) {
        this.counter = Math.max(0, 8 - ownedDemonElementCount);
        this.description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        initializeTips();
        checkAndGrantCompletionCard();
    }

    private void checkAndGrantCompletionCard() {
        if (this.counter > 0 || AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) {
            return;
        }
        if (hasCompletionCardInMasterDeck()) {
            hasGrantedCompletionCard = true;
            completionCardQueued = false;
            return;
        }
        if (completionCardQueued) {
            return;
        }

        grantCompletionCard();
    }

    private void grantCompletionCard() {
        if (!hasCompletionCardInMasterDeck()) {
            AbstractDungeon.topLevelEffectsQueue.add(new ShowCardAndObtainEffect(
                    new CardEightDemonPossession(),
                    Settings.WIDTH / 2.0f,
                    Settings.HEIGHT / 2.0f));
            completionCardQueued = true;
        }
        hasGrantedCompletionCard = true;
    }

    private boolean hasCompletionCardInMasterDeck() {
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (CardEightDemonPossession.ID.equals(card.cardID)) {
                return true;
            }
        }
        return false;
    }

    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:PanKuUI");
    private static final UIStrings qiNames = CardCrawlGame.languagePack.getUIString("fuZhouMod:PanKuQiNames");

    private String getQiShortName(String id) {
        if (CardHeavenDemonQi.ID.equals(id)) return qiNames.TEXT[0];
        if (CardEarthDemonQi.ID.equals(id)) return qiNames.TEXT[1];
        if (CardWindDemonQi.ID.equals(id)) return qiNames.TEXT[2];
        if (CardThunderDemonQi.ID.equals(id)) return qiNames.TEXT[3];
        if (CardWaterDemonQi.ID.equals(id)) return qiNames.TEXT[4];
        if (CardFireDemonQi.ID.equals(id)) return qiNames.TEXT[5];
        if (CardMountainDemonQi.ID.equals(id)) return qiNames.TEXT[6];
        if (CardMoonDemonQi.ID.equals(id)) return qiNames.TEXT[7];
        return "?";
    }

    @Override
    public void onVictory() {
        if (!isTrueCombatVictory()) {
            clearPendingReward();
            updateCounter();
            return;
        }
        if (AbstractDungeon.getCurrMapNode() != null) {
            if (MapNodeDemonPortalField.isDemonPortal.get(AbstractDungeon.getCurrMapNode())) {
                flash();

                // 奖励页SL后回到同一节点时，优先恢复已记录的待领取潘库奖励，避免“奖励丢失”
                if (isPendingRewardForCurrentNode()) {
                    ensurePendingRewardPresentInCurrentRoom();
                    updateCounter();
                    return;
                }

                java.util.List<Class<?>> owned = getOwnedDemonElements();
                java.util.ArrayList<AbstractCard> availableDemons = new java.util.ArrayList<>();
                for (AbstractCard c : CardLibrary.getAllCards()) {
                    if (c.hasTag(CustomTags.EIGHT_DEMONS)) {
                        if (!owned.contains(c.getClass())) {
                            availableDemons.add(c.makeCopy());
                        }
                    }
                }

                if (!availableDemons.isEmpty()) {
                    AbstractCard demonCard = availableDemons.get(AbstractDungeon.cardRng.random(availableDemons.size() - 1));
                    AbstractDemonQiModifier qiMod = PanKuDemonQiHelper.createModifierByDemonCard(demonCard);
                    if (qiMod != null) {
                        AbstractCard enchantPreview = PanKuDemonQiHelper.createRewardEnchantPreviewCard(demonCard);
                        AbstractCard storePreview = PanKuDemonQiHelper.createRewardStorePreviewCard(demonCard);
                        AbstractDungeon.getCurrRoom().rewards.add(new PanKuRewardItem(demonCard, enchantPreview, storePreview, qiMod));
                        setPendingReward(demonCard.cardID);
                    }
                } else {
                    clearPendingReward();
                }

                updateCounter();
            }
        }
    }

    @Override
    public PanKuSaveData onSave() {
        PanKuSaveData data = new PanKuSaveData();

        if (AbstractDungeon.map != null) {
            for (ArrayList<MapRoomNode> row : AbstractDungeon.map) {
                for (MapRoomNode node : row) {
                    if (MapNodeDemonPortalField.isDemonPortal.get(node)) {
                        data.nodeData.add(node.y * 100 + node.x);
                    }
                }
            }
        }

        data.storedQiIDs.addAll(storedQiCardIDs);
        data.pendingRewardQiID = pendingRewardQiCardID;
        data.pendingRewardNode = pendingRewardNodeEncoded;
        data.completionCardGranted = hasGrantedCompletionCard;
        return data;
    }

    @Override
    public void onLoad(PanKuSaveData data) {
        storedQiCardIDs.clear();
        if (data != null && data.storedQiIDs != null) {
            for (String storedQiID : data.storedQiIDs) {
                if (storedQiID != null && !storedQiCardIDs.contains(storedQiID)) {
                    storedQiCardIDs.add(storedQiID);
                }
            }
        }
        pendingRewardQiCardID = data == null ? null : data.pendingRewardQiID;
        pendingRewardNodeEncoded = data == null ? null : data.pendingRewardNode;
        hasGrantedCompletionCard = data != null && data.completionCardGranted;

        if (data != null && data.nodeData != null && AbstractDungeon.map != null) {
            for (Integer encoded : data.nodeData) {
                int x = encoded % 100;
                int y = encoded / 100;
                if (y >= 0 && y < AbstractDungeon.map.size() && x >= 0 && x < AbstractDungeon.map.get(y).size()) {
                    MapRoomNode node = AbstractDungeon.map.get(y).get(x);
                    MapNodeDemonPortalField.isDemonPortal.set(node, true);
                    if (!(node.getRoom() instanceof MonsterRoomElite)) {
                        // 若被替换的节点正好是玩家当前/即将进入的节点，原版已经在它上面调用过 onPlayerEntry()，
                        // 直接换新 MonsterRoomElite 会丢掉 monsters 导致 AbstractRoom.update NPE，必须补一次 onPlayerEntry
                        boolean isCurrentRoom = (AbstractDungeon.getCurrMapNode() == node)
                                || (AbstractDungeon.nextRoom == node);
                        node.setRoom(new MonsterRoomElite());
                        if (isCurrentRoom) {
                            node.getRoom().onPlayerEntry();
                        }
                    }
                }
            }
        }

        updateCounter();
    }

    @Override
    public String getUpdatedDescription() {
        if (storedQiCardIDs == null || storedQiCardIDs.isEmpty()) {
            return DESCRIPTIONS[0];
        }
        StringBuilder sb = new StringBuilder(DESCRIPTIONS[0]);
        sb.append(uiStrings.TEXT[3]);
        for (int i = 0; i < storedQiCardIDs.size(); i++) {
            if (i > 0) {
                sb.append(uiStrings.TEXT[4]);
            }
            // 中文分隔符不带空格时，后续 #b 可能被并入同一token；必要时补空格，确保每个词都能被高亮解析
            if (sb.length() > 0 && !Character.isWhitespace(sb.charAt(sb.length() - 1))) {
                sb.append(' ');
            }
            sb.append("#b").append(getQiShortName(storedQiCardIDs.get(i)));
        }
        return sb.toString();
    }
}
