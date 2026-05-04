package basicmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

/**
 * 安全施加能力：
 * 目标已有同名能力时，避开原版 ApplyPowerAction 在遍历 powers 期间触发 onModifyPower 的崩溃点。
 */
public class SafeApplyPowerAction extends AbstractGameAction {
    private final AbstractPower powerToApply;

    public SafeApplyPowerAction(AbstractCreature target, AbstractCreature source, AbstractPower powerToApply, int stackAmount) {
        this.target = target;
        this.source = source;
        this.powerToApply = powerToApply;
        this.amount = stackAmount;
        this.actionType = ActionType.POWER;
    }

    @Override
    public void update() {
        if (this.target == null || this.target.isDeadOrEscaped() || this.powerToApply == null) {
            this.isDone = true;
            return;
        }

        AbstractPower existingPower = this.target.getPower(this.powerToApply.ID);
        if (existingPower == null) {
            addToTop(new ApplyPowerAction(this.target, this.source, this.powerToApply, this.amount));
            this.isDone = true;
            return;
        }

        existingPower.stackPower(this.amount);
        existingPower.flash();
        existingPower.updateDescription();
        AbstractDungeon.onModifyPower();
        this.isDone = true;
    }
}
