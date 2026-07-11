package basicmod.cards.fusion;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.helpers.FusionHelper;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 符咒风暴 - 2费攻击 消耗
 * 造成持有符咒遗物数×4的伤害。消耗。
 */
public class TalismanStorm extends BaseCard {
    public static final String ID = makeID(TalismanStorm.class.getSimpleName());
    private static final CardStats stats = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.RARE,
            CardTarget.ENEMY,
            2
    );

    public TalismanStorm() {
        super(ID, stats);
        setMagic(4, 2);  // 每个符咒遗物提供的伤害倍数
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int talismanCount = FusionHelper.countTalismanRelics();
        int dmg = talismanCount * this.magicNumber;
        addToBot(new DamageAction(m, new DamageInfo(p, dmg, DamageInfo.DamageType.NORMAL),
                AbstractGameAction.AttackEffect.LIGHTNING));
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int talismanCount = FusionHelper.countTalismanRelics();
        this.baseDamage = talismanCount * this.magicNumber;
        super.calculateCardDamage(mo);
    }

    @Override
    public void applyPowers() {
        int talismanCount = FusionHelper.countTalismanRelics();
        this.baseDamage = talismanCount * this.magicNumber;
        super.applyPowers();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(2);
            initializeDescription();
        }
    }
}
