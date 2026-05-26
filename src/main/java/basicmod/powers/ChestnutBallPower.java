package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

/**
 * 栗子球Power - 本回合完全免疫任何来源的伤害（包括HP_LOSS）
 * 回合结束时自动移除
 */
public class ChestnutBallPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("ChestnutBallPower");

    public ChestnutBallPower(AbstractCreature owner) {
        super(POWER_ID, PowerType.BUFF, true, owner, null, 1);
    }

    @Override
    public void updateDescription() {
        if (this.DESCRIPTIONS != null && this.DESCRIPTIONS.length >= 1) {
            this.description = DESCRIPTIONS[0];
        }
    }

    /**
     * 将所有攻击伤害归零
     */
    @Override
    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
        return 0.0F;
    }

    /**
     * 拦截生命值损失（如卡牌的"失去X点生命"效果）
     */
    @Override
    public int onLoseHp(int damageAmount) {
        return 0;
    }

    /**
     * 下一个玩家回合开始时移除（确保覆盖敌人回合的攻击）
     */
    @Override
    public void atStartOfTurn() {
        this.flash();
        this.addToBot(new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(this.owner, this.owner, this));
    }
}
