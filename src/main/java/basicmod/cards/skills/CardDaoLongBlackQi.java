package basicmod.cards.skills;

import basicmod.actions.DaoLongBlackQiAction;
import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardDaoLongBlackQi extends BaseCard {
    public static final String ID = makeID("DaoLongBlackQi");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.NONE,
            2);

    public CardDaoLongBlackQi() {
        super(ID, info);
        setExhaust(true); // 消耗牌
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DaoLongBlackQiAction(this.upgraded));
    }
}
