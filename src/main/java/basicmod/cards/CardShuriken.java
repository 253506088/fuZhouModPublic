package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.ShurikenPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 手里剑。
 * 改变尼嘉-忍者团的攻击次数。
 */
public class CardShuriken extends BaseCard {
    public static final String ID = makeID("Shuriken");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.POWER,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            1
    );

    /**
     * 构造函数。
     */
    public CardShuriken() {
        super(ID, info);
        tags.add(CustomTags.MASK_SUPPORT);
    }

    /**
     * 打出后获得手里剑能力。
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int hitCount = this.upgraded ? 3 : 2;
        addToBot(new ApplyPowerAction(p, p, new ShurikenPower(p, hitCount), hitCount));
    }
}
