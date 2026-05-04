package basicmod.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;

public class PanKuRewardItemPatch {
    @SpireEnum
    public static RewardItem.RewardType PANKU_REWARD_TYPE;

    private static void ensurePendingPanKuReward() {
        if (AbstractDungeon.player == null || AbstractDungeon.getCurrRoom() == null || !AbstractDungeon.getCurrRoom().isBattleOver) {
            return;
        }
        com.megacrit.cardcrawl.relics.AbstractRelic relic = AbstractDungeon.player.getRelic(PanKuBox.ID);
        if (relic instanceof PanKuBox) {
            ((PanKuBox) relic).ensurePendingRewardPresentInCurrentRoom();
        }
    }

    @SpirePatch(clz = CombatRewardScreen.class, method = "setupItemReward")
    public static class RestorePendingOnSetupPatch {
        @SpirePostfixPatch
        public static void Postfix(CombatRewardScreen __instance) {
            ensurePendingPanKuReward();
        }
    }

    @SpirePatch(clz = CombatRewardScreen.class, method = "update")
    public static class RestorePendingOnUpdatePatch {
        @SpirePrefixPatch
        public static void Prefix(CombatRewardScreen __instance) {
            ensurePendingPanKuReward();
        }
    }

    @SpirePatch(clz = ProceedButton.class, method = "update")
    public static class PreventProceedPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(ProceedButton __instance) {
            ensurePendingPanKuReward();
            if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().rewards != null) {
                if (AbstractDungeon.getCurrRoom().isBattleOver) {
                    for (RewardItem reward : AbstractDungeon.getCurrRoom().rewards) {
                        if (reward.type == PANKU_REWARD_TYPE && !reward.isDone && !reward.ignoreReward) {
                            com.megacrit.cardcrawl.helpers.Hitbox hb = basemod.ReflectionHacks.getPrivate(__instance, ProceedButton.class, "hb");
                            if (hb.hovered && com.megacrit.cardcrawl.helpers.input.InputHelper.justClickedLeft) {
                                com.megacrit.cardcrawl.core.CardCrawlGame.sound.play("UI_CLICK_2");
                                AbstractDungeon.effectsQueue.add(new com.megacrit.cardcrawl.vfx.ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0f, "潘库宝盒的魔力尚未平息...", true));
                            }
                            return SpireReturn.Return(); // 强制拦截"继续"按钮
                        }
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }
}
