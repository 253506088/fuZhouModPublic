package basicmod.actions;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class AddMaskCardAction extends AbstractGameAction {
    private AbstractCard card;

    public AddMaskCardAction(AbstractCard card) {
        this.card = card;
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        // 如果手牌未满，正常加入手牌
        if (AbstractDungeon.player.hand.size() < BaseMod.MAX_HAND_SIZE) {
            AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(card, 1));
        } else {
            // 如果手牌满了，自动打出（白嫖）
            card.freeToPlayOnce = true; // 设置费用为0（虽然自动打出通常不看费用，但这是保险）
            
            // 查找随机目标（如果卡牌需要目标）
            AbstractMonster target = null;
            if (card.target == AbstractCard.CardTarget.ENEMY || card.target == AbstractCard.CardTarget.SELF_AND_ENEMY) {
                target = AbstractDungeon.getRandomMonster();
            }

            // 将卡牌加入队列执行
            AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(card, target, card.energyOnUse, true, true), true);
        }
        this.isDone = true;
    }
}
