package basicmod.actions;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

/**
 * 艾克佐迪亚集齐后的循环斩杀 Action。
 *
 * 实现方式：
 * - 扫描全部怪物，对仍存活的逐只排队一个 InstantKillAction（走标准 damage(HP_LOSS)+die() 流程，
 *   能绕开 Buffer、伤害上限、无实体减伤等）。
 * - 所有 InstantKillAction 之后再排队一个新的 ExodiaKillAllAction 重新检查。
 * - 重复上述循环最多 MAX_RETRY 轮，用于处理"切形态复活"的怪物（如觉醒者一阶段）。
 * - 仍未杀死的怪物交给 ExodiaProtectionPower 在下回合开始时再补一刀。
 */
public class ExodiaKillAllAction extends AbstractGameAction {

    /** 单次集齐触发后允许的最大循环次数，防止异常情况下无限递归 */
    private static final int MAX_RETRY = 5;

    /** 当前剩余重试次数 */
    private final int retriesLeft;

    /** 默认入口：从最大重试次数开始 */
    public ExodiaKillAllAction() {
        this(MAX_RETRY);
    }

    /** 内部使用的构造器，递归创建下一轮 */
    private ExodiaKillAllAction(int retriesLeft) {
        this.actionType = ActionType.SPECIAL;
        this.duration = 0.0F;
        this.retriesLeft = retriesLeft;
    }

    @Override
    public void update() {
        AbstractRoom room = AbstractDungeon.getCurrRoom();
        if (room == null || room.monsters == null) {
            this.isDone = true;
            return;
        }

        // 全场基本都死了就结束
        if (room.monsters.areMonstersBasicallyDead()) {
            BasicMod.logger.info("【艾克佐迪亚·斩杀】全场怪物已基本死亡，停止循环。");
            this.isDone = true;
            return;
        }

        // 重试到顶就放弃，剩下的交给 ExodiaProtectionPower 在下回合再来
        if (retriesLeft <= 0) {
            BasicMod.logger.info("【艾克佐迪亚·斩杀】重试用尽，剩余怪物交给庇护 Power 下回合再补刀。");
            this.isDone = true;
            return;
        }

        // 重要：先 addToTop 下一轮自己，再 addToTop 各 InstantKill
        // addToTop 是 LIFO，所以最终队列前部顺序为：[InstantKill_n, ..., InstantKill_1, NextRoundCheck]
        // 即"先杀完所有人，再做下一轮检查"
        AbstractDungeon.actionManager.addToTop(new ExodiaKillAllAction(retriesLeft - 1));

        int killCount = 0;
        for (AbstractMonster mo : room.monsters.monsters) {
            if (mo == null) continue;
            if (mo.isDeadOrEscaped() || mo.isDying) continue;
            BasicMod.logger.info("【艾克佐迪亚·斩杀】排队 InstantKill：" + mo.name
                    + "（本轮剩余重试=" + retriesLeft + "）");
            AbstractDungeon.actionManager.addToTop(new InstantKillAction(mo));
            killCount++;
        }

        if (killCount == 0) {
            BasicMod.logger.info("【艾克佐迪亚·斩杀】本轮无可杀目标，停止循环。");
        }

        this.isDone = true;
    }
}
