package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.SmokeBombPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 烟雾弹。
 * 让尼嘉-忍者团命中时临时降低目标力量。
 */
public class CardSmokeBomb extends BaseCard {
    public static final String ID = makeID("SmokeBomb");
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
    public CardSmokeBomb() {
        super(ID, info);
        tags.add(CustomTags.MASK_SUPPORT);
    }

    /**
     * 打出后获得烟雾弹能力。
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        SmokeBombPower.apply(p, this.upgraded);
    }
}
