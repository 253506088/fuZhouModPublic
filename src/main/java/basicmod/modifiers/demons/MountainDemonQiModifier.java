package basicmod.modifiers.demons;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

public class MountainDemonQiModifier extends AbstractDemonQiModifier {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:DemonQiModifiers");

    @Override
    public String getPrefix() { return uiStrings.TEXT[12]; }
    @Override
    public String getExtraDesc() { return uiStrings.TEXT[13]; }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return super.shouldApply(card) && (card.exhaust || card.isEthereal);
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.exhaust = false;
        card.isEthereal = false;
    }
    
    @Override
    public void onApplyPowers(AbstractCard card) {
        card.exhaust = false;
        card.isEthereal = false;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new MountainDemonQiModifier();
    }
}
