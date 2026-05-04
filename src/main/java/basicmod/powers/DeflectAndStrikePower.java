package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class DeflectAndStrikePower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID(DeflectAndStrikePower.class.getSimpleName());

    public DeflectAndStrikePower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, true, owner, amount);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        // 条件：物理攻击（NORMAL）、有攻击来源、目标不是自己（即敌人打我）、伤害被完全格挡（damageAmount <= 0）、计划伤害大于 0
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null && info.owner != this.owner && damageAmount <= 0 && info.output > 0) {
            this.flash();
            
            // 对攻击者造成其意图造成的伤害（info.output），伤害类型设为 THORNS 以防无限嵌套（或根据需求设为 NORMAL）
            // 在 STS 中，反弹类通常使用 THORNS，且不会再次触发反弹逻辑。
            addToTop(new DamageAction(info.owner, new DamageInfo(this.owner, info.output, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        }
        return damageAmount;
    }

    @Override
    public void atEndOfRound() {
        // 回合结束时移除此效果
        addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
