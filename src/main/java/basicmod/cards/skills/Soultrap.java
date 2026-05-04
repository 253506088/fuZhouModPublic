package basicmod.cards.skills;

import basicmod.actions.SoultrapAction;
import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Soultrap extends BaseCard {
    public static final String ID = makeID(Soultrap.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.NONE,
            2
    );

    public Soultrap() {
        super(ID, info);
        setCostUpgrade(1);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new SoultrapAction());
    }
}
