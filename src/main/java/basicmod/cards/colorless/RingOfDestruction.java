package basicmod.cards.colorless;

import basicmod.cards.BaseCard;
import basicmod.powers.RingOfDestructionPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 破坏轮 - 无色技能卡
 * 效果：选择一个怪物为目标，该怪物死亡时对场上其他怪物造成其最大生命值的伤害，消耗
 * 升级：去掉消耗词条
 */
public class RingOfDestruction extends BaseCard {
    public static final String ID = makeID(RingOfDestruction.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CardColor.COLORLESS,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.ENEMY,
            1
    );

    public RingOfDestruction() {
        super(ID, info);
        setExhaust(true, false); // 基础消耗，升级后去掉消耗
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 给目标怪物施加破坏轮标记，死亡时触发爆炸
        addToBot(new ApplyPowerAction(m, p, new RingOfDestructionPower(m, p), 1));
    }
}
