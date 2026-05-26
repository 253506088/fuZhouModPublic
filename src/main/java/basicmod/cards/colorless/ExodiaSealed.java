package basicmod.cards.colorless;

import basicmod.cards.BaseCard;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 被封印的艾克佐迪亚 - 无色技能卡
 * 效果：999费，手牌集齐5件套时消灭全部怪物获得胜利
 * 升级：获得保留词条
 * 
 * 触发时机：
 * - triggerWhenDrawn: 被抽到手牌时
 * - triggerOnOtherCardPlayed: 其他卡被打出时
 * - triggerAtStartOfTurn: 回合开始时（覆盖保留场景）
 */
public class ExodiaSealed extends BaseCard {
    public static final String ID = makeID(ExodiaSealed.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CardColor.COLORLESS,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.ALL_ENEMY,
            999
    );

    public ExodiaSealed() {
        super(ID, info);
        setSelfRetain(false, true); // 升级后获得保留
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ExodiaHelper.checkExodiaComplete();
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
