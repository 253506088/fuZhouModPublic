package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;

public class CardCui extends BaseCard {
    public static final String ID = makeID("Cui");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.ENEMY,
            1);

    public CardCui() {
        super(ID, info);
        setBlock(6, 2); // 6 -> 8
        setMagic(1, 1); // 1 -> 2 (抽弃、虚弱)
        tags.add(basicmod.enums.CustomTags.DAOLONG_DARK_ASSASSIN);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, block));
        addToBot(new DrawCardAction(p, magicNumber));
        addToBot(new DiscardAction(p, p, magicNumber, false));
        addToBot(new ApplyPowerAction(m, p, new WeakPower(m, magicNumber, false), magicNumber));
    }
}
