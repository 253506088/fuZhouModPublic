package basicmod.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;

import static basicmod.BasicMod.makeID;

/**
 * 手里剑能力。
 * 改变尼嘉-忍者团的攻击段数，多张只保留最高档位。
 */
public class ShurikenPower extends BasePower {
    public static final String POWER_ID = makeID("ShurikenPower");

    /**
     * 构造函数。
     *
     * @param owner 持有者
     * @param hitCount 攻击段数
     */
    public ShurikenPower(AbstractCreature owner, int hitCount) {
        super(POWER_ID, PowerType.BUFF, false, owner, owner, Math.max(2, hitCount), false);
        updateDescription();
    }

    /**
     * 堆叠时只保留更高段数。
     *
     * @param stackAmount 新档位的攻击段数
     */
    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount = Math.max(this.amount, stackAmount);
        updateDescription();
    }

    /**
     * 更新描述。
     */
    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}
