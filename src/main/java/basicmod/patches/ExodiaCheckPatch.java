package basicmod.patches;

import basicmod.BasicMod;
import basicmod.cards.colorless.ExodiaHelper;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.GameActionManager;

/**
 * 艾克佐迪亚集齐检测兜底 Patch。
 *
 * 在 GameActionManager.update 后置每帧执行一次集齐检测，
 * 这样无论卡牌通过何种方式（搜牌、复活、塞回手）进入手牌，
 * 都能在动作执行结束的下一帧立刻触发 Exodia 胜利，
 * 不再需要等玩家再打出一张卡才被 triggerOnOtherCardPlayed 巡到。
 *
 * 由于 ExodiaHelper 内部有 triggered 静态标记，触发后立即 return，性能可忽略。
 */
public class ExodiaCheckPatch {

    @SpirePatch(clz = GameActionManager.class, method = "update")
    public static class GameActionManagerUpdatePatch {
        @SpirePostfixPatch
        public static void Postfix(GameActionManager __instance) {
            try {
                ExodiaHelper.checkExodiaComplete();
            } catch (Throwable t) {
                // 兜底：任何异常都不要影响主流程
                BasicMod.logger.error("【艾克佐迪亚检测Patch】异常：" + t.getMessage(), t);
            }
        }
    }
}
