package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.core.AbstractCreature;

import static basicmod.BasicMod.makeID;

public class DominionPower extends BasePower {
    public static final String POWER_ID = makeID("DominionPower");

    public DominionPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, owner, Math.max(0, amount));
        // 构造时直接刷新描述，保证卡面能即时显示层数。
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        BasicMod.logger.info("【影蚀】首次获得，当前层数={}", this.amount);
    }

    @Override
    public void stackPower(int stackAmount) {
        if (stackAmount <= 0) {
            return;
        }
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        BasicMod.logger.info("【影蚀】层数增加，增加={}，当前层数={}", stackAmount, this.amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}
