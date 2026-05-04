package basicmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

/**
 * 卯兔动作类：
 * 手动触发所有怪物的“回合开始”(中毒等)和“回合结束”(雷苏诅咒等)结算，
 * 然后跳过怪物的实际行动。
 */
public class RabbitAction extends AbstractGameAction {
    public RabbitAction() {
        this.actionType = ActionType.WAIT;
    }

    @Override
    public void update() {
        // 获取当前所有存活的怪物
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDeadOrEscaped()) {
                // 1. 触发中毒等回合开始结算 (由原版/Mod能力实现)
                // 这里调用 applyStartOfTurnPowers 会让每个 power 执行 atStartOfTurn()
                m.applyStartOfTurnPowers();

                // 2. 额外遍历能力，触发 atEndOfRound (主要为了雷苏诅咒在怪物回合结束执行斩杀)
                for (AbstractPower p : m.powers) {
                    p.atEndOfRound();
                }
            }
        }

        // 3. 执行跳过怪物回合的底层逻辑
        AbstractDungeon.getCurrRoom().skipMonsterTurn = true;

        this.isDone = true;
    }
}
