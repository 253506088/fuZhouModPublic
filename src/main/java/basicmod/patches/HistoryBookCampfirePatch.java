package basicmod.patches;

import basemod.ReflectionHacks;
import basicmod.helpers.HistoryBookRewriteManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;

import java.util.ArrayList;

/**
 * 岁月史书营火补丁。
 * 当残卷开启“本次休息处可多选”时，让营火按钮在选择后重新开放。
 */
public class HistoryBookCampfirePatch {
    private static boolean optionWasReadyBeforeUpdate = false;
    private static AbstractCampfireOption touchOptionBeforeUpdate = null;

    @SpirePatch(clz = AbstractCampfireOption.class, method = "update")
    /**
     * 营火选项点击记录补丁。
     */
    public static class OptionUsePatch {
        /**
         * 营火选项更新前，记录该按钮本帧是否处于可点击状态。
         */
        @SpirePrefixPatch
        public static void Prefix(AbstractCampfireOption __instance) {
            optionWasReadyBeforeUpdate = HistoryBookRewriteManager.campfireMultiSelect
                    && __instance.usable
                    && AbstractDungeon.getCurrRoom() instanceof RestRoom
                    && !((RestRoom) AbstractDungeon.getCurrRoom()).campfireUI.somethingSelected;
        }

        /**
         * 营火选项更新后，若本帧触发了选择，则记录该选项已经用过。
         */
        @SpirePostfixPatch
        public static void Postfix(AbstractCampfireOption __instance) {
            if (optionWasReadyBeforeUpdate
                    && AbstractDungeon.getCurrRoom() instanceof RestRoom
                    && ((RestRoom) AbstractDungeon.getCurrRoom()).campfireUI.somethingSelected) {
                HistoryBookRewriteManager.rememberUsedCampfireOption(__instance);
            }
            optionWasReadyBeforeUpdate = false;
        }
    }

    @SpirePatch(clz = CampfireUI.class, method = "update")
    /**
     * 营火多选的具体补丁入口。
     */
    public static class MultiSelectPatch {
        /**
         * 营火界面更新前，记录触屏确认按钮当前指向的选项。
         */
        @SpirePrefixPatch
        public static void Prefix(CampfireUI __instance) {
            if (HistoryBookRewriteManager.campfireMultiSelect) {
                touchOptionBeforeUpdate = __instance.touchOption;
            } else {
                touchOptionBeforeUpdate = null;
            }
        }

        /**
         * 岁月史书开启营火多选后，营火选项执行完会重新开放，直到玩家离开房间。
         */
        @SpirePostfixPatch
        public static void Postfix(CampfireUI __instance) {
            if (!HistoryBookRewriteManager.campfireMultiSelect) {
                return;
            }
            if (__instance.somethingSelected && !AbstractDungeon.isScreenUp) {
                HistoryBookRewriteManager.rememberUsedCampfireOption(touchOptionBeforeUpdate);
                ArrayList<AbstractCampfireOption> buttons = ReflectionHacks.getPrivate(__instance, CampfireUI.class, "buttons");
                if (buttons != null) {
                    buttons.clear();
                }
                if (AbstractDungeon.getCurrRoom() != null) {
                    AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                }
                ReflectionHacks.privateMethod(CampfireUI.class, "initializeButtons").invoke(__instance);
                buttons = ReflectionHacks.getPrivate(__instance, CampfireUI.class, "buttons");
                HistoryBookRewriteManager.disableUsedCampfireOptions(buttons);
                __instance.reopen();
            }
            touchOptionBeforeUpdate = null;
        }
    }
}
