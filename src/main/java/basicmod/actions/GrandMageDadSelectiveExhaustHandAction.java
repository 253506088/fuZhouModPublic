package basicmod.actions;

import basicmod.monsters.GrandMageDad;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class GrandMageDadSelectiveExhaustHandAction extends AbstractGameAction {
    private static final String SELECT_TEXT = "选择不消耗的手牌，每张受到12点伤害，可不选。来自于【五行压制】第二回合效果";

    private final AbstractCreature source;
    private final int damagePerCard;

    public GrandMageDadSelectiveExhaustHandAction(AbstractCreature source, int damagePerCard) {
        this.actionType = ActionType.EXHAUST;
        this.duration = Settings.ACTION_DUR_FAST;
        this.source = source;
        this.damagePerCard = damagePerCard;
    }

    @Override
    public void update() {
        AbstractPlayer player = AbstractDungeon.player;
        if (player == null || player.hand == null) {
            this.isDone = true;
            return;
        }

        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (player.hand.isEmpty()) {
                this.isDone = true;
                return;
            }
            AbstractDungeon.handCardSelectScreen.open(SELECT_TEXT, 99, true, true);
            tickDuration();
            return;
        }

        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            ArrayList<AbstractCard> selectedCards = new ArrayList<>(AbstractDungeon.handCardSelectScreen.selectedCards.group);
            ArrayList<AbstractCard> cardsToExhaust = new ArrayList<>(player.hand.group);
            cardsToExhaust.removeAll(selectedCards);

            for (AbstractCard card : selectedCards) {
                player.hand.moveToDiscardPile(card);
                addToBot(new DamageAction(
                        player,
                        new DamageInfo(this.source, this.damagePerCard, DamageInfo.DamageType.NORMAL),
                        AttackEffect.FIRE));
            }
            for (AbstractCard card : cardsToExhaust) {
                player.hand.moveToExhaustPile(card);
            }

            player.hand.refreshHandLayout();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            GrandMageDad.logDetail("【五行压制】回合结束选择不消耗手牌：不消耗受伤=" + selectedCards.size()
                    + "，消耗=" + cardsToExhaust.size()
                    + "，单张伤害=" + this.damagePerCard + "。");
            this.isDone = true;
        }
        tickDuration();
    }
}
