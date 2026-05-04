package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.BlackHandPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardBlackHandSpreadRumors extends BaseCard {
    public static final String ID = makeID("BlackHandSpreadRumors");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.ENEMY,
            0
    );

    public CardBlackHandSpreadRumors() {
        super(ID, info);
        setMagic(2, 2);
        tags.add(CustomTags.blackhand);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        boolean hadBlackHand = BlackHandPower.hasAny(m);
        BlackHandPower.apply(m, p, this.magicNumber);
        if (hadBlackHand) {
            addToBot(new GainEnergyAction(1));
        }
    }
}
