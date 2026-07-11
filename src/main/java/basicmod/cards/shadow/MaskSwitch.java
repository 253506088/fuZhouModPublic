package basicmod.cards.shadow;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 面具切换 - 0费技能 消耗
 * 抽1张牌并获得1点能量。
 */
public class MaskSwitch extends BaseCard {
    public static final String ID = makeID(MaskSwitch.class.getSimpleName());
    private static final CardStats stats = new CardStats(
            CharacterEnums.SHENGZHU_COLOR, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, 0);

    public MaskSwitch() { super(ID, stats); setMagic(1, 1); setExhaust(true); }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DrawCardAction(p, this.magicNumber));
        addToBot(new GainEnergyAction(1));
    }

    @Override
    public void upgrade() {
        if (!upgraded) { upgradeName(); upgradeMagicNumber(1); initializeDescription(); }
    }
}
