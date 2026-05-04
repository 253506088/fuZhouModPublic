package basicmod.powers;

import basicmod.BasicMod;
import basicmod.helpers.GrandMageDadHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class GrandMageCounterPouncePower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("GrandMageCounterPouncePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int extraHpFromCarry;
    private final int strengthGrowthByHp;

    public GrandMageCounterPouncePower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
        this.name = NAME;
        GrandMageDadHelper.DynamicHpBonusReport report = GrandMageDadHelper.buildDynamicHpBonusReport();
        this.extraHpFromCarry = report.totalBonus;
        this.strengthGrowthByHp = this.extraHpFromCarry / 30;
        log("【构造】创建【正气反扑】实例。owner=" + describeOwner()
                + "，amount=" + this.amount
                + "，额外生命=" + this.extraHpFromCarry
                + "，力量成长=floor(" + this.extraHpFromCarry + "/30)=" + this.strengthGrowthByHp
                + "，当前力量=" + getCurrentStrength() + "。");
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        log("【生效】已将【正气反扑】挂到目标身上。owner=" + describeOwner()
                + "，amount=" + this.amount
                + "，额外生命=" + this.extraHpFromCarry
                + "，力量成长=floor(" + this.extraHpFromCarry + "/30)=" + this.strengthGrowthByHp
                + "，当前力量=" + getCurrentStrength()
                + "。说明：该Power本体仅用于记录/展示，实际伤害增益由独立施加的力量Power承担。");
    }

    @Override
    public void stackPower(int stackAmount) {
        int before = this.amount;
        super.stackPower(stackAmount);
        log("【叠加】收到叠加请求。owner=" + describeOwner()
                + "，入参stackAmount=" + stackAmount
                + "，层数变化=" + before + " -> " + this.amount
                + "，当前力量=" + getCurrentStrength() + "。");
    }

    @Override
    public void onRemove() {
        log("【移除】【正气反扑】被移除。owner=" + describeOwner()
                + "，移除前amount=" + this.amount
                + "，当前力量=" + getCurrentStrength() + "。");
    }

    @Override
    public void updateDescription() {
        if (DESCRIPTIONS.length >= 4) {
            this.description = DESCRIPTIONS[0]
                    + DESCRIPTIONS[1] + this.extraHpFromCarry
                    + DESCRIPTIONS[2] + this.amount
                    + DESCRIPTIONS[3];
        } else {
            this.description = "本场额外生命为" + this.extraHpFromCarry
                    + "点；每30点额外生命折算1点力量，不足30点不计算；本场折算"
                    + this.strengthGrowthByHp + "点力量，所以正气反扑提供" + this.amount + "点力量。";
        }
    }

    private int getCurrentStrength() {
        if (this.owner == null) {
            return 0;
        }
        AbstractPower strength = this.owner.getPower(StrengthPower.POWER_ID);
        return strength == null ? 0 : strength.amount;
    }

    private String describeOwner() {
        if (this.owner == null) {
            return "空目标";
        }
        return this.owner.name + "/" + this.owner.id;
    }

    private void log(String message) {
        BasicMod.logger.info("[正气反扑] " + message);
    }
}
