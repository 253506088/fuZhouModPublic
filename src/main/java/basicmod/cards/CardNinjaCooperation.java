package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.NinjaCooperationPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 忍者协作。
 * 消耗堆每有一张尼嘉-忍者团，尼嘉-忍者团每段伤害提高。
 */
public class CardNinjaCooperation extends BaseCard {
    public static final String ID = makeID("NinjaCooperation");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.POWER,
            CardRarity.RARE,
            CardTarget.SELF,
            1
    );

    /**
     * 构造函数。
     */
    public CardNinjaCooperation() {
        super(ID, info);
        tags.add(CustomTags.MASK_SUPPORT);
        setInnate(false, true);
    }

    /**
     * 打出后获得忍者协作能力。
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new NinjaCooperationPower(p, 1), 1));
    }
}
