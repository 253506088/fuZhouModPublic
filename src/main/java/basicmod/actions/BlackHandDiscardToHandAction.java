package basicmod.actions;

import basemod.BaseMod;
import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;

/**
 * 黑手党弃牌堆回收动作。
 * 从弃牌堆中选择指定数量的卡牌加入手牌。
 */
public class BlackHandDiscardToHandAction extends AbstractGameAction {
    /** UI字符串，用于选择提示 */
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(BasicMod.makeID("BlackHandActionsUI"));
    /** 提示文本数组 */
    private static final String[] TEXT = uiStrings == null
            ? new String[]{
                    "Select 1 card from your discard pile to add to your hand.",
                    "Select %d cards from your discard pile to add to your hand."
            }
            : uiStrings.TEXT;

    /** 玩家对象 */
    private final AbstractPlayer player;
    /** 要移动的卡牌数量 */
    private final int amountToMove;
    /** 已选择的卡牌计数 */
    private int selectCount;

    /**
     * 默认构造函数，移动1张卡牌。
     */
    public BlackHandDiscardToHandAction() {
        this(1);
    }

    /**
     * 指定数量的构造函数。
     *
     * @param amount 要移动的卡牌数量
     */
    public BlackHandDiscardToHandAction(int amount) {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_MED;
        this.player = AbstractDungeon.player;
        this.amountToMove = Math.max(1, amount);
        this.selectCount = 0;
    }

    /**
     * 执行动作逻辑：打开弃牌堆选择界面，将选中的卡牌加入手牌。
     */
    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_MED) {
            if (this.player == null || this.player.discardPile.isEmpty()) {
                this.isDone = true;
                return;
            }

            int handSpace = BaseMod.MAX_HAND_SIZE - this.player.hand.size();
            if (handSpace <= 0) {
                this.player.createHandIsFullDialog();
                this.isDone = true;
                return;
            }

            this.selectCount = Math.min(this.amountToMove, this.player.discardPile.size());
            this.selectCount = Math.min(this.selectCount, handSpace);
            if (this.selectCount <= 0) {
                this.isDone = true;
                return;
            }

            if (this.selectCount >= this.player.discardPile.size()) {
                for (int i = 0; i < this.selectCount; i++) {
                    moveToHand(this.player.discardPile.getTopCard());
                }
                this.isDone = true;
                return;
            }

            AbstractDungeon.gridSelectScreen.open(this.player.discardPile, this.selectCount, buildPrompt(this.selectCount), false);
            tickDuration();
            return;
        }

        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            ArrayList<AbstractCard> chosenCards = new ArrayList<>(AbstractDungeon.gridSelectScreen.selectedCards);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            for (AbstractCard chosen : chosenCards) {
                moveToHand(chosen);
            }
        }
        this.isDone = true;
    }

    /**
     * 构建选择提示文本。
     *
     * @param count 要选择的卡牌数量
     * @return 格式化后的提示文本
     */
    private String buildPrompt(int count) {
        if (count <= 1) {
            return TEXT[0];
        }
        if (TEXT.length > 1) {
            try {
                return String.format(TEXT[1], count);
            } catch (Exception ignored) {
                return TEXT[0];
            }
        }
        return TEXT[0];
    }

    /**
     * 将指定卡牌从弃牌堆移动到手牌。
     * 如果手牌已满，卡牌会放回弃牌堆顶部。
     *
     * @param card 要移动的卡牌
     */
    private void moveToHand(AbstractCard card) {
        if (card == null || this.player == null) {
            return;
        }

        this.player.discardPile.removeCard(card);
        if (this.player.hand.size() >= BaseMod.MAX_HAND_SIZE) {
            this.player.discardPile.addToTop(card);
            this.player.createHandIsFullDialog();
            return;
        }

        card.unhover();
        card.lighten(false);
        card.applyPowers();
        this.player.hand.addToTop(card);
        this.player.hand.refreshHandLayout();
    }
}
