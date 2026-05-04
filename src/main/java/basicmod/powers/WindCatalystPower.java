package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class WindCatalystPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("WindCatalystPower");
    private static final int MAX_BASE_STATUS_DAMAGE = 20;
    private static final int MAX_EVOLVED_STATUS_DAMAGE = 40;
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public WindCatalystPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(POWER_ID, PowerType.DEBUFF, true, owner, source, amount);
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
        if (this.owner.hasPower(SoakedPower.POWER_ID)) {
            flash();
            int soakedAmt = this.owner.getPower(SoakedPower.POWER_ID).amount;
            // 移除潮湿，转化为同等冻伤
            addToTop(new ApplyPowerAction(this.owner, this.source, new FrostbitePower(this.owner, this.source, soakedAmt), soakedAmt));
            addToTop(new RemoveSpecificPowerAction(this.owner, this.source, SoakedPower.POWER_ID));
        }
    }

    @Override
    public void atStartOfTurn() {
        if (this.owner instanceof AbstractMonster && !this.owner.isDeadOrEscaped()) {
            flash();
            // 催化加倍：再次结算伤害
            triggerExtraDamage();
        }
    }

    private void triggerExtraDamage() {
        // 基础火/冰 (1/8 伤害, 保底 2 点以一致)
        if (this.owner.hasPower(BurningPower.POWER_ID) || this.owner.hasPower(FrostbitePower.POWER_ID)) {
            int dmg = Math.min(MAX_BASE_STATUS_DAMAGE, Math.max(2, this.owner.currentHealth / 8));
            dmg = GrandMageBlessingPower.capDemonQiDamage(this.owner, BurningPower.POWER_ID, dmg, "风势额外触发灼烧/冻伤");
            addToBot(new LoseHPAction(this.owner, this.source, dmg));
        }

        // 进化火/冰 (1/5 伤害, 保底 5 点)
        if (this.owner.hasPower(BurningHeartPower.POWER_ID) || this.owner.hasPower(FrostHellPower.POWER_ID)) {
            int dmg = Math.min(MAX_EVOLVED_STATUS_DAMAGE, Math.max(5, this.owner.currentHealth / 5));
            dmg = GrandMageBlessingPower.capDemonQiDamage(this.owner, BurningHeartPower.POWER_ID, dmg, "风势额外触发灼心/冰狱");
            addToBot(new LoseHPAction(this.owner, this.source, dmg));
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (!isPlayer) {
            // 催化加速：额外减少 1 层异常层数 (由于原有 Power 也会自然减少 1 层，总计减少 2 层)
            accelerateDecay();
            
            // 风势本身自然减少 1 层
            addToBot(new ReducePowerAction(this.owner, this.owner, this, 1));
        }
    }

    private void accelerateDecay() {
        // 灼烧
        if (this.owner.hasPower(BurningPower.POWER_ID)) {
            addToBot(new ReducePowerAction(this.owner, this.owner, BurningPower.POWER_ID, 1));
        }
        // 冻伤
        if (this.owner.hasPower(FrostbitePower.POWER_ID)) {
            addToBot(new ReducePowerAction(this.owner, this.owner, FrostbitePower.POWER_ID, 1));
        }
        // 灼心
        if (this.owner.hasPower(BurningHeartPower.POWER_ID)) {
            addToBot(new ReducePowerAction(this.owner, this.owner, BurningHeartPower.POWER_ID, 1));
        }
        // 冰狱
        if (this.owner.hasPower(FrostHellPower.POWER_ID)) {
            addToBot(new ReducePowerAction(this.owner, this.owner, FrostHellPower.POWER_ID, 1));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
