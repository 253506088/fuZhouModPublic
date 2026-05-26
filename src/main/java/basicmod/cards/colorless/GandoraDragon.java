package basicmod.cards.colorless;

import basicmod.actions.GandoraAction;
import basicmod.cards.BaseCard;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 破坏龙甘多拉 - 无色攻击卡
 * 效果：清空玩家和全部怪物的力量（清零），对所有怪物造成清空力量绝对值之和的伤害，消耗
 * 升级：费用-1（3→2）
 */
public class GandoraDragon extends BaseCard {
    public static final String ID = makeID(GandoraDragon.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CardColor.COLORLESS,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ALL_ENEMY,
            3
    );

    public GandoraDragon() {
        super(ID, info);
        setCostUpgrade(2);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 清空全场力量并造成力量绝对值之和的伤害
        addToBot(new GandoraAction(p));
    }
}
