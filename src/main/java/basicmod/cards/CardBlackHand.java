package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.helpers.BlackHandCardHelper;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 黑手党卡牌。
 * 升级手中所有阿福和黑手党卡牌。
 */
public class CardBlackHand extends BaseCard {
    /** 卡牌ID */
    public static final String ID = makeID("BlackHand");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.NONE,
            1);

    public CardBlackHand() {
        super(ID, info);
        setExhaust(true);
        setCostUpgrade(0);
        tags.add(CustomTags.blackhand);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                for (AbstractCard c : p.hand.group) {
                    if (BlackHandCardHelper.isAfuOrBlackHandCard(c) && c.canUpgrade()) {
                        c.upgrade();
                        c.superFlash();
                    }
                }
                this.isDone = true;
            }
        });
    }
}
