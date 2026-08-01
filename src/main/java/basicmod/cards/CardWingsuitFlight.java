package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.WingsuitFlightPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 翼装飞行。
 * 让尼嘉-忍者团保留，并在每两张尼嘉-忍者团后获得临时影噬。
 */
public class CardWingsuitFlight extends BaseCard {
    public static final String ID = makeID("WingsuitFlight");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.POWER,
            CardRarity.COMMON,
            CardTarget.SELF,
            1
    );

    /**
     * 构造函数。
     */
    public CardWingsuitFlight() {
        super(ID, info);
        tags.add(CustomTags.MASK_SUPPORT);
        setCostUpgrade(0);
    }

    /**
     * 打出后获得翼装飞行能力。
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new WingsuitFlightPower(p, 1), 1));
    }
}
