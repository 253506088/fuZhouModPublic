package basicmod.powers;

import basicmod.helpers.NiJiaSupportHelper;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

import static basicmod.BasicMod.makeID;

/**
 * 翼装飞行能力。
 * 让尼嘉-忍者团保留，并在每回合每打出两张尼嘉-忍者团时获得临时影噬。
 */
public class WingsuitFlightPower extends BasePower {
    public static final String POWER_ID = makeID("WingsuitFlightPower");
    private int niJiaPlayedThisTurn = 0;
    private int temporaryDominion = 0;

    /**
     * 构造函数。
     *
     * @param owner 持有者
     * @param amount 层数
     */
    public WingsuitFlightPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, owner, Math.max(1, amount));
        updateDescription();
    }

    /**
     * 初次获得时立刻给手牌中的尼嘉牌加保留。
     */
    @Override
    public void onInitialApplication() {
        NiJiaSupportHelper.applyRetainToHand();
    }

    /**
     * 堆叠后刷新手牌保留。
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
        NiJiaSupportHelper.applyRetainToHand();
        updateDescription();
    }

    /**
     * 每打出两张尼嘉-忍者团，获得本回合临时影噬。
     */
    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (!NiJiaSupportHelper.isNiJiaNinja(card)) {
            return;
        }
        this.niJiaPlayedThisTurn++;
        if (this.niJiaPlayedThisTurn % 2 == 0) {
            int gain = Math.max(1, this.amount);
            this.temporaryDominion += gain;
            this.flash();
            addToBot(new ApplyPowerAction(this.owner, this.owner, new DominionPower(this.owner, gain), gain));
        }
    }

    /**
     * 回合结束弃牌前，给手牌中的尼嘉-忍者团补上保留。
     * 兜底所有生成路径，防止复制出的尼嘉丢失保留标记后被弃掉。
     *
     * @param isPlayer 是否为玩家回合结束
     */
    @Override
    public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
        if (isPlayer) {
            NiJiaSupportHelper.applyRetainToHand();
        }
    }

    /**
     * 回合结束时扣回本回合临时获得的影噬。
     *
     * @param isPlayer 是否为玩家回合结束
     */
    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (!isPlayer) {
            return;
        }
        if (this.temporaryDominion > 0) {
            addToBot(new ReducePowerAction(this.owner, this.owner, DominionPower.POWER_ID, this.temporaryDominion));
        }
        this.temporaryDominion = 0;
        this.niJiaPlayedThisTurn = 0;
    }

    /**
     * 更新描述。
     */
    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}
