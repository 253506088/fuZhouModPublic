package basicmod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import static basicmod.BasicMod.makeID;

/**
 * 苦涩的抉择惩罚能力。
 * 下回合开始后让全部卡牌费用临时增加。
 */
public class BlackHandBitterChoicePenaltyPower extends BasePower {
    public static final String POWER_ID = makeID("BlackHandBitterChoicePenaltyPower");
    private boolean active;

    /**
     * 构造函数。
     *
     * @param owner 持有者
     * @param amount 下回合费用增加值
     */
    public BlackHandBitterChoicePenaltyPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.DEBUFF, true, owner, owner, Math.max(1, amount));
        this.active = false;
        updateDescription();
    }

    /**
     * 判断玩家当前是否正在受加费影响。
     *
     * @return 激活时返回加费值，否则返回 0
     */
    public static int getActiveTaxAmount() {
        if (AbstractDungeon.player == null) {
            return 0;
        }
        // 关键点：只做一次能力查找，判断与取值复用同一个结果。
        AbstractPower power = AbstractDungeon.player.getPower(POWER_ID);
        if (!(power instanceof BlackHandBitterChoicePenaltyPower)) {
            return 0;
        }
        BlackHandBitterChoicePenaltyPower penalty = (BlackHandBitterChoicePenaltyPower) power;
        return penalty.active ? Math.max(1, penalty.amount) : 0;
    }

    /**
     * 堆叠下回合加费值。
     *
     * @param stackAmount 增加值
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
     * 下回合抽牌后激活加费。
     */
    @Override
    public void atStartOfTurnPostDraw() {
        this.active = true;
        refreshHandCosts();
        updateDescription();
    }

    /**
     * 激活回合结束时移除能力并刷新费用。
     *
     * @param isPlayer 是否为玩家回合结束
     */
    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer && this.active) {
            this.active = false;
            refreshHandCosts();
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }

    private void refreshHandCosts() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.hand == null) {
            return;
        }
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            card.applyPowers();
        }
    }

    /**
     * 更新描述。
     */
    @Override
    public void updateDescription() {
        this.description = (this.active ? DESCRIPTIONS[2] : DESCRIPTIONS[0]) + this.amount + DESCRIPTIONS[1];
    }
}
