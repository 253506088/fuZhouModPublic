package basicmod.helpers;

import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.List;

/**
 * 黑手党卡牌辅助类。
 * 提供黑手党卡牌的判断和统计功能。
 */
public final class BlackHandCardHelper {
    private BlackHandCardHelper() {
    }

    /**
     * 判断卡牌是否为黑手党卡牌。
     *
     * @param card 要判断的卡牌
     * @return 如果是黑手党卡牌返回true
     */
    public static boolean isBlackHandCard(AbstractCard card) {
        return card != null && card.hasTag(CustomTags.blackhand);
    }

    /**
     * 判断卡牌是否为阿福或黑手党卡牌。
     *
     * @param card 要判断的卡牌
     * @return 如果是阿福或黑手党卡牌返回true
     */
    public static boolean isAfuOrBlackHandCard(AbstractCard card) {
        return card != null && (card.hasTag(CustomTags.afu) || card.hasTag(CustomTags.blackhand));
    }

    /**
     * 统计主牌组中黑手党卡牌的数量。
     *
     * @return 黑手党卡牌数量
     */
    public static int countBlackHandInMasterDeck() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) {
            return 0;
        }
        int count = 0;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (isBlackHandCard(c)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 统计本回合打出的黑手党卡牌数量。
     *
     * @param excludeCard 要排除的卡牌
     * @return 本回合打出的黑手党卡牌数量
     */
    public static int countBlackHandPlayedThisTurn(AbstractCard excludeCard) {
        if (AbstractDungeon.actionManager == null) {
            return 0;
        }
        int count = 0;
        List<AbstractCard> playedThisTurn = AbstractDungeon.actionManager.cardsPlayedThisTurn;
        for (AbstractCard c : playedThisTurn) {
            if (c != excludeCard && isBlackHandCard(c)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 获取本回合打出的上一张卡牌。
     *
     * @return 上一张打出的卡牌，如果没有则返回null
     */
    public static AbstractCard getPreviousCardPlayedThisTurn() {
        if (AbstractDungeon.actionManager == null) {
            return null;
        }
        List<AbstractCard> playedThisTurn = AbstractDungeon.actionManager.cardsPlayedThisTurn;
        if (playedThisTurn == null || playedThisTurn.size() < 2) {
            return null;
        }
        return playedThisTurn.get(playedThisTurn.size() - 2);
    }
}
