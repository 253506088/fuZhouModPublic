package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

import java.util.ArrayList;

public class CardComboMove extends BaseCard {
    public static final String ID = makeID("ComboMove");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.NONE,
            1);

    public CardComboMove() {
        super(ID, info);
        setCostUpgrade(0);
        tags.add(CustomTags.afu);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ComboMoveAction());
    }

    private static class ComboMoveAction extends AbstractGameAction {
        private AbstractPlayer p;

        public ComboMoveAction() {
            this.p = AbstractDungeon.player;
            this.actionType = ActionType.CARD_MANIPULATION;
            this.duration = Settings.ACTION_DUR_MED;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_MED) {
                CardGroup attacks = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                for (AbstractCard c : p.drawPile.group) {
                    if (c.type == AbstractCard.CardType.ATTACK) {
                        attacks.addToBottom(c);
                    }
                }

                if (attacks.isEmpty()) {
                    this.isDone = true;
                    return;
                }

                AbstractDungeon.gridSelectScreen.open(attacks, 1, "选择一张攻击牌放入手牌", false);
                tickDuration();
                return;
            }

            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    c.unhover();
                    if (p.hand.size() < basemod.BaseMod.MAX_HAND_SIZE) {
                        p.drawPile.removeCard(c);
                        p.hand.addToTop(c);
                        // 使用 setCostForTurn 使费用仅在回合内生效
                        c.setCostForTurn(0);
                    } else {
                        p.drawPile.removeCard(c);
                        p.discardPile.addToTop(c);
                    }
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }
            this.isDone = true;
        }
    }
}
