package basicmod.cards.skills;

import basicmod.actions.AmbidexterityAction;
import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Ambidexterity extends BaseCard {
    public static final String ID = makeID(Ambidexterity.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.NONE,
            1
    );

    public Ambidexterity() {
        super(ID, info);
        this.baseMagicNumber = this.magicNumber = 2; // Default 2
        tags.add(CustomTags.TEAM_JACKIE);
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(1); // Upgrade to draw 3
            initializeDescription();
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AmbidexterityAction(this.magicNumber));
    }
}
