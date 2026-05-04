package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.FlightAfuPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardEagleWings extends BaseCard {
    public static final String ID = makeID("EagleWings");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            1);

    private static final int FLIGHT = 1;

    public CardEagleWings() {
        super(ID, info);
        setMagic(FLIGHT);
        setExhaust(true, false);
        tags.add(CustomTags.afu);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new FlightAfuPower(p, magicNumber), magicNumber));
    }
}
