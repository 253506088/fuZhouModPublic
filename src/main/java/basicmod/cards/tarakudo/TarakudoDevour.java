package basicmod.cards.tarakudo;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TarakudoDevour extends BaseCard {
    public static final String ID = makeID(TarakudoDevour.class.getSimpleName());
    private static final CardStats stats = new CardStats(CharacterEnums.SHENGZHU_COLOR, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, 2);
    public TarakudoDevour() { super(ID, stats); setDamage(8, 3); setMagic(2, 1); }
    @Override public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HEAVY));
        addToBot(new HealAction(p, p, this.magicNumber));
    }
    @Override public void upgrade() { if (!upgraded) { upgradeName(); upgradeDamage(3); upgradeMagicNumber(1); initializeDescription(); } }
}
