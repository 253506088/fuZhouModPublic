package basicmod.cards.gate;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class GateOverflow extends BaseCard {
    public static final String ID = makeID(GateOverflow.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ALL_ENEMY, 0);
    public GateOverflow() { super(ID, stats); setDamage(3, 2); setExhaust(true); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.FIRE));
    }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeDamage(2); initializeDescription(); } }
}
