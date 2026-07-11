package basicmod.cards.district13;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class District13Lockdown extends BaseCard {
    public static final String ID = makeID(District13Lockdown.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF, 1);
    public District13Lockdown() { super(ID, stats); setBlock(6, 3); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) { addToBot(new GainBlockAction(p, p, this.block)); }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeBlock(3); initializeDescription(); } }
}
