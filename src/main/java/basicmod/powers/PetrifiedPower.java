package basicmod.powers;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static basicmod.BasicMod.makeID;

public class PetrifiedPower extends BasePower {
    public static final String POWER_ID = makeID(PetrifiedPower.class.getSimpleName());
    private static final PowerType TYPE = PowerType.DEBUFF;
    private static final boolean TURN_BASED = false;

    // amount 存储层数，每层增加1点力量
    // amount2 存储受到的伤害百分比提升数值（10或15）
    public PetrifiedPower(AbstractCreature owner, int amount, int percent) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
        this.amount2 = percent;
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        // 初始施加时直接给 1 点力量
        addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, 1), 1));
    }
 
    @Override
    public void atStartOfTurn() {
        if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            // amount 代表当前的加深层数（也是当前的增伤倍率和已增加的力量总数）
            if (this.amount < 10) {
                flash();
                // 每次自增只给 1 点力量
                addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, 1), 1));
                this.amount++;
                updateDescription();
            }
        }
    }
 
    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            // 受伤害提升 = 总层数 * 10/15%
            return damage * (1.0f + (float)(this.amount * this.amount2) / 100.0f);
        }
        return damage;
    }
 
    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        // 如果再次手动叠加，根据层数补齐力量（每多一层补充 1 点）
        addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, stackAmount), stackAmount));
        if (this.amount > 10) {
            this.amount = 10;
        }
    }
 
    @Override
    public void updateDescription() {
        int currentPercent = this.amount * this.amount2;
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + currentPercent + DESCRIPTIONS[2];
    }
}
