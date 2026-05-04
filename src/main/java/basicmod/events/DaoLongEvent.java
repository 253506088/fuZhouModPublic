package basicmod.events;

import basicmod.cards.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;

import static basicmod.BasicMod.makeID;

public class DaoLongEvent extends AbstractImageEvent {
    public static final String ID = makeID("DaoLongEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    private int screenNum = 0;
    private boolean hasEligibleCards;

    public DaoLongEvent() {
        super(NAME, DESCRIPTIONS[0], "basicmod/images/events/daolong.png");
        this.hasEligibleCards = checkEligibleCards();

        if (this.hasEligibleCards) {
            // 看到有卡，接受可用 [0]，离开灰掉 [2] (无法拒绝)
            this.imageEventText.setDialogOption(OPTIONS[0]); // 接受
            this.imageEventText.setDialogOption(OPTIONS[2], true); // 无法拒绝 (灰掉)
        } else {
            // 没卡，接受灰掉 [3]，离开可用 [1]
            this.imageEventText.setDialogOption(OPTIONS[3], true); // 不可用 (灰掉)
            this.imageEventText.setDialogOption(OPTIONS[1]); // 离开
        }
    }

    private boolean checkEligibleCards() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) return false;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (isEligible(c)) return true;
        }
        return false;
    }

    private boolean isEligible(AbstractCard c) {
        return c.cardID.equals(CardBlackHandChow.ID) ||
               c.cardID.equals(CardBlackHandRatso.ID) ||
               c.cardID.equals(CardBlackHandAhFen.ID) ||
               c.cardID.equals(CardTaiShanPress.ID);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                if (this.hasEligibleCards) {
                    if (buttonPressed == 0) {
                        // 1. 接受加持 (只有这个能点)
                        transformCards();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]); // 离开
                        this.imageEventText.clearRemainingOptions();
                        screenNum = 1;
                    }
                } else {
                    if (buttonPressed == 1) {
                        // 2. 离开
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]); // 离开
                        this.imageEventText.clearRemainingOptions();
                        screenNum = 1;
                    }
                }
                break;
            case 1:
                openMap();
                break;
        }
    }

    private void transformCards() {
        for (int i = 0; i < AbstractDungeon.player.masterDeck.group.size(); i++) {
            AbstractCard c = AbstractDungeon.player.masterDeck.group.get(i);
            AbstractCard newCard = null;
            
            if (c.cardID.equals(CardBlackHandChow.ID)) newCard = new CardGan();
            else if (c.cardID.equals(CardBlackHandRatso.ID)) newCard = new CardWen();
            else if (c.cardID.equals(CardBlackHandAhFen.ID)) newCard = new CardCui();
            else if (c.cardID.equals(CardTaiShanPress.ID)) newCard = new CardTornado();

            if (newCard != null) {
                if (c.upgraded) {
                    newCard.upgrade();
                }
                AbstractDungeon.player.masterDeck.group.set(i, newCard);
            }
        }
    }
}
