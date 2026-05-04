package basicmod.patches;

import basicmod.helpers.TalismanHelper;
import basicmod.relics.TalismanLocator;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;

public class TalismanLocatorPatch {
    private static void ensureLocatorRewardAndLinks() {
        if (AbstractDungeon.player == null || AbstractDungeon.getCurrRoom() == null || AbstractDungeon.getCurrRoom().rewards == null) {
            return;
        }
        if (!AbstractDungeon.player.hasRelic(TalismanLocator.ID)) {
            return;
        }

        RewardItem originalRelic = null;
        RewardItem talismanReward = null;
        RewardItem sapphireKey = null;

        for (RewardItem reward : AbstractDungeon.getCurrRoom().rewards) {
            if (reward == null || reward.isDone || reward.ignoreReward) {
                continue;
            }
            if (reward.type == RewardItem.RewardType.RELIC && reward.relic != null) {
                if (TalismanHelper.ALL_TALISMAN_IDS.contains(reward.relic.relicId)) {
                    if (talismanReward == null) talismanReward = reward;
                } else if (originalRelic == null) {
                    originalRelic = reward;
                }
            } else if (reward.type == RewardItem.RewardType.SAPPHIRE_KEY && sapphireKey == null) {
                sapphireKey = reward;
            }
        }

        if (originalRelic != null && talismanReward == null) {
            AbstractRelic missingTalisman = TalismanHelper.getRandomMissingTalisman();
            if (missingTalisman != null) {
                talismanReward = new RewardItem(missingTalisman);
                int index = AbstractDungeon.getCurrRoom().rewards.indexOf(originalRelic);
                AbstractDungeon.getCurrRoom().rewards.add(index + 1, talismanReward);
                AbstractRelic locator = AbstractDungeon.player.getRelic(TalismanLocator.ID);
                if (locator != null) {
                    locator.flash();
                }
            }
        }

        if (originalRelic != null && talismanReward != null) {
            if (sapphireKey != null) {
                // 三者互斥环（原遗物 -> 符咒 -> 蓝钥匙 -> 原遗物）
                originalRelic.relicLink = talismanReward;
                talismanReward.relicLink = sapphireKey;
                sapphireKey.relicLink = originalRelic;
            } else {
                // 双向互斥
                originalRelic.relicLink = talismanReward;
                talismanReward.relicLink = originalRelic;
            }
        }
    }

    @SpirePatch(clz = CombatRewardScreen.class, method = "setupItemReward")
    public static class SetupItemRewardPatch {
        @SpirePrefixPatch
        public static void Prefix(CombatRewardScreen __instance) {
            ensureLocatorRewardAndLinks();
        }
    }

    @SpirePatch(clz = CombatRewardScreen.class, method = "update")
    public static class RewardScreenUpdatePatch {
        @SpirePrefixPatch
        public static void Prefix(CombatRewardScreen __instance) {
            ensureLocatorRewardAndLinks();
        }
    }
}
