package basicmod.cards.drago;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DragoFrenzy extends BaseCard {
    public static final String ID = makeID(DragoFrenzy.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, 0);
    public DragoFrenzy() { super(ID, stats); setMagic(2, 1); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) { addToBot(new GainEnergyAction(this.magicNumber)); }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeMagicNumber(1); initializeDescription(); } }
}
