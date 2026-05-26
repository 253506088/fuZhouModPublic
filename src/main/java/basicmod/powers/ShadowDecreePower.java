package basicmod.powers;

import basicmod.BasicMod;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static basicmod.BasicMod.makeID;

public class ShadowDecreePower extends BasePower implements OnReceivePowerPower {
    public static final String POWER_ID = makeID("ShadowDecreePower");

    public ShadowDecreePower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, owner, Math.max(1, amount));
        // amount 作为每回合固定扣减的力敏值（目前默认 1）。
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        if (stackAmount <= 0) {
            return;
        }
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        this.flash();
        BasicMod.logger.info("【黑影敕令】回合开始触发：失去力量={}，失去敏捷={}，失去生命=1", this.amount, this.amount);
        addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, -this.amount), -this.amount));
        addToBot(new ApplyPowerAction(this.owner, this.owner, new DexterityPower(this.owner, -this.amount), -this.amount));
        addToBot(new LoseHPAction(this.owner, this.owner, 1));
    }

    private boolean shouldBlockStrengthGain(AbstractPower power, AbstractCreature target) {
        return target == this.owner
                && power != null
                && StrengthPower.POWER_ID.equals(power.ID)
                && power.amount > 0;
    }

    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (shouldBlockStrengthGain(power, target)) {
            this.flash();
            String sourceName = source == null ? "未知来源" : source.name;
            BasicMod.logger.debug("【黑影敕令】已拦截正向力量：来源={}，目标={}，数值={}",
                    sourceName, target.name, power.amount);
            return false;
        }
        return true;
    }

    @Override
    public int onReceivePowerStacks(AbstractPower power, AbstractCreature target, AbstractCreature source, int stackAmount) {
        if (target == this.owner
                && power != null
                && StrengthPower.POWER_ID.equals(power.ID)
                && stackAmount > 0) {
            this.flash();
            String sourceName = source == null ? "未知来源" : source.name;
            BasicMod.logger.debug("【黑影敕令】已拦截力量叠层：来源={}，目标={}，叠层={}",
                    sourceName, target.name, stackAmount);
            return 0;
        }
        return stackAmount;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
    }
}
