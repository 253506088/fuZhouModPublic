package basicmod.modifiers.demons;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.BlurPower;

public class EarthDemonQiModifier extends AbstractDemonQiModifier {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:DemonQiModifiers");

    @Override
    public String getPrefix() { return uiStrings.TEXT[2]; }
    @Override
    public String getExtraDesc() { return uiStrings.TEXT[3]; }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new BlurPower(AbstractDungeon.player, 1), 1)
        );
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new EarthDemonQiModifier();
    }
}
