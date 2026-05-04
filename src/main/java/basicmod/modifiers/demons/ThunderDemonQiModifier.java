package basicmod.modifiers.demons;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

public class ThunderDemonQiModifier extends AbstractDemonQiModifier {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:DemonQiModifiers");

    @Override
    public String getPrefix() { return uiStrings.TEXT[6]; }
    @Override
    public String getExtraDesc() { return uiStrings.TEXT[7]; }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        card.modifyCostForCombat(-1);
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ThunderDemonQiModifier();
    }
}
