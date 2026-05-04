package basicmod.actions;

import basicmod.helpers.MaskManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class MingTaExhaustAction extends AbstractGameAction {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:MaskActionsUI");
    private AbstractMonster m;
    private int baseDamage;
    private AbstractCard sourceCard;
    private ArrayList<AbstractCard> nonAttacks = new ArrayList<>();

    public MingTaExhaustAction(AbstractMonster target, int baseDamage, AbstractCard sourceCard) {
        this.actionType = ActionType.EXHAUST;
        this.duration = Settings.ACTION_DUR_FAST;
        this.m = target;
        this.baseDamage = baseDamage;
        this.sourceCard = sourceCard;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                if (c.type != AbstractCard.CardType.ATTACK || c == sourceCard) {
                    nonAttacks.add(c);
                }
            }

            if (nonAttacks.size() == AbstractDungeon.player.hand.group.size()) {
                this.isDone = true;
                doBaseDamage();
                return;
            }

            if (AbstractDungeon.player.hand.group.size() - nonAttacks.size() == 1) {
                for (AbstractCard c : AbstractDungeon.player.hand.group) {
                    if (c.type == AbstractCard.CardType.ATTACK && c != sourceCard) {
                        exhaustAndDamage(c);
                        this.isDone = true;
                        return;
                    }
                }
            }

            AbstractDungeon.player.hand.group.removeAll(nonAttacks);
            AbstractDungeon.handCardSelectScreen.open(uiStrings.TEXT[0], 1, false, false, false, false);
            tickDuration();
            return;
        }

        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                AbstractDungeon.player.hand.addToTop(c); 
                exhaustAndDamage(c);
            }
            
            for (AbstractCard c : nonAttacks) {
                AbstractDungeon.player.hand.addToTop(c);
            }
            AbstractDungeon.player.hand.refreshHandLayout();
            
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            this.isDone = true;
        }
    }

    private void exhaustAndDamage(AbstractCard c) {
        int dmg = c.baseDamage > 0 ? c.baseDamage : 0;
        MaskManager.mingTaTotalDamage += dmg;
        AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(c, AbstractDungeon.player.hand));
        doBaseDamage();
    }

    private void doBaseDamage() {
        int totalDamage = baseDamage + MaskManager.mingTaTotalDamage;
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(AbstractDungeon.player, totalDamage, DamageInfo.DamageType.NORMAL), AttackEffect.SLASH_HEAVY));
    }
}
