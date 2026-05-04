package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class SoakedPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("SoakedPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SoakedPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(POWER_ID, PowerType.DEBUFF, false, owner, source, amount);
        this.name = NAME;
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
        if (this.owner.hasPower(EarthBindPower.POWER_ID)) {
            flash();
            // 只要有地缚+潮湿，移除地缚，换成 1 层沉重泥沼 (潮湿不移除)
            addToTop(new com.megacrit.cardcrawl.actions.common.ApplyPowerAction(this.owner, this.source, new HeavyMirePower(this.owner, this.source, 1), 1));
            addToTop(new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(this.owner, this.source, EarthBindPower.POWER_ID));
        }
        
        // 新增：风势催化 (潮湿 + 风势 -> 冻伤)
        if (this.owner.hasPower(WindCatalystPower.POWER_ID)) {
            flash();
            int amount = this.amount;
            addToTop(new com.megacrit.cardcrawl.actions.common.ApplyPowerAction(this.owner, this.source, new FrostbitePower(this.owner, this.source, amount), amount));
            addToTop(new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(this.owner, this.source, this.ID));
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (!isPlayer) {
            addToBot(new ReducePowerAction(this.owner, this.owner, this, 1));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}
