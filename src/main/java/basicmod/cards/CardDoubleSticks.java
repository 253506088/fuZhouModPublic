package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.DominionPower;
import basicmod.powers.DoubleSticksPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 双棍。
 * 让尼嘉-忍者团的影噬伤害加成变为三倍。
 */
public class CardDoubleSticks extends BaseCard {
    public static final String ID = makeID("DoubleSticks");
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
    public CardDoubleSticks() {
        super(ID, info);
        tags.add(CustomTags.MASK_SUPPORT);
    }

    /**
     * 打出后获得双棍能力，升级版额外获得 1 层影噬。
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new DoubleSticksPower(p, 1), 1));
        if (this.upgraded) {
            addToBot(new ApplyPowerAction(p, p, new DominionPower(p, 1), 1));
        }
    }
}
