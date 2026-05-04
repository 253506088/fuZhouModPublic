package basicmod.patches;

import basicmod.monsters.GrandMageDad;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

@SpirePatch(clz = AbstractMonster.class, method = "renderIntent")
public class GrandMageSecondaryIntentRenderPatch {
    @SpirePostfixPatch
    public static void Postfix(AbstractMonster __instance, SpriteBatch sb) {
        if (!(__instance instanceof GrandMageDad)) {
            return;
        }
        GrandMageDad dad = (GrandMageDad) __instance;
        int code = dad.getSecondaryIntentCodeForRender();
        Texture icon = resolveSecondaryIntentIcon(code);
        if (icon == null || __instance.intentHb == null) {
            return;
        }

        float size = 50.0F * Settings.scale;
        float x = __instance.intentHb.cX + 72.0F * Settings.scale + __instance.intentOffsetX - size / 2.0F;
        float y = __instance.intentHb.cY - size / 2.0F;
        float alpha = Math.max(0.0F, Math.min(1.0F, __instance.intentAlpha));

        sb.setColor(new Color(1.0F, 1.0F, 1.0F, alpha));
        sb.draw(icon, x, y, size, size);
        sb.setColor(Color.WHITE);
    }

    private static Texture resolveSecondaryIntentIcon(int code) {
        switch (code) {
            case GrandMageDad.SEC_CURSE_JACKIE:
                return ImageMaster.INTENT_DEBUFF;
            case GrandMageDad.SEC_DEFEND_ARTIFACT:
                return ImageMaster.INTENT_DEFEND_BUFF;
            case GrandMageDad.SEC_RANDOM_CURSE:
                return ImageMaster.INTENT_DEBUFF2;
            case GrandMageDad.SEC_CLEANSE_BUFF:
                return ImageMaster.INTENT_BUFF;
            case GrandMageDad.SEC_DOUBLE_CURSE:
                return ImageMaster.INTENT_DEBUFF;
            default:
                return null;
        }
    }
}

