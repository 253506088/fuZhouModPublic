package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.GoatClimbsMountainPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardGoatClimbsMountain extends BaseCard {
    public static final String ID = makeID("GoatClimbsMountain");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.POWER,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            1);

    private static final int STR = 1;

    public CardGoatClimbsMountain() {
        super(ID, info);
        setMagic(STR);
        setCostUpgrade(0);
        tags.add(CustomTags.afu);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new GoatClimbsMountainPower(p, magicNumber), magicNumber));
    }
}
