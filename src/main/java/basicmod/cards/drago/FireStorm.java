package basicmod.cards.drago;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class FireStorm extends BaseCard {
    public static final String ID = makeID(FireStorm.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ALL_ENEMY, 2);
    public FireStorm() { super(ID, stats); setDamage(6, 3); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) { addToBot(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.FIRE)); }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeDamage(3); initializeDescription(); } }
}
