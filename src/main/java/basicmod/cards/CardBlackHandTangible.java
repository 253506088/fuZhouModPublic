package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.BlackHandPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardBlackHandTangible extends BaseCard {
    public static final String ID = makeID("BlackHandTangible");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.ENEMY,
            1
    );

    public CardBlackHandTangible() {
        super(ID, info);
        setCostUpgrade(0);
        tags.add(CustomTags.blackhand);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int current = BlackHandPower.getAmount(m);
        if (current > 0) {
            BlackHandPower.apply(m, p, current);
        }
    }
}
