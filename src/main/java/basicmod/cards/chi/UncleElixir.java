package basicmod.cards.chi;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class UncleElixir extends BaseCard {
    public static final String ID = makeID(UncleElixir.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF, 0);
    public UncleElixir() { super(ID, stats); setMagic(4, 2); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new HealAction(p, p, this.magicNumber));
        addToBot(new GainEnergyAction(1));
    }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeMagicNumber(2); initializeDescription(); } }
}
