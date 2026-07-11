package basicmod.cards.chi;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ChiCounter extends BaseCard {
    public static final String ID = makeID(ChiCounter.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, 1);
    public ChiCounter() { super(ID, stats); setDamage(3, 2); setBlock(3, 2); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
        addToBot(new GainBlockAction(p, p, this.block));
    }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeDamage(2); upgradeBlock(2); initializeDescription(); } }
}
