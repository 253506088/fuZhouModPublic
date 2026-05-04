package basicmod.modifiers.demons;

import basemod.abstracts.AbstractCardModifier;
import basicmod.powers.BurningPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class FireDemonQiModifier extends AbstractDemonQiModifier {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:DemonQiModifiers");

    @Override
    public String getPrefix() { return uiStrings.TEXT[10]; }
    @Override
    public String getExtraDesc() { return uiStrings.TEXT[11]; }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return super.shouldApply(card) && card.type == AbstractCard.CardType.ATTACK;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (card.type == AbstractCard.CardType.ATTACK) {
            for (AbstractMonster m : getTargets(card, target)) {
                AbstractDungeon.actionManager.addToBottom(
                        new ApplyPowerAction(m, AbstractDungeon.player, new BurningPower(m, AbstractDungeon.player, 2), 2)
                );
            }
        }
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new FireDemonQiModifier();
    }
}
