package basicmod.modifiers.demons;

import basemod.abstracts.AbstractCardModifier;
import basicmod.powers.FearPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HeavenDemonQiModifier extends AbstractDemonQiModifier {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:DemonQiModifiers");

    @Override
    public String getPrefix() { return uiStrings.TEXT[0]; }
    @Override
    public String getExtraDesc() { return uiStrings.TEXT[1]; }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        for (AbstractMonster m : getTargets(card, target)) {
            if (AbstractDungeon.cardRandomRng.random(0, 7) == 0) {
                AbstractDungeon.actionManager.addToBottom(
                        new ApplyPowerAction(m, AbstractDungeon.player, new FearPower(m, AbstractDungeon.player, 1), 1)
                );
            }
        }
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new HeavenDemonQiModifier();
    }
}
