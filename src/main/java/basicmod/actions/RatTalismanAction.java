package basicmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;

public class RatTalismanAction extends AbstractGameAction {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:RatTalismanUI");
    private final AbstractPlayer p;
    private int numberOfCards;

    public RatTalismanAction(int numberOfCards) {
        this.p = AbstractDungeon.player;
        this.numberOfCards = numberOfCards;
        this.setValues(p, p, numberOfCards);
        this.actionType = ActionType.DRAW;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (p.exhaustPile.isEmpty()) {
                this.isDone = true;
                return;
            }

            if (p.exhaustPile.size() <= numberOfCards) {
                ArrayList<AbstractCard> cardsToMove = new ArrayList<>(p.exhaustPile.group);
                for (AbstractCard c : cardsToMove) {
                    moveCardToHand(c);
                }
                this.isDone = true;
                return;
            }

            // Choose cards from exhaust pile
            AbstractDungeon.gridSelectScreen.open(p.exhaustPile, numberOfCards, uiStrings.TEXT[1], false);
        } else {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    moveCardToHand(c);
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                this.isDone = true;
            }
        }
        this.tickDuration();
    }

    private void moveCardToHand(AbstractCard c) {
        if (p.hand.size() < basemod.BaseMod.MAX_HAND_SIZE) {
            c.unfadeOut();
            p.exhaustPile.removeCard(c);
            p.hand.addToHand(c);
            
            // Set cost to 0 this turn
            c.setCostForTurn(0);
            // 子鼠拉回的卡在本场战斗获得虚无与消耗。
            c.isEthereal = true;
            c.exhaust = true;
            c.initializeDescription();
            
            c.unhover();
            c.lighten(false);
            c.applyPowers();
            c.flash();
            p.hand.refreshHandLayout();
        } else {
            // Hand full, keep in exhaust pile.
        }
    }
}
