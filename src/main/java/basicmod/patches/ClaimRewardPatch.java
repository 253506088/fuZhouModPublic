package basicmod.patches;

import basicmod.helpers.TalismanHelper;
import basicmod.relics.TalismanLocator;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;

@SpirePatch(clz = RewardItem.class, method = "claimReward")
public class ClaimRewardPatch {
    private static void handleLocatorFallback(RewardItem claimed) {
        if (AbstractDungeon.player == null || AbstractDungeon.getCurrRoom() == null || !AbstractDungeon.player.hasRelic(TalismanLocator.ID)) {
            return;
        }
        if (AbstractDungeon.getCurrRoom().rewards == null) {
            return;
        }

        RewardItem normalRelic = null;
        RewardItem talismanRelic = null;
        RewardItem sapphireKey = null;

        for (RewardItem reward : AbstractDungeon.getCurrRoom().rewards) {
            if (reward == null || reward.isDone || reward.ignoreReward) {
                continue;
            }
            if (reward.type == RewardItem.RewardType.RELIC && reward.relic != null) {
                if (TalismanHelper.ALL_TALISMAN_IDS.contains(reward.relic.relicId)) {
                    if (talismanRelic == null) talismanRelic = reward;
                } else if (normalRelic == null) {
                    normalRelic = reward;
                }
            } else if (reward.type == RewardItem.RewardType.SAPPHIRE_KEY && sapphireKey == null) {
                sapphireKey = reward;
            }
        }

        if (normalRelic == null || talismanRelic == null) {
            return;
        }

        boolean claimedInGroup = claimed == normalRelic || claimed == talismanRelic || (sapphireKey != null && claimed == sapphireKey);
        if (!claimedInGroup) {
            return;
        }

        if (normalRelic != claimed) {
            normalRelic.isDone = true;
            normalRelic.ignoreReward = true;
        }
        if (talismanRelic != claimed) {
            talismanRelic.isDone = true;
            talismanRelic.ignoreReward = true;
        }
        if (sapphireKey != null && sapphireKey != claimed) {
            sapphireKey.isDone = true;
            sapphireKey.ignoreReward = true;
        }
    }

    @SpirePostfixPatch
    public static boolean Postfix(boolean __result, RewardItem __instance) {
        if (__result) {
            // 当这个物品被成功领取后，根据 relicLink 寻找所有互相绑定的互斥奖励并将其标记为已完成（销毁）。
            // 游戏原生只往下一个节点寻找一次，这里改为循环遍历直到回到自己或遇到空节点，实现了更强的群组互斥支持。
            RewardItem ptr = __instance.relicLink;
            while (ptr != null && ptr != __instance) {
                ptr.isDone = true;
                ptr.ignoreReward = true;
                ptr = ptr.relicLink;
            }
            // 奖励页SL后若relicLink丢失，仍强制执行符咒探测仪的互斥规则
            handleLocatorFallback(__instance);
        }
        return __result;
    }
}
