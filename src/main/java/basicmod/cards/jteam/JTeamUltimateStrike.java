package basicmod.cards.jteam;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 龙小组·终极合击 - 3费攻击 消耗
 * 造成3伤害；本局每打出过一张牌+3伤害（近似龙小组合击）。
 */
public class JTeamUltimateStrike extends BaseCard {
    public static final String ID = makeID(JTeamUltimateStrike.class.getSimpleName());
    private static final CardStats stats = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.RARE,
            CardTarget.ENEMY,
            3
    );

    public JTeamUltimateStrike() {
        super(ID, stats);
        setDamage(3, 1);
        setMagic(3, 1);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int bonus = p.hand.size() * this.magicNumber;
        int totalDmg = this.damage + bonus;
        addToBot(new DamageAction(m, new DamageInfo(p, totalDmg, DamageInfo.DamageType.NORMAL),
                AbstractGameAction.AttackEffect.SLASH_HEAVY));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(1);
            upgradeMagicNumber(1);
            initializeDescription();
        }
    }
}
