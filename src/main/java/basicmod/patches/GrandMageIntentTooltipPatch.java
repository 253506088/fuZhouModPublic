package basicmod.patches;

import basicmod.monsters.GrandMageDad;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.RunicDome;

import java.util.ArrayList;
import java.lang.reflect.Field;

@SpirePatch(clz = AbstractMonster.class, method = "createIntent")
public class GrandMageIntentTooltipPatch {
    private static Field intentTipField;
    private static boolean initialized = false;
    /** 大法师老爹悬浮提示每帧复用的列表，避免反复创建临时 ArrayList。 */
    private static final ArrayList<PowerTip> TIPS_BUFFER = new ArrayList<>();

    @SpirePostfixPatch
    public static void Postfix(AbstractMonster __instance) {
        applyGrandMageIntentTooltip(__instance);
    }

    @SpirePatch(clz = AbstractMonster.class, method = "renderTip")
    public static class GrandMageRenderTipPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AbstractMonster __instance, SpriteBatch sb) {
            if (!(__instance instanceof GrandMageDad)) {
                return SpireReturn.Continue();
            }

            renderGrandMageTips(__instance);
            return SpireReturn.Return(null);
        }
    }

    private static PowerTip getIntentTip(AbstractMonster monster) {
        if (!initialized) {
            try {
                intentTipField = AbstractMonster.class.getDeclaredField("intentTip");
                intentTipField.setAccessible(true);
            } catch (Exception ignored) {
                intentTipField = null;
            } finally {
                initialized = true;
            }
        }
        if (intentTipField == null) {
            return null;
        }
        try {
            return (PowerTip) intentTipField.get(monster);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    private static void applyGrandMageIntentTooltip(AbstractMonster monster) {
        if (!(monster instanceof GrandMageDad)) {
            return;
        }
        PowerTip intentTip = getIntentTip(monster);
        if (intentTip == null) {
            return;
        }
        GrandMageDad dad = (GrandMageDad) monster;
        intentTip.header = dad.getIntentTooltipTitle();
        intentTip.body = dad.getIntentTooltipBody();
    }

    private static void renderGrandMageTips(AbstractMonster monster) {
        applyGrandMageIntentTooltip(monster);
        ArrayList<PowerTip> tips = TIPS_BUFFER;
        tips.clear();
        PowerTip intentTip = getIntentTip(monster);
        boolean showIntent = monster.intentAlphaTarget == 1.0F
                && (AbstractDungeon.player == null || !AbstractDungeon.player.hasRelic(RunicDome.ID))
                && monster.intent != AbstractMonster.Intent.NONE;
        if (showIntent && intentTip != null) {
            tips.add(intentTip);
        }

        for (AbstractPower power : monster.powers) {
            if (power.region48 != null) {
                tips.add(new PowerTip(power.name, power.description, power.region48));
            } else {
                tips.add(new PowerTip(power.name, power.description, power.img));
            }
        }

        if (tips.isEmpty() || monster.hb == null) {
            return;
        }

        float x = Math.max(20.0F * Settings.scale, Settings.WIDTH - 340.0F * Settings.scale);
        float y = monster.hb.cY + TipHelper.calculateAdditionalOffset(tips, monster.hb.cY);
        TipHelper.queuePowerTips(x, y, tips);
    }
}
