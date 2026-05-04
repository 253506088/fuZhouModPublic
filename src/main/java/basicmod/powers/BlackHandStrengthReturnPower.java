package basicmod.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static basicmod.BasicMod.makeID;

public class BlackHandStrengthReturnPower extends BasePower {
    public static final String POWER_ID = makeID("BlackHandStrengthReturnPower");

    public BlackHandStrengthReturnPower(AbstractCreature owner, int turns, int returnStrength) {
        super(POWER_ID, PowerType.BUFF, true, owner, owner, Math.max(1, turns));
        this.amount2 = Math.max(1, returnStrength);
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount = Math.max(2, this.amount);
        this.amount2 += Math.max(0, stackAmount);
        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        this.amount--;
        if (this.amount <= 0) {
            flash();
            addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, this.amount2), this.amount2));
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
            return;
        }
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.amount2 + DESCRIPTIONS[2];
    }
}
