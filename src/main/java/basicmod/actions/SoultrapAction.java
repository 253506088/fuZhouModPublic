package basicmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.DexterityPower;

/**
 * 收魂动作。
 * 消耗手牌，根据消耗数量获得力量和敏捷（交替获得）。
 */
public class SoultrapAction extends AbstractGameAction {
    /** UI字符串 */
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:SoultrapUI");

    /**
     * 构造函数。
     */
    public SoultrapAction() {
        this.actionType = ActionType.EXHAUST;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    /**
     * 执行动作逻辑：消耗手牌，根据数量交替获得力量和敏捷。
     */
    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (AbstractDungeon.player.hand.isEmpty()) {
                this.isDone = true;
                return;
            }
            AbstractDungeon.handCardSelectScreen.open(uiStrings.TEXT[0], 99, true, true);
            tickDuration();
            return;
        }

        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            int count = AbstractDungeon.handCardSelectScreen.selectedCards.size();
            if (count > 0) {
                for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                    AbstractDungeon.player.hand.moveToExhaustPile(c);
                }
                int strengthGain = (count + 1) / 2;
                int dexterityGain = count / 2;
                if (strengthGain > 0) {
                    addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, strengthGain), strengthGain));
                }
                if (dexterityGain > 0) {
                    addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DexterityPower(AbstractDungeon.player, dexterityGain), dexterityGain));
                }
            }
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            this.isDone = true;
        }
        tickDuration();
    }
}
