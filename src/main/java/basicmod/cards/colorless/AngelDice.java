package basicmod.cards.colorless;

import basicmod.actions.DiceRollAction;
import basicmod.cards.BaseCard;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 天使的骰子 - 无色技能卡
 * 效果：力量随机提升1~6点，消耗
 * 升级：费用降低为1
 */
public class AngelDice extends BaseCard {
    public static final String ID = makeID(AngelDice.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CardColor.COLORLESS,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            2
    );

    public AngelDice() {
        super(ID, info);
        setCostUpgrade(1);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 掷骰子提升自身力量1~6点
        addToBot(new DiceRollAction(p, p, true));
    }
}
