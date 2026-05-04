package basicmod.cards.demons;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardWindDemonQi extends BaseCard {
    public static final String ID = makeID("CardWindDemonQi");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.ALL_ENEMY,
            1
    );

    public CardWindDemonQi() {
        super(ID, info);
        setMagic(3, 1); // 基础抽3，升级抽4
        setCustomVar("DiscardCnt", 1); // 丢弃数量
        setCustomVar("WindAmt", 2, 1); // 风势数量：2(3)
        tags.add(CustomTags.EIGHT_DEMONS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 抽卡
        addToBot(new DrawCardAction(p, this.magicNumber));
        // 弃卡选1张
        addToBot(new DiscardAction(p, p, customVar("DiscardCnt"), false));
        
        // 施加风势给全体敌人
        for (AbstractMonster mo : com.megacrit.cardcrawl.dungeons.AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                addToBot(new com.megacrit.cardcrawl.actions.common.ApplyPowerAction(mo, p, new basicmod.powers.WindCatalystPower(mo, p, customVar("WindAmt")), customVar("WindAmt")));
            }
        }
    }
}
