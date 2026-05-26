package basicmod.cards.colorless;

import basicmod.BasicMod;
import basicmod.actions.ExodiaKillAllAction;
import basicmod.powers.ExodiaProtectionPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

/**
 * 艾克佐迪亚集齐检测工具类。
 *
 * 检测玩家手牌中是否集齐艾克佐迪亚 5 件套，集齐则：
 * 1) 把 5 张部件标记为保留（仅这一次，不影响 ExodiaSealed 升级版自带的 retain 机制）；
 * 2) 给玩家挂上 ExodiaProtectionPower 兜底保护（999 格挡 + 无实体）；
 * 3) 排队 ExodiaKillAllAction 循环补刀，直接结束战斗。
 *
 * 检测时机覆盖：
 * - 5 张部件卡的 triggerWhenDrawn / triggerOnOtherCardPlayed / triggerAtStartOfTurn / use
 * - ExodiaCheckPatch 每帧动作完成后的兜底巡逻
 */
public class ExodiaHelper {

    /** 防止重复触发：每场战斗只触发一次 */
    private static boolean triggered = false;

    /** 5 张 Exodia 部件的 cardID，用于查询 */
    private static final String[] EXODIA_PART_IDS = new String[]{
            ExodiaSealed.ID,
            ExodiaRightArm.ID,
            ExodiaLeftArm.ID,
            ExodiaRightLeg.ID,
            ExodiaLeftLeg.ID
    };

    /**
     * 每场战斗结束时重置触发标记。
     */
    public static void reset() {
        triggered = false;
    }

    /**
     * 检测手牌是否集齐艾克佐迪亚 5 件套，集齐则发动胜利逻辑。
     * 这个方法可以被频繁调用（每帧），内部已做触发标记和状态校验。
     */
    public static void checkExodiaComplete() {
        if (triggered) return;

        AbstractPlayer p = AbstractDungeon.player;
        if (p == null || p.hand == null) return;
        if (p.isDead) return;

        AbstractRoom room = AbstractDungeon.getCurrRoom();
        if (room == null || room.monsters == null) return;
        // 战斗已结束或怪物已基本死光，不再触发
        if (room.monsters.areMonstersBasicallyDead()) return;

        if (!isExodiaCompleteInHand(p)) {
            return;
        }

        triggered = true;
        BasicMod.logger.info("【艾克佐迪亚】手牌集齐 5 件套！锁定部件 + 上庇护 + 循环斩杀。");

        // 1. 锁定手牌中的 5 张部件，避免回合结束时被弃掉
        lockExodiaPartsInHand(p);

        // 2. 挂上庇护 Power 兜底（覆盖觉醒者复活、腐化心脏伤害上限、未知 mod 怪等所有场景）
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(
                p, p, new ExodiaProtectionPower(p), 1));

        // 3. 排队循环补刀（绕开 Buffer / 伤害上限 / 无实体减伤）
        AbstractDungeon.actionManager.addToBottom(new ExodiaKillAllAction());
    }

    /**
     * 检测手牌中是否同时包含全部 5 张 Exodia 部件。
     */
    private static boolean isExodiaCompleteInHand(AbstractPlayer p) {
        boolean hasMain = false;
        boolean hasRightArm = false;
        boolean hasLeftArm = false;
        boolean hasRightLeg = false;
        boolean hasLeftLeg = false;

        for (AbstractCard card : p.hand.group) {
            if (card == null || card.cardID == null) continue;
            if (card.cardID.equals(ExodiaSealed.ID)) hasMain = true;
            else if (card.cardID.equals(ExodiaRightArm.ID)) hasRightArm = true;
            else if (card.cardID.equals(ExodiaLeftArm.ID)) hasLeftArm = true;
            else if (card.cardID.equals(ExodiaRightLeg.ID)) hasRightLeg = true;
            else if (card.cardID.equals(ExodiaLeftLeg.ID)) hasLeftLeg = true;
        }

        return hasMain && hasRightArm && hasLeftArm && hasRightLeg && hasLeftLeg;
    }

    /**
     * 把当前手牌中的 5 张 Exodia 部件标记为保留。
     * 注意：这里只在集齐瞬间执行一次，不会去把非 Exodia 卡的 retain 改 false，
     * 也不会把 Exodia 卡的 retain 改回 false，所以 ExodiaSealed 升级版自带的保留属性完全不受影响。
     */
    private static void lockExodiaPartsInHand(AbstractPlayer p) {
        for (AbstractCard card : p.hand.group) {
            if (card == null || card.cardID == null) continue;
            if (isExodiaPart(card.cardID)) {
                card.selfRetain = true;
                card.retain = true;
            }
        }
    }

    /** 判断给定 cardID 是否是 Exodia 5 件套之一 */
    private static boolean isExodiaPart(String cardID) {
        for (String id : EXODIA_PART_IDS) {
            if (id.equals(cardID)) return true;
        }
        return false;
    }
}
