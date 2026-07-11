package basicmod.cards.drago;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.EnergizedPower;

public class DragonWill extends BaseCard {
    public static final String ID = makeID(DragonWill.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.POWER, CardRarity.UNCOMMON, CardTarget.SELF, 1);
    public DragonWill() { super(ID, stats); setMagic(1, 1); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) { addToBot(new ApplyPowerAction(p, p, new EnergizedPower(p, this.magicNumber), this.magicNumber)); }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeMagicNumber(1); initializeDescription(); } }
}
