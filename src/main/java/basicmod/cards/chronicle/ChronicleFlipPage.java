package basicmod.cards.chronicle;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ChronicleFlipPage extends BaseCard {
    public static final String ID = makeID(ChronicleFlipPage.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, 1);
    public ChronicleFlipPage() { super(ID, stats); setMagic(2, 1); setExhaust(true); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) { addToBot(new DrawCardAction(p, this.magicNumber)); }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeMagicNumber(1); initializeDescription(); } }
}
