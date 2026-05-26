package basicmod.cards.colorless;

import basicmod.actions.BlueEyesDiscardAction;
import basicmod.cards.BaseCard;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 青眼白龙 - 无色攻击卡
 * 效果：造成30点伤害，随即消耗掉两张手牌
 * 升级：费用降低为1
 */
public class BlueEyesWhiteDragon extends BaseCard {
    public static final String ID = makeID(BlueEyesWhiteDragon.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CardColor.COLORLESS,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            2
    );

    public BlueEyesWhiteDragon() {
        super(ID, info);
        setDamage(30);
        setCostUpgrade(1);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 造成30点伤害
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL),
                com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect.SLASH_HEAVY));
        // 随机消耗2张手牌
        addToBot(new BlueEyesDiscardAction(p, 2));
    }
}
