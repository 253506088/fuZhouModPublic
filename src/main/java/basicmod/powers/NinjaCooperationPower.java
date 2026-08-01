package basicmod.powers;

import basicmod.helpers.NiJiaSupportHelper;
import com.megacrit.cardcrawl.core.AbstractCreature;

import static basicmod.BasicMod.makeID;

/**
 * 忍者协作能力。
 * 消耗堆每有一张尼嘉-忍者团，尼嘉-忍者团每段伤害提高。
 */
public class NinjaCooperationPower extends BasePower {
    public static final String POWER_ID = makeID("NinjaCooperationPower");
    /** 上次写进描述里的消耗堆尼嘉数量，-1 保证首次必刷新 */
    private int lastDescribedNiJiaCount = -1;

    /**
     * 构造函数。
     *
     * @param owner 持有者
     * @param amount 每张消耗堆尼嘉提供的伤害
     */
    public NinjaCooperationPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, owner, Math.max(1, amount));
        updateDescription();
    }

    /**
     * 堆叠层数。
     *
     * @param stackAmount 增加层数
     */
    @Override
    public void stackPower(int stackAmount) {
        if (stackAmount <= 0) {
            return;
        }
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        updateDescription();
    }

    /**
     * 每帧检查消耗堆尼嘉数量，变化时刷新描述。
     * 数量读取走缓存，张数没变时只是一次整数比较，开销可忽略。
     *
     * @param slotNum 能力图标槽位
     */
    @Override
    public void update(int slotNum) {
        super.update(slotNum);
        if (NiJiaSupportHelper.getNiJiaCountInExhaustPile() != this.lastDescribedNiJiaCount) {
            updateDescription();
        }
    }

    /**
     * 更新描述，实时展示消耗堆尼嘉数量与总加成。
     */
    @Override
    public void updateDescription() {
        int count = NiJiaSupportHelper.getNiJiaCountInExhaustPile();
        this.lastDescribedNiJiaCount = count;
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1]
                + count + DESCRIPTIONS[2] + (count * this.amount) + DESCRIPTIONS[3];
    }
}
