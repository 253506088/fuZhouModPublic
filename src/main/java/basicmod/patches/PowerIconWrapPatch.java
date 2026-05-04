package basicmod.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;

@SpirePatch(clz = AbstractCreature.class, method = "renderPowerIcons")
public class PowerIconWrapPatch {
    private static final int MAX_PER_ROW = 9;
    private static final float FIRST_ICON_OFFSET_X = 10.0F;
    private static final float AMOUNT_OFFSET_X = 32.0F;
    private static final float DESKTOP_ICON_OFFSET_Y = 48.0F;
    private static final float MOBILE_ICON_OFFSET_Y = 53.0F;
    private static final float AMOUNT_OFFSET_Y = 66.0F;
    private static final float ROW_OFFSET_Y = 50.0F;
    private static final float POWER_ICON_PADDING_X = ReflectionHacks.getPrivateStatic(AbstractCreature.class, "POWER_ICON_PADDING_X");

    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(AbstractCreature __instance, SpriteBatch sb, float x, float y) {
        Color hbTextColor = ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "hbTextColor");
        renderIcons(__instance, sb, x, y, hbTextColor);
        renderAmounts(__instance, sb, x, y, hbTextColor);
        return SpireReturn.Return(null);
    }

    private static void renderIcons(AbstractCreature creature, SpriteBatch sb, float x, float y, Color color) {
        for (int i = 0; i < creature.powers.size(); i++) {
            AbstractPower power = creature.powers.get(i);
            float iconX = x + getColumnOffset(i);
            float iconY = y - getIconOffsetY() - getRowOffset(i);
            power.renderIcons(sb, iconX, iconY, color);
        }
    }

    private static void renderAmounts(AbstractCreature creature, SpriteBatch sb, float x, float y, Color color) {
        for (int i = 0; i < creature.powers.size(); i++) {
            AbstractPower power = creature.powers.get(i);
            float amountX = x + getColumnOffset(i) + AMOUNT_OFFSET_X * Settings.scale;
            float amountY = y - AMOUNT_OFFSET_Y * Settings.scale - getRowOffset(i);
            power.renderAmount(sb, amountX, amountY, color);
        }
    }

    private static float getColumnOffset(int index) {
        return (FIRST_ICON_OFFSET_X * Settings.scale) + (index % MAX_PER_ROW) * POWER_ICON_PADDING_X;
    }

    private static float getRowOffset(int index) {
        return (index / MAX_PER_ROW) * ROW_OFFSET_Y * Settings.scale;
    }

    private static float getIconOffsetY() {
        return (Settings.isMobile ? MOBILE_ICON_OFFSET_Y : DESKTOP_ICON_OFFSET_Y) * Settings.scale;
    }
}
