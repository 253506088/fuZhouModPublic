package basicmod.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

/**
 * 黑影护卫能力。
 * 尼嘉-忍者团造成伤害后，将生命伤害转换为格挡；升级版还会计算被格挡吸收的伤害。
 */
public class ShadowGuardPower extends BasePower {
    public static final String POWER_ID = makeID("ShadowGuardPower");
    private boolean includeBlockedDamage;

    /**
     * 构造函数。
     *
     * @param owner 持有者
     * @param amount 层数
     * @param includeBlockedDamage 是否计算被格挡吸收的伤害
     */
    public ShadowGuardPower(AbstractCreature owner, int amount, boolean includeBlockedDamage) {
        super(POWER_ID, PowerType.BUFF, false, owner, owner, Math.max(1, amount));
        this.includeBlockedDamage = includeBlockedDamage;
        updateDescription();
    }

    /**
     * 应用黑影护卫，保留升级版标记。
     *
     * @param owner 持有者
     * @param includeBlockedDamage 是否为升级版效果
     */
    public static void apply(AbstractCreature owner, boolean includeBlockedDamage) {
        AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
            @Override
            public void update() {
                if (owner != null) {
                    if (owner.hasPower(POWER_ID) && owner.getPower(POWER_ID) instanceof ShadowGuardPower) {
                        ShadowGuardPower power = (ShadowGuardPower) owner.getPower(POWER_ID);
                        power.stackPower(1);
                        if (includeBlockedDamage) {
                            power.markIncludeBlockedDamage();
                        }
                    } else {
                        AbstractDungeon.actionManager.addToTop(new com.megacrit.cardcrawl.actions.common.ApplyPowerAction(owner, owner, new ShadowGuardPower(owner, 1, includeBlockedDamage), 1));
                    }
                }
                this.isDone = true;
            }
        });
    }

    /**
     * 判断是否计算被格挡吸收的伤害。
     *
     * @return 升级版效果已启用时返回 true
     */
    public boolean includesBlockedDamage() {
        return this.includeBlockedDamage;
    }

    /**
     * 标记为升级版效果。
     */
    public void markIncludeBlockedDamage() {
        this.includeBlockedDamage = true;
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
     * 更新描述。
     */
    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + (this.includeBlockedDamage ? DESCRIPTIONS[2] : DESCRIPTIONS[1]);
    }
}
