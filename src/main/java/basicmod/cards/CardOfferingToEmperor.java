package basicmod.cards;

import basicmod.actions.ObtainRandomPotionAction;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 给皇帝的供奉。
 * 回复生命并获得一瓶随机药水。
 */
public class CardOfferingToEmperor extends BaseCard {
    public static final String ID = makeID("OfferingToEmperor");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            1
    );

    /**
     * 构造函数。
     */
    public CardOfferingToEmperor() {
        super(ID, info);
        setMagic(5, 3);
        setExhaust(true);
    }

    /**
     * 打出后恢复生命并获得随机药水。
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new HealAction(p, p, this.magicNumber));
        addToBot(new ObtainRandomPotionAction(p));
    }
}
