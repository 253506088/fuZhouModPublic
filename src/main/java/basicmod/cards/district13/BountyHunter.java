package basicmod.cards.district13;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BountyHunter extends BaseCard {
    public static final String ID = makeID(BountyHunter.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, 2);
    public BountyHunter() { super(ID, stats); setDamage(5, 2); setMagic(10, 5); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        addToBot(new GainGoldAction(this.magicNumber));
    }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeDamage(2); upgradeMagicNumber(5); initializeDescription(); } }
}
