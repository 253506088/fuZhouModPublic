package basicmod.helpers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

/**
 * 卡牌奖励工具。
 * 负责把卡牌先可靠写入主牌组，再按需播放只展示不发奖的视觉效果。
 */
public final class CardRewardHelper {
    /**
     * 工具类不允许实例化。
     */
    private CardRewardHelper() {
    }

    /**
     * 把卡牌立即加入主牌组，并通知遗物牌组发生变化。
     */
    public static boolean grantCardToMasterDeck(AbstractCard card) {
        if (card == null || AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) {
            return false;
        }

        AbstractDungeon.player.masterDeck.addToTop(card);
        UnlockTracker.markCardAsSeen(card.cardID);
        if (AbstractDungeon.player.relics != null) {
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                relic.onMasterDeckChange();
            }
        }
        return true;
    }

    /**
     * 播放卡牌展示特效；这个特效只负责展示，不再承担真正入组。
     */
    public static void showCardBriefly(AbstractCard card) {
        if (card == null) {
            return;
        }

        AbstractDungeon.topLevelEffectsQueue.add(new ShowCardBrieflyEffect(
                card.makeStatEquivalentCopy(),
                Settings.WIDTH / 2.0F,
                Settings.HEIGHT / 2.0F
        ));
    }
}
