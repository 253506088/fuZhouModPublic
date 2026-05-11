package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

/**
 * 月之恶魔的“解除限制”能力：
 * 让玩家本回合内造成的恶魔异常伤害跳过所有单次伤害上限。
 */
public class MoonUncapPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("MoonUncapPower");

    /**
     * 创建一个持续到本回合结束的临时能力。
     * 这里把移除时机放在 atEndOfRound，
     * 这样它会覆盖当前回合接下来的敌方回合开始结算。
     *
     * @param owner 能力持有者，这里固定为玩家
     */
    public MoonUncapPower(AbstractCreature owner) {
        super(POWER_ID, PowerType.BUFF, true, owner, owner, 0);
        this.isTurnBased = true;
        updateDescription();
    }

    /**
     * 本回合结束后移除该能力，避免影响下一回合。
     */
    @Override
    public void atEndOfRound() {
        addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
    }

    /**
     * 刷新能力描述。
     */
    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
