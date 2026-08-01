package basicmod.actions;

import basicmod.powers.BlackHandBitterChoicePenaltyPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.LoseDexterityPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

/**
 * 苦涩的抉择动作。
 * 先支付不可格挡生命，再在玩家仍存活时发放能量、抽牌和临时力敏。
 */
public class BlackHandBitterChoiceAction extends AbstractGameAction {
    private final AbstractPlayer player;
    private final int hpLoss;
    private final int resourceAmount;
    private final int statAmount;

    /**
     * 构造函数。
     *
     * @param player 玩家
     * @param hpLoss 失去生命值
     * @param resourceAmount 获得能量与抽牌数量
     * @param statAmount 临时力量与敏捷数量
     */
    public BlackHandBitterChoiceAction(AbstractPlayer player, int hpLoss, int resourceAmount, int statAmount) {
        this.player = player;
        this.hpLoss = hpLoss;
        this.resourceAmount = resourceAmount;
        this.statAmount = statAmount;
        this.actionType = ActionType.SPECIAL;
    }

    /**
     * 执行动作。
     */
    @Override
    public void update() {
        if (this.player == null || this.player.isDeadOrEscaped()) {
            this.isDone = true;
            return;
        }

        this.player.damage(new DamageInfo(this.player, this.hpLoss, DamageInfo.DamageType.HP_LOSS));
        if (this.player.currentHealth <= 0 || this.player.isDeadOrEscaped()) {
            this.isDone = true;
            return;
        }

        addToTop(new ApplyPowerAction(this.player, this.player, new BlackHandBitterChoicePenaltyPower(this.player, 1), 1));
        addToTop(new ApplyPowerAction(this.player, this.player, new LoseDexterityPower(this.player, this.statAmount), this.statAmount));
        addToTop(new ApplyPowerAction(this.player, this.player, new DexterityPower(this.player, this.statAmount), this.statAmount));
        addToTop(new ApplyPowerAction(this.player, this.player, new LoseStrengthPower(this.player, this.statAmount), this.statAmount));
        addToTop(new ApplyPowerAction(this.player, this.player, new StrengthPower(this.player, this.statAmount), this.statAmount));
        addToTop(new DrawCardAction(this.player, this.resourceAmount));
        addToTop(new GainEnergyAction(this.resourceAmount));
        this.isDone = true;
    }
}
