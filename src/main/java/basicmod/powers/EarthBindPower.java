package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class EarthBindPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("EarthBindPower");

    public EarthBindPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(POWER_ID, PowerType.DEBUFF, false, owner, source, amount);
        this.canGoNegative = false;
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        checkFusion();
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        checkFusion();
    }

    private void checkFusion() {
        if (this.owner.hasPower(SoakedPower.POWER_ID)) {
            flash();
            // 只要有地缚+潮湿，移除地缚，换成 1 层沉重泥沼 (潮湿不移除)
            addToTop(new com.megacrit.cardcrawl.actions.common.ApplyPowerAction(this.owner, this.source, new HeavyMirePower(this.owner, this.source, 1), 1));
            addToTop(new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(this.owner, this.source, this.ID));
        }
    }

    @Override
    public void atStartOfTurn() {
        if (this.owner.currentHealth > 0) {
            flash();
            int damage = Math.max(2, this.owner.currentHealth / 8);
            addToBot(new LoseHPAction(this.owner, this.source, damage));
        }
        // 地缚不会自动递减层数
    }

    @Override
    public void updateDescription() {
        int damage = Math.max(2, this.owner.currentHealth / 8);
        this.description = DESCRIPTIONS[0] + damage + DESCRIPTIONS[1];
    }
}
