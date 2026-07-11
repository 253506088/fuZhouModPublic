package basicmod.cards.chronicle;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class ShengZhuUsurper extends BaseCard {
    public static final String ID = makeID(ShengZhuUsurper.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.POWER, CardRarity.RARE, CardTarget.SELF, 3);
    public ShengZhuUsurper() { super(ID, stats); setMagic(4, 2); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) { addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber)); }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeMagicNumber(2); initializeDescription(); } }
}
