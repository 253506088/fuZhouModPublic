package basicmod.cards;

import basicmod.actions.BlackHandBitterChoiceAction;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 苦涩的抉择。
 * 黑手帮技能牌，支付生命换取本回合资源，并让下回合全部牌加费。
 */
public class CardBlackHandBitterChoice extends BaseCard {
    public static final String ID = makeID("BlackHandBitterChoice");
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
    public CardBlackHandBitterChoice() {
        super(ID, info);
        tags.add(CustomTags.blackhand);
        setMagic(3, 1);
        setExhaust(true);
    }

    /**
     * 打出后执行苦涩抉择动作。
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new BlackHandBitterChoiceAction(p, 3, this.magicNumber, this.upgraded ? 3 : 2));
    }
}
