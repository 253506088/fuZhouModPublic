package basicmod.cards.chi;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class UncleGarlic extends BaseCard {
    public static final String ID = makeID(UncleGarlic.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF, 1);
    public UncleGarlic() { super(ID, stats); setBlock(5, 3); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) { addToBot(new GainBlockAction(p, p, this.block)); }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeBlock(3); initializeDescription(); } }
}
