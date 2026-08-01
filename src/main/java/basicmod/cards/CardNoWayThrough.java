package basicmod.cards;

import basicmod.cards.shadowkhan.NiJiaNinja;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.helpers.NiJiaSupportHelper;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 此路不通。
 * 获得格挡，并生成两张尼嘉-忍者团。
 */
public class CardNoWayThrough extends BaseCard {
    public static final String ID = makeID("NoWayThrough");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.SELF,
            1
    );

    /**
     * 构造函数。
     */
    public CardNoWayThrough() {
        super(ID, info);
        tags.add(CustomTags.MASK_SUPPORT);
        setBlock(6, 3);
    }

    /**
     * 打出后获得格挡，并加入两张尼嘉-忍者团到手牌。
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, this.block));
        for (int i = 0; i < 2; i++) {
            AbstractCard card = new NiJiaNinja();
            if (this.upgraded) {
                card.upgrade();
            }
            NiJiaSupportHelper.prepareGeneratedNiJiaCard(card);
            addToBot(new MakeTempCardInHandAction(card, 1));
        }
    }
}
