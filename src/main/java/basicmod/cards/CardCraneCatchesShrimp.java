package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class CardCraneCatchesShrimp extends BaseCard {
    public static final String ID = makeID("CraneCatchesShrimp");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.NONE,
            1);

    public CardCraneCatchesShrimp() {
        super(ID, info);
        setMagic(1); // 这里的 M 表示抽牌倍率/逻辑
        setCostUpgrade(0);
        tags.add(CustomTags.afu);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new CraneCatchesShrimpAction(this.magicNumber));
    }

    private static class CraneCatchesShrimpAction extends AbstractGameAction {
        private int magicVal;
        public CraneCatchesShrimpAction(int magicVal) {
            this.magicVal = magicVal;
            this.actionType = ActionType.CARD_MANIPULATION;
        }

        @Override
        public void update() {
            AbstractPlayer p = AbstractDungeon.player;
            ArrayList<AbstractCard> toDiscard = new ArrayList<>();
            for (AbstractCard c : p.hand.group) {
                if (c.type != AbstractCard.CardType.ATTACK && c != AbstractDungeon.player.hoveredCard) {
                    toDiscard.add(c);
                }
            }

            int count = toDiscard.size();
            for (AbstractCard c : toDiscard) {
                p.hand.moveToDiscardPile(c);
                c.triggerOnManualDiscard();
            }

            if (count > 0) {
                addToTop(new DrawCardAction(p, count * magicVal));
            }
            this.isDone = true;
        }
    }
}
