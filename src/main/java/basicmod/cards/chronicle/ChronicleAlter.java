package basicmod.cards.chronicle;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ChronicleAlter extends BaseCard {
    public static final String ID = makeID(ChronicleAlter.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF, 0);
    public ChronicleAlter() { super(ID, stats); setMagic(1, 1); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) { addToBot(new GainEnergyAction(this.magicNumber)); }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeMagicNumber(1); initializeDescription(); } }
}
