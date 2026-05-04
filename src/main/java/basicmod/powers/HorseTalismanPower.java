package basicmod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

import static basicmod.BasicMod.makeID;

public class HorseTalismanPower extends BasePower {
    public static final String POWER_ID = makeID("HorseTalismanPower");

    public HorseTalismanPower(AbstractCreature owner) {
        super(POWER_ID, PowerType.BUFF, false, owner, 0);
        this.isTurnBased = false;
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.type == DamageInfo.DamageType.NORMAL && damageAmount > 0 && target != this.owner) {
            this.amount += damageAmount;
            this.updateDescription();
            this.flash();
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            healFromStoredDamage();
            addToBot(new RemoveSpecificPowerAction(owner, owner, this));
        }
    }

    @Override
    public void onVictory() {
        // Battle can end before end-of-turn callbacks (e.g. lethal this turn).
        healFromStoredDamage();
    }

    private void healFromStoredDamage() {
        int healAmount = this.amount / 2;
        if (healAmount > 0) {
            flash();
            owner.heal(healAmount);
            this.amount = 0;
            this.updateDescription();
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount / 2 + DESCRIPTIONS[1];
    }
}
