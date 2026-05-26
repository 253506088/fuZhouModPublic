package basicmod.cards;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.DominionPower;
import basicmod.powers.ShadowDecreePower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardShadowDecree extends BaseCard {
    public static final String ID = makeID("ShadowDecree");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.POWER,
            CardRarity.RARE,
            CardTarget.SELF,
            3
    );

    public CardShadowDecree() {
        super(ID, info);
        tags.add(CustomTags.MASK_SUPPORT);
        // 升级后费用 3 -> 2。
        setCostUpgrade(2);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        BasicMod.logger.info("【黑影敕令】打出：获得影噬=4，并施加黑影敕令状态");
        addToBot(new ApplyPowerAction(p, p, new DominionPower(p, 4), 4));
        addToBot(new ApplyPowerAction(p, p, new ShadowDecreePower(p, 1), 1));
    }
}
