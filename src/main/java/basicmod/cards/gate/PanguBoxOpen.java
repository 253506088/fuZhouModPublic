package basicmod.cards.gate;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PanguBoxOpen extends BaseCard {
    public static final String ID = makeID(PanguBoxOpen.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, 1);
    public PanguBoxOpen() { super(ID, stats); setMagic(2, 1); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) { addToBot(new DrawCardAction(p, this.magicNumber)); addToBot(new GainEnergyAction(1)); }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeMagicNumber(1); initializeDescription(); } }
}
