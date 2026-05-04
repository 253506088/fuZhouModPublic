package basicmod.cards.skills;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.PetrifiedPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Petrification extends BaseCard {
    public static final String ID = makeID(Petrification.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.ENEMY,
            1
    );

    public Petrification() {
        super(ID, info);
        tags.add(CustomTags.TEAM_JACKIE);
        this.baseMagicNumber = this.magicNumber = 10;
        setExhaust(true);
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
           upgradeName();
           upgradeMagicNumber(5); // 10 -> 15
           initializeDescription();
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(m, p, new PetrifiedPower(m, 1, this.magicNumber), 1));
    }
}
