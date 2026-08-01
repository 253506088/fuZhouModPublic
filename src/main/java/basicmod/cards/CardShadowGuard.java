package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.ShadowGuardPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 黑影护卫。
 * 让尼嘉-忍者团造成伤害后为玩家获得格挡。
 */
public class CardShadowGuard extends BaseCard {
    public static final String ID = makeID("ShadowGuard");
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
    public CardShadowGuard() {
        super(ID, info);
        tags.add(CustomTags.MASK_SUPPORT);
    }

    /**
     * 打出后获得黑影护卫能力。
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ShadowGuardPower.apply(p, this.upgraded);
    }
}
