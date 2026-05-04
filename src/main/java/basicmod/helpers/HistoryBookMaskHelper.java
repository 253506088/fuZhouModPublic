package basicmod.helpers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 岁月史书面具奖励工具。
 * 负责统计、删除已有面具，并配合慢速加牌逻辑延后无尽黑暗检查。
 */
public class HistoryBookMaskHelper {
    public static final List<String> ALL_MASK_IDS = Arrays.asList(
            BasicMod.makeID("NiJiaMask"),
            BasicMod.makeID("LaZuoMask"),
            BasicMod.makeID("SaMoMask"),
            BasicMod.makeID("BaTeMask"),
            BasicMod.makeID("KaBoMask"),
            BasicMod.makeID("LeiSuMask"),
            BasicMod.makeID("ManNiMask"),
            BasicMod.makeID("MingTaMask"),
            BasicMod.makeID("YiKaMask"),
            BasicMod.makeID("TaLaMask")
    );

    public static boolean delayingEndlessDarknessCheck = false;

    /**
     * 按面具种类删除玩家已有面具，并按种类数量增加最大生命值。
     */
    public static int removeOwnedMaskTypesAndGainMaxHp(int maxHpPerType) {
        if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) {
            return 0;
        }

        int removedTypes = 0;
        for (String maskId : ALL_MASK_IDS) {
            AbstractCard card = findOneMaskToRemove(maskId);
            if (card != null) {
                AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(card, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                AbstractDungeon.player.masterDeck.removeCard(card);
                AbstractDungeon.player.increaseMaxHp(maxHpPerType, true);
                removedTypes++;
            }
        }
        return removedTypes;
    }

    /**
     * 创建一张基础版面具卡，找不到模板时返回 null。
     */
    public static AbstractCard makeMaskCopy(String maskId) {
        AbstractCard template = CardLibrary.getCard(maskId);
        return template == null ? null : template.makeCopy();
    }

    /**
     * 批量加面具结束后，再统一检查一次无尽黑暗，避免第十张刚入组时立刻打断动画。
     */
    public static void finishDelayedEndlessDarknessCheck() {
        delayingEndlessDarknessCheck = false;
        EndlessDarknessHelper.checkAndTriggerAwakening();
    }

    /**
     * 查找某一类面具要删除的卡，优先删除未升级版本。
     */
    private static AbstractCard findOneMaskToRemove(String maskId) {
        AbstractCard upgraded = null;
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (maskId.equals(card.cardID)) {
                if (!card.upgraded) {
                    return card;
                }
                if (upgraded == null) {
                    upgraded = card;
                }
            }
        }
        return upgraded;
    }

    /**
     * 返回十面具 ID 的新列表，避免外部误改常量。
     */
    public static ArrayList<String> getAllMaskIdsCopy() {
        return new ArrayList<>(ALL_MASK_IDS);
    }
}
