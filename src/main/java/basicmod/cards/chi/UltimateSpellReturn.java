package basicmod.cards.chi;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class UltimateSpellReturn extends BaseCard {
    public static final String ID = makeID(UltimateSpellReturn.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.SKILL, CardRarity.RARE, CardTarget.ALL_ENEMY, 3);
    public UltimateSpellReturn() { super(ID, stats); setDamage(5, 2); setMagic(3, 1); setExhaust(true); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.FIRE));
        addToBot(new DrawCardAction(p, this.magicNumber));
    }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeDamage(2); upgradeMagicNumber(1); initializeDescription(); } }
}
