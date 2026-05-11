package basicmod.cards.demons;

import basicmod.cards.BaseCard;
import basicmod.powers.MoonUncapPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 月之恶魔的第三个选项：让玩家本回合内造成的恶魔异常伤害解除上限。
 */
public class MoonChoiceUncap extends BaseCard {
    public static final String ID = makeID("MoonChoiceUncap");
    private static final CardStats info = new CardStats(
            CardColor.COLORLESS,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.NONE,
            -2
    );

    /**
     * 构造一个仅用于选择界面的临时选项牌。
     */
    public MoonChoiceUncap() {
        super(ID, info);
    }

    /**
     * 选项牌不直接通过 use 结算，真正效果在 onChoseThisOption 中触发。
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {}

    /**
     * 选择该选项后，给玩家施加一个持续到本回合结束的“解除限制”能力。
     */
    @Override
    public void onChoseThisOption() {
        AbstractPlayer player = AbstractDungeon.player;
        addToBot(new ApplyPowerAction(player, player, new MoonUncapPower(player)));
    }
}
