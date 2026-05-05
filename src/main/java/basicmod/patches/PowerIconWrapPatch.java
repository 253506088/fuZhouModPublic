package basicmod.patches;

import basemod.ReflectionHacks;
import basicmod.BasicMod;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;

/**
 * 能力图标换行渲染补丁。
 * 负责接管角色能力图标与层数字的绘制，并在单个能力绘制异常时做兜底，避免整局崩溃。
 */
@SpirePatch(clz = AbstractCreature.class, method = "renderPowerIcons")
public class PowerIconWrapPatch {
    /** 每行最多显示的能力图标数量。 */
    private static final int MAX_PER_ROW = 9;
    /** 第一列图标的 X 轴偏移。 */
    private static final float FIRST_ICON_OFFSET_X = 10.0F;
    /** 层数字相对图标的 X 轴偏移。 */
    private static final float AMOUNT_OFFSET_X = 32.0F;
    /** PC 端图标的 Y 轴偏移。 */
    private static final float DESKTOP_ICON_OFFSET_Y = 48.0F;
    /** 移动端图标的 Y 轴偏移。 */
    private static final float MOBILE_ICON_OFFSET_Y = 53.0F;
    /** 层数字的 Y 轴偏移。 */
    private static final float AMOUNT_OFFSET_Y = 66.0F;
    /** 换行后每行之间的 Y 轴间距。 */
    private static final float ROW_OFFSET_Y = 50.0F;
    /** 原版能力图标横向间距。 */
    private static final float POWER_ICON_PADDING_X = ReflectionHacks.getPrivateStatic(AbstractCreature.class, "POWER_ICON_PADDING_X");

    /**
     * 接管原版能力图标渲染入口，按“先图标、后层数”顺序绘制。
     */
    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(AbstractCreature __instance, SpriteBatch sb, float x, float y) {
        Color hbTextColor = ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "hbTextColor");
        renderIcons(__instance, sb, x, y, hbTextColor);
        renderAmounts(__instance, sb, x, y, hbTextColor);
        return SpireReturn.Return(null);
    }

    /**
     * 绘制能力图标。
     */
    private static void renderIcons(AbstractCreature creature, SpriteBatch sb, float x, float y, Color color) {
        for (int i = 0; i < creature.powers.size(); i++) {
            AbstractPower power = creature.powers.get(i);
            float iconX = x + getColumnOffset(i);
            float iconY = y - getIconOffsetY() - getRowOffset(i);
            // 单个能力图标渲染失败时只跳过该能力，避免整局渲染线程崩溃。
            try {
                power.renderIcons(sb, iconX, iconY, color);
            } catch (Exception e) {
                BasicMod.logger.error("渲染能力图标失败。powerId={}, owner={}, mobile={}",
                        power == null ? "null" : power.ID,
                        creature == null ? "null" : creature.name,
                        Settings.isMobile,
                        e);
            }
        }
    }

    /**
     * 绘制能力层数数字。
     */
    private static void renderAmounts(AbstractCreature creature, SpriteBatch sb, float x, float y, Color color) {
        for (int i = 0; i < creature.powers.size(); i++) {
            AbstractPower power = creature.powers.get(i);
            float amountX = x + getColumnOffset(i) + AMOUNT_OFFSET_X * Settings.scale;
            float amountY = y - AMOUNT_OFFSET_Y * Settings.scale - getRowOffset(i);
            // 单个能力层数渲染失败时只跳过该能力，避免影响其他能力显示。
            try {
                power.renderAmount(sb, amountX, amountY, color);
            } catch (Exception e) {
                BasicMod.logger.error("渲染能力层数失败。powerId={}, owner={}, mobile={}",
                        power == null ? "null" : power.ID,
                        creature == null ? "null" : creature.name,
                        Settings.isMobile,
                        e);
            }
        }
    }

    /**
     * 计算指定索引图标的横向偏移。
     */
    private static float getColumnOffset(int index) {
        return (FIRST_ICON_OFFSET_X * Settings.scale) + (index % MAX_PER_ROW) * POWER_ICON_PADDING_X;
    }

    /**
     * 计算指定索引图标所在行的纵向偏移。
     */
    private static float getRowOffset(int index) {
        return (index / MAX_PER_ROW) * ROW_OFFSET_Y * Settings.scale;
    }

    /**
     * 根据平台返回图标基准 Y 轴偏移。
     */
    private static float getIconOffsetY() {
        return (Settings.isMobile ? MOBILE_ICON_OFFSET_Y : DESKTOP_ICON_OFFSET_Y) * Settings.scale;
    }
}
