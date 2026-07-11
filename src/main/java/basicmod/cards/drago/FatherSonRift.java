package basicmod.cards.drago;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class FatherSonRift extends BaseCard {
    public static final String ID = makeID(FatherSonRift.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, 2);
    public FatherSonRift() { super(ID, stats); setDamage(12, 4); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) { addToBot(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HEAVY)); }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeDamage(4); initializeDescription(); } }
}
