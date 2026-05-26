package basicmod.cards.colorless;

import basicmod.actions.DiceRollAction;
import basicmod.cards.BaseCard;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 恶魔的骰子 - 无色技能卡
 * 效果：指定一名敌人力量随机下降1~6点，消耗
 * 升级：费用降低为1
 */
public class DemonDice extends BaseCard {
    public static final String ID = makeID(DemonDice.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CardColor.COLORLESS,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            2
    );

    public DemonDice() {
        super(ID, info);
        setCostUpgrade(1);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 掷骰子降低目标力量1~6点
        addToBot(new DiceRollAction(m, p, false));
    }
}
