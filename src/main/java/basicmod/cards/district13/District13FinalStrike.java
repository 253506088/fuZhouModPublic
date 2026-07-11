package basicmod.cards.district13;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class District13FinalStrike extends BaseCard {
    public static final String ID = makeID(District13FinalStrike.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, 3);
    public District13FinalStrike() { super(ID, stats); setDamage(10, 4); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HEAVY));
    }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeDamage(4); initializeDescription(); } }
}
