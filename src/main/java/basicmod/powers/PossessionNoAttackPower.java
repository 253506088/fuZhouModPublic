package basicmod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

import static basicmod.BasicMod.makeID;

/**
 * 夺舍禁止攻击能力。
 * 表示本回合不能再打出攻击牌，实际限制由 canUse 补丁执行。
 */
public class PossessionNoAttackPower extends BasePower {
    public static final String POWER_ID = makeID("PossessionNoAttackPower");

    /**
     * 构造函数。
     *
     * @param owner 持有者
     * @param amount 层数
     */
    public PossessionNoAttackPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.DEBUFF, true, owner, owner, Math.max(1, amount));
        updateDescription();
    }

    /**
     * 玩家回合结束时移除。
     *
     * @param isPlayer 是否为玩家回合结束
     */
    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }

    /**
     * 更新描述。
     */
    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
