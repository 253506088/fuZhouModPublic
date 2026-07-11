package basicmod.cards.chronicle;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class JTeamRestoreHistory extends BaseCard {
    public static final String ID = makeID(JTeamRestoreHistory.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.SKILL, CardRarity.RARE, CardTarget.SELF, 3);
    public JTeamRestoreHistory() { super(ID, stats); setMagic(10, 5); setExhaust(true); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) { addToBot(new HealAction(p, p, this.magicNumber)); }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeMagicNumber(5); initializeDescription(); } }
}
