package basicmod.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;

import static basicmod.BasicMod.makeID;

/**
 * 恶魔法典能力。
 * 让黑影兵团重新受到力量、敏捷、虚弱、脆弱等常规修正影响。
 */
public class DemonCodexPower extends BasePower {
    public static final String POWER_ID = makeID("DemonCodexPower");

    /**
     * 构造函数。
     *
     * @param owner 持有者
     * @param amount 层数，仅用于显示
     */
    public DemonCodexPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, owner, Math.max(1, amount));
        updateDescription();
    }

    /**
     * 重复打出只叠显示层数，实际开关保持启用。
     *
     * @param stackAmount 增加层数
     */
    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount = Math.max(1, this.amount + Math.max(0, stackAmount));
        updateDescription();
    }

    /**
     * 更新描述。
     */
    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
