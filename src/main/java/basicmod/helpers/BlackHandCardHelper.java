package basicmod.helpers;

import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.List;

public final class BlackHandCardHelper {
    private BlackHandCardHelper() {
    }

    public static boolean isBlackHandCard(AbstractCard card) {
        return card != null && card.hasTag(CustomTags.blackhand);
    }

    public static boolean isAfuOrBlackHandCard(AbstractCard card) {
        return card != null && (card.hasTag(CustomTags.afu) || card.hasTag(CustomTags.blackhand));
    }

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
