package basicmod.cards.talismans;

import basicmod.cards.BaseCard;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import basicmod.enums.CharacterEnums;

public class SnakeTalismanCard extends BaseCard {
    public static final String ID = makeID("SnakeTalismanCard");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.SELF,
            1
    );

    public SnakeTalismanCard() {
        super(ID, info);
        tags.add(CustomTags.TALISMAN_CARD);
        setMagic(1);
        setExhaust(true);
        setEthereal(true, false);
        setCostUpgrade(0);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new IntangiblePlayerPower(p, magicNumber), magicNumber));
    }
}
