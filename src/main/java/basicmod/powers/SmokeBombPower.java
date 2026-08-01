package basicmod.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

/**
 * 烟雾弹能力。
 * 尼嘉-忍者团命中时使目标本回合临时失去力量，升级版改为每段攻击都触发。
 */
public class SmokeBombPower extends BasePower {
    public static final String POWER_ID = makeID("SmokeBombPower");
    private boolean triggerPerSegment;

    /**
     * 构造函数。
     *
     * @param owner 持有者
     * @param amount 临时降力数量
     * @param triggerPerSegment 是否每段都触发
     */
    public SmokeBombPower(AbstractCreature owner, int amount, boolean triggerPerSegment) {
        super(POWER_ID, PowerType.BUFF, false, owner, owner, Math.max(1, amount));
        this.triggerPerSegment = triggerPerSegment;
        updateDescription();
    }

    /**
     * 应用烟雾弹，保留升级版触发模式。
     *
     * @param owner 持有者
     * @param triggerPerSegment 是否每段都触发
     */
    public static void apply(AbstractCreature owner, boolean triggerPerSegment) {
        AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
            @Override
            public void update() {
                if (owner != null) {
                    if (owner.hasPower(POWER_ID) && owner.getPower(POWER_ID) instanceof SmokeBombPower) {
                        SmokeBombPower power = (SmokeBombPower) owner.getPower(POWER_ID);
                        power.stackPower(1);
                        if (triggerPerSegment) {
                            power.markTriggerPerSegment();
                        }
                    } else {
                        AbstractDungeon.actionManager.addToTop(new com.megacrit.cardcrawl.actions.common.ApplyPowerAction(owner, owner, new SmokeBombPower(owner, 1, triggerPerSegment), 1));
                    }
                }
                this.isDone = true;
            }
        });
    }

    /**
     * 判断本次尼嘉伤害是否触发烟雾弹。
     *
     * @param hpLoss 目标失去的生命
     * @param segmentResolved 当前攻击段是否完成结算
     * @return 满足触发条件时返回 true
     */
    public boolean shouldTrigger(int hpLoss, boolean segmentResolved) {
        if (this.triggerPerSegment) {
            return segmentResolved;
        }
        return hpLoss > 0;
    }

    /**
     * 标记为升级版触发模式。
     */
    public void markTriggerPerSegment() {
        this.triggerPerSegment = true;
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
        this.description = DESCRIPTIONS[0] + this.amount + (this.triggerPerSegment ? DESCRIPTIONS[2] : DESCRIPTIONS[1]);
    }
}
