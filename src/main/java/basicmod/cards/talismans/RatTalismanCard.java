package basicmod.cards.talismans;

import basicmod.cards.BaseCard;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import basicmod.actions.RatTalismanAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import basicmod.enums.CharacterEnums;

public class RatTalismanCard extends BaseCard {
    public static final String ID = makeID("RatTalismanCard");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.NONE,
            1
    );

    public RatTalismanCard() {
        super(ID, info);
        tags.add(CustomTags.TALISMAN_CARD);
        setMagic(1, 1);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new RatTalismanAction(this.magicNumber));
    }
}
