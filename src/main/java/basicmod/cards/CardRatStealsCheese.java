package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardRatStealsCheese extends BaseCard {
    public static final String ID = makeID("RatStealsCheese");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.NONE,
            0);

    private static final int ENERGY = 1;
    private static final int UPG_ENERGY = 1;

    public CardRatStealsCheese() {
        super(ID, info);
        setMagic(ENERGY, UPG_ENERGY);
        tags.add(CustomTags.afu);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainEnergyAction(magicNumber));
        addToBot(new MakeTempCardInDiscardAction(new Dazed(), 1));
    }
}
