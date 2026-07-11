package basicmod.cards.district13;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;

public class District13Raid extends BaseCard {
    public static final String ID = makeID(District13Raid.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, 1);
    public District13Raid() { super(ID, stats); setDamage(7, 3); setMagic(5, 2); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) {
        int dmg = this.damage;
        if (m.hasPower(WeakPower.POWER_ID)) dmg += this.magicNumber;
        addToBot(new DamageAction(m, new DamageInfo(p, dmg, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeDamage(3); upgradeMagicNumber(2); initializeDescription(); } }
}
