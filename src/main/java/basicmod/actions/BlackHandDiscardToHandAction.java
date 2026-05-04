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

public class BlackHandDiscardToHandAction extends AbstractGameAction {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(BasicMod.makeID("BlackHandActionsUI"));
    private static final String[] TEXT = uiStrings == null
            ? new String[]{
                    "Select 1 card from your discard pile to add to your hand.",
                    "Select %d cards from your discard pile to add to your hand."
            }
            : uiStrings.TEXT;

    private final AbstractPlayer player;
    private final int amountToMove;
    private int selectCount;

    public BlackHandDiscardToHandAction() {
        this(1);
    }

    public BlackHandDiscardToHandAction(int amount) {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_MED;
        this.player = AbstractDungeon.player;
        this.amountToMove = Math.max(1, amount);
        this.selectCount = 0;
    }

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
