package basicmod.cards.colorless;

import basicmod.cards.BaseCard;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 被封印者的左臂 - 无色技能卡
 * 效果：获得7点护甲
 * 升级：获得10点护甲
 */
public class ExodiaLeftArm extends BaseCard {
    public static final String ID = makeID(ExodiaLeftArm.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CardColor.COLORLESS,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.SELF,
            1
    );

    public ExodiaLeftArm() {
        super(ID, info);
        setBlock(7, 3); // 基础7，升级+3=10
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, this.block));
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
