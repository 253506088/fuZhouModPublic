package basicmod.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;

import static basicmod.BasicMod.makeID;

/**
 * 双棍能力。
 * 让尼嘉-忍者团从影噬获得的伤害加成变为三倍。
 */
public class DoubleSticksPower extends BasePower {
    public static final String POWER_ID = makeID("DoubleSticksPower");

    /**
     * 构造函数。
     *
     * @param owner 持有者
     * @param amount 层数，仅用于显示
     */
    public DoubleSticksPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, owner, Math.max(1, amount));
        updateDescription();
    }

    /**
     * 重复打出只刷新显示，不继续提高倍率。
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
