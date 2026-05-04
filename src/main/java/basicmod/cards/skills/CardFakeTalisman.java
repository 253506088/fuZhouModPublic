package basicmod.cards.skills;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardFakeTalisman extends BaseCard {
    public static final String ID = makeID("FakeTalisman");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.NONE,
            0);

    public CardFakeTalisman() {
        super(ID, info);
        setMagic(1, 1); // 基础抽1张，升级后增加1张（变为抽2）
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 抽牌数量由 magicNumber 决定
        addToBot(new DrawCardAction(p, magicNumber));
        // 选择 1 张手牌丢弃
        addToBot(new DiscardAction(p, p, 1, false));
    }
}
