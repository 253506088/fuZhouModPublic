package basicmod.patches;

import basicmod.helpers.EndlessDarknessHelper;
import basicmod.helpers.HistoryBookMaskHelper;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.vfx.FastCardObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

/**
 * 无尽黑暗觉醒检查补丁。
 * 岁月史书发放十面具时会临时延后检查，避免动画中途直接触发彩蛋。
 */
public class EndlessDarknessPatch {

    @SpirePatch(clz = ShowCardAndObtainEffect.class, method = "update")
    public static class ShowObtainPatch {
        @SpirePostfixPatch
        public static void Postfix(ShowCardAndObtainEffect __instance) {
            // 岁月史书正在慢速发放十面具时，延后彩蛋检查，避免动画中途被无尽黑暗截胡。
            if (HistoryBookMaskHelper.delayingEndlessDarknessCheck) {
                return;
            }
            if (__instance.isDone) {
                EndlessDarknessHelper.checkAndTriggerAwakening();
            }
        }
    }

    @SpirePatch(clz = FastCardObtainEffect.class, method = "update")
    public static class FastObtainPatch {
        @SpirePostfixPatch
        public static void Postfix(FastCardObtainEffect __instance) {
            // 岁月史书正在慢速发放十面具时，延后彩蛋检查，避免动画中途被无尽黑暗截胡。
            if (HistoryBookMaskHelper.delayingEndlessDarknessCheck) {
                return;
            }
            if (__instance.isDone) {
                EndlessDarknessHelper.checkAndTriggerAwakening();
            }
        }
    }
}
