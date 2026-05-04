package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class EarthPrisonPrepPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("EarthPrisonPrepPower");

    public EarthPrisonPrepPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
        this.canGoNegative = false;
        updateDescription();
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            this.amount--;
            if (this.amount <= 0) {
                flash();
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (!m.isDeadOrEscaped()) {
                        addToBot(new ApplyPowerAction(m, this.owner, new EarthBindPower(m, this.owner, 1), 1));
                    }
                }
                this.amount = 3;
            }
            updateDescription();
        }
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        // 如果受到了实际的生命值伤害 (damageAmount > 0)
        // 且伤害来源不是自己 (排除自残)
        if (damageAmount > 0 && info.owner != null && info.owner != this.owner) {
            flash();
            this.amount = 3;
            // 解除所有敌人的地缚和沉重泥沼状态
            for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                if (m.hasPower(EarthBindPower.POWER_ID)) {
                    addToBot(new RemoveSpecificPowerAction(m, this.owner, EarthBindPower.POWER_ID));
                }
                if (m.hasPower(HeavyMirePower.POWER_ID)) {
                    addToBot(new RemoveSpecificPowerAction(m, this.owner, HeavyMirePower.POWER_ID));
                }
            }
            updateDescription();
        }
        return damageAmount;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}
