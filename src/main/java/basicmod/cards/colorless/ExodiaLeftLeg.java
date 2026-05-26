package basicmod.cards.colorless;

import basicmod.cards.BaseCard;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 被封印者的左足 - 无色攻击卡
 * 效果：造成7点伤害
 * 升级：造成10点伤害
 */
public class ExodiaLeftLeg extends BaseCard {
    public static final String ID = makeID(ExodiaLeftLeg.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CardColor.COLORLESS,
            CardType.ATTACK,
            CardRarity.SPECIAL,
            CardTarget.ENEMY,
            1
    );

    public ExodiaLeftLeg() {
        super(ID, info);
        setDamage(7, 3); // 基础7，升级+3=10
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL),
                com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
    }

    @Override
    public void triggerWhenDrawn() {
        ExodiaHelper.checkExodiaComplete();
    }

    @Override
    public void triggerOnOtherCardPlayed(AbstractCard c) {
        ExodiaHelper.checkExodiaComplete();
    }

    @Override
    public void triggerAtStartOfTurn() {
        ExodiaHelper.checkExodiaComplete();
    }
}
