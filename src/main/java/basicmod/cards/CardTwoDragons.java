package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.TwoDragonsPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardTwoDragons extends BaseCard {
    public static final String ID = makeID("TwoDragons");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.SELF,
            2);

    private static final int COUNT = 2;

    public CardTwoDragons() {
        super(ID, info);
        setMagic(COUNT);
        setCostUpgrade(1); // 升级后费用-1
        setExhaust(true);
        tags.add(CustomTags.afu);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new TwoDragonsPower(p, magicNumber), magicNumber));
    }
}
