package basicmod.modifiers.demons;

import basemod.abstracts.AbstractCardModifier;
import basicmod.powers.WindCatalystPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class WindDemonQiModifier extends AbstractDemonQiModifier {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:DemonQiModifiers");

    @Override
    public String getPrefix() { return uiStrings.TEXT[4]; }
    @Override
    public String getExtraDesc() { return uiStrings.TEXT[5]; }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(2));
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDeadOrEscaped()) {
                AbstractDungeon.actionManager.addToBottom(
                        new ApplyPowerAction(m, AbstractDungeon.player, new WindCatalystPower(m, AbstractDungeon.player, 2), 2)
                );
            }
        }
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new WindDemonQiModifier();
    }
}
