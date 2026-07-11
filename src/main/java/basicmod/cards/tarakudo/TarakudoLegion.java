package basicmod.cards.tarakudo;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TarakudoLegion extends BaseCard {
    public static final String ID = makeID(TarakudoLegion.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.SKILL, CardRarity.RARE, CardTarget.ALL_ENEMY, 3);
    public TarakudoLegion() { super(ID, stats); setDamage(5, 3); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.SLASH_HEAVY));
    }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeDamage(3); initializeDescription(); } }
}
