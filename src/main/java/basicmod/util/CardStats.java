package basicmod.util;

import com.megacrit.cardcrawl.cards.AbstractCard;

/**
 * 卡牌属性信息类。
 * 用于存储卡牌的基本属性，简化卡牌构造函数。
 */
public class CardStats {
    /** 基础费用 */
    public final int baseCost;
    /** 卡牌类型 */
    public final AbstractCard.CardType cardType;
    /** 卡牌目标 */
    public final AbstractCard.CardTarget cardTarget;
    /** 卡牌稀有度 */
    public final AbstractCard.CardRarity cardRarity;
    /** 卡牌颜色 */
    public final AbstractCard.CardColor cardColor;

    /**
     * 构造函数。
     *
     * @param cardColor 卡牌颜色
     * @param cardType 卡牌类型
     * @param cardRarity 卡牌稀有度
     * @param cardTarget 卡牌目标
     * @param baseCost 基础费用
     */
    public CardStats(AbstractCard.CardColor cardColor, AbstractCard.CardType cardType, AbstractCard.CardRarity cardRarity, AbstractCard.CardTarget cardTarget, int baseCost)
    {
        this.baseCost = baseCost;
        this.cardType = cardType;
        this.cardTarget = cardTarget;
        this.cardRarity = cardRarity;
        this.cardColor = cardColor;
    }
}