package basicmod.modifiers.demons;

import basemod.abstracts.AbstractCardModifier;
import basicmod.actions.MoonInversionApplyStrengthAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class MoonDemonQiModifier extends AbstractDemonQiModifier {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:DemonQiModifiers");
    private transient com.megacrit.cardcrawl.rooms.AbstractRoom roomUsedIn = null;

    @Override
    public String getPrefix() { return uiStrings.TEXT[14]; }
    @Override
    public String getExtraDesc() { return uiStrings.TEXT[15]; }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return super.shouldApply(card) && card.type == AbstractCard.CardType.ATTACK;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (AbstractDungeon.getCurrRoom() == roomUsedIn) return;

        boolean triggered = false;
        for (AbstractMonster m : getTargets(card, target)) {
            if (m != null && !m.isDeadOrEscaped() && m.hasPower(StrengthPower.POWER_ID)) {
                int str = m.getPower(StrengthPower.POWER_ID).amount;
                if (str > 0) {
                    triggered = true;
                    AbstractDungeon.actionManager.addToBottom(
                            new MoonInversionApplyStrengthAction(m, AbstractDungeon.player, -str * 2)
                    );
                }
            }
        }

        if (triggered) {
            roomUsedIn = AbstractDungeon.getCurrRoom();
        }
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new MoonDemonQiModifier();
    }
}
