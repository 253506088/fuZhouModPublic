package basicmod.cards.colorless;

import basicmod.cards.BaseCard;
import basicmod.powers.ChestnutBallPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 栗子球 - 无色技能卡
 * 效果：本回合完全免疫任何来源的伤害（攻击伤害、HP损失等全部归零），消耗，虚无
 * 升级：去掉虚无词条
 */
public class ChestnutBall extends BaseCard {
    public static final String ID = makeID(ChestnutBall.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CardColor.COLORLESS,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.SELF,
            1
    );

    public ChestnutBall() {
        super(ID, info);
        setExhaust(true);
        setEthereal(true, false); // 基础有虚无，升级后去掉
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 施加栗子球Power，本回合完全免疫所有伤害来源
        addToBot(new ApplyPowerAction(p, p, new ChestnutBallPower(p), 1));
    }
}
