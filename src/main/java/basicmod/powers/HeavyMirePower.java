package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class HeavyMirePower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("HeavyMirePower");
    private static final int MAX_SINGLE_DAMAGE = 40;

    public HeavyMirePower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(POWER_ID, PowerType.DEBUFF, false, owner, source, amount);
        this.canGoNegative = false;
        this.amount2 = 0;
        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        if (this.owner.currentHealth > 0) {
            flash();
            int damage = Math.min(MAX_SINGLE_DAMAGE, Math.max(5, this.owner.currentHealth / 5));
            damage = GrandMageBlessingPower.capDemonQiDamage(this.owner, POWER_ID, damage, "沉重泥沼回合开始");
            addToBot(new LoseHPAction(this.owner, this.source, damage));

            addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, -2), -2));
            addToBot(new ApplyPowerAction(this.owner, this.owner, new DexterityPower(this.owner, -2), -2));

            this.amount2 += 2;
            updateDescription();
        }
    }

    @Override
    public void onRemove() {
        if (this.amount2 > 0 && this.owner.currentHealth > 0 && !this.owner.isDeadOrEscaped()) {
            flash();
            addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, this.amount2), this.amount2));
            addToBot(new ApplyPowerAction(this.owner, this.owner, new DexterityPower(this.owner, this.amount2), this.amount2));
        }
    }

    @Override
    public void updateDescription() {
        int damage = 5;
        if (this.owner != null) {
            damage = Math.min(MAX_SINGLE_DAMAGE, Math.max(5, this.owner.currentHealth / 5));
        }
        this.description = DESCRIPTIONS[0] + damage + DESCRIPTIONS[1] + this.amount2 + DESCRIPTIONS[2];
    }
}
