package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FlameBarrierPower;

public class CardCui extends BaseCard {
    public static final String ID = makeID("Cui");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.SELF,
            1);

    public CardCui() {
        super(ID, info);
        setBlock(8, 3); // 8 -> 11
        setMagic(3, 2); // 3 -> 5 反弹伤害
        tags.add(basicmod.enums.CustomTags.DAOLONG_DARK_ASSASSIN);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, block));
        addToBot(new ApplyPowerAction(p, p, new FlameBarrierPower(p, magicNumber), magicNumber));
    }
}
