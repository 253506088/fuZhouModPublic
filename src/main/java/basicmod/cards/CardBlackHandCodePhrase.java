package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.helpers.BlackHandCardHelper;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardBlackHandCodePhrase extends BaseCard {
    public static final String ID = makeID("BlackHandCodePhrase");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.SELF,
            0
    );

    public CardBlackHandCodePhrase() {
        super(ID, info);
        setBlock(4, 1);
        setExhaust(true);
        tags.add(CustomTags.blackhand);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (isPreviousCardBlackHand()) {
            addToBot(new GainEnergyAction(1));
            addToBot(new DrawCardAction(p, 1));
        } else {
            addToBot(new GainBlockAction(p, p, this.block));
        }
    }

    private boolean isPreviousCardBlackHand() {
        AbstractCard previous = BlackHandCardHelper.getPreviousCardPlayedThisTurn();
        return BlackHandCardHelper.isBlackHandCard(previous);
    }
}
