package basicmod.helpers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;

/**
 * 统一处理主动符咒遗物的触发输入。
 * 鼠标玩家继续使用右键；手柄玩家需要先进入遗物查看状态，再把光标聚焦到遗物上按 B 键触发。
 * 备注：手柄 A 键保留给游戏原本的确认/查看明细语义，B 键在这里等价为“鼠标右键点击遗物”。
 */
public final class TalismanInputHelper {
    /**
     * 工具类不允许实例化。
     */
    private TalismanInputHelper() {
    }

    /**
     * 判断指定遗物本帧是否被玩家当作“右键点击”触发。
     *
     * @param relic 要判断的遗物
     * @return true 表示本帧应该执行该遗物的右键触发逻辑
     */
    public static boolean isRelicRightClickTriggered(AbstractRelic relic) {
        if (relic == null || relic.hb == null || !relic.hb.hovered) {
            return false;
        }

        if (InputHelper.justClickedRight) {
            return true;
        }

        return isControllerRelicCancelTriggered(relic);
    }

    /**
     * 判断手柄是否在“查看遗物”状态下用 B 键触发当前遗物。
     *
     * @param relic 当前被选中的遗物
     * @return true 表示手柄 B 键应等价为鼠标右键点击
     */
    private static boolean isControllerRelicCancelTriggered(AbstractRelic relic) {
        if (!Settings.isControllerMode || AbstractDungeon.player == null) {
            return false;
        }
        if (!AbstractDungeon.player.viewingRelics) {
            return false;
        }
        if (CInputActionSet.proceed.isJustPressed()) {
            BasicMod.logger.info("【符咒手柄输入】检测到结束回合按键，本帧不触发遗物：{}", relic.relicId);
            return false;
        }
        if (!CInputActionSet.cancel.isJustPressed()) {
            return false;
        }

        CInputActionSet.cancel.unpress();
        BasicMod.logger.info("【符咒手柄输入】手柄查看遗物时按 B 触发右键：{}", relic.relicId);
        return true;
    }
}
