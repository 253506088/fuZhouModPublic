package basicmod.patches;

import basicmod.helpers.FinalBossChoiceManager;
import basicmod.enums.FinalBossChoice;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

public class FinalBossChoiceSelectorPatch {
    private static final float FALLBACK_BASE_Y_RATIO = 0.19F;
    private static final float FALLBACK_CENTER_X_RATIO = 0.5F;
    private static final float ANCHOR_X_FROM_CONFIRM = 110.0F;
    private static final float ANCHOR_Y_FROM_CONFIRM = 148.0F;
    private static final float ARROW_X_OFFSET = 118.0F;
    private static final float ARROW_SIZE = 52.0F;
    private static final float LABEL_Y_OFFSET = 44.0F;

    private static final Hitbox leftHb = new Hitbox(ARROW_SIZE * Settings.scale, ARROW_SIZE * Settings.scale);
    private static final Hitbox rightHb = new Hitbox(ARROW_SIZE * Settings.scale, ARROW_SIZE * Settings.scale);
    private static boolean initialized = false;
    private static AbstractPlayer.PlayerClass lastSelectedClass = null;

    @SpirePatch(clz = CharacterSelectScreen.class, method = "open")
    public static class OpenPatch {
        @SpirePostfixPatch
        public static void Postfix(CharacterSelectScreen __instance, boolean isTrial) {
            initialized = false;
            lastSelectedClass = null;
            FinalBossChoiceManager.clearPendingChoice();
        }
    }

    @SpirePatch(clz = CharacterSelectScreen.class, method = "update")
    public static class UpdatePatch {
        @SpirePostfixPatch
        public static void Postfix(CharacterSelectScreen __instance) {
            if (!isOnCharSelect()) {
                return;
            }
            AbstractPlayer.PlayerClass selectedClass = getSelectedSupportedClass(__instance);
            if (selectedClass == null) {
                return;
            }
            updateSelectedClass(selectedClass);
            ensureInit();
            updateHitboxes(__instance);

            if (InputHelper.justClickedLeft) {
                if (leftHb.hovered) {
                    cycle(selectedClass, -1);
                    leftHb.clickStarted = true;
                } else if (rightHb.hovered) {
                    cycle(selectedClass, 1);
                    rightHb.clickStarted = true;
                }
            }
        }
    }

    @SpirePatch(clz = CharacterSelectScreen.class, method = "render")
    public static class RenderPatch {
        @SpirePostfixPatch
        public static void Postfix(CharacterSelectScreen __instance, SpriteBatch sb) {
            if (!isOnCharSelect()) {
                return;
            }
            AbstractPlayer.PlayerClass selectedClass = getSelectedSupportedClass(__instance);
            if (selectedClass == null) {
                return;
            }
            updateSelectedClass(selectedClass);
            ensureInit();
            float[] anchor = getAnchor(__instance);
            float centerX = anchor[0];
            float baseY = anchor[1];

            UIStrings ui = CardCrawlGame.languagePack == null ? null : CardCrawlGame.languagePack.getUIString("fuZhouMod:FinalBossChoiceUI");
            String[] txt = ui != null ? ui.TEXT : null;
            String title = txt != null && txt.length > 0 ? txt[0] : "Final Boss";
            String heart = txt != null && txt.length > 1 ? txt[1] : "Heart";
            String dad = txt != null && txt.length > 2 ? txt[2] : "Grand Mage Dad";
            String random = txt != null && txt.length > 3 ? txt[3] : "Random";

            FinalBossChoice choice = FinalBossChoiceManager.getSelectableChoice(selectedClass);
            String choiceText = choice == FinalBossChoice.HEART ? heart : choice == FinalBossChoice.DAD ? dad : random;

            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, title, centerX, baseY + LABEL_Y_OFFSET * Settings.scale, Settings.GOLD_COLOR);
            FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, choiceText, centerX, baseY, Settings.CREAM_COLOR);

            Color leftColor = leftHb.hovered ? Settings.GOLD_COLOR : Settings.CREAM_COLOR;
            Color rightColor = rightHb.hovered ? Settings.GOLD_COLOR : Settings.CREAM_COLOR;
            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, "<", leftHb.cX, leftHb.cY + 4.0F * Settings.scale, leftColor);
            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, ">", rightHb.cX, rightHb.cY + 4.0F * Settings.scale, rightColor);
        }
    }

    private static boolean isOnCharSelect() {
        return CardCrawlGame.mainMenuScreen != null
                && CardCrawlGame.mainMenuScreen.screen == MainMenuScreen.CurScreen.CHAR_SELECT;
    }

    private static AbstractPlayer.PlayerClass getSelectedSupportedClass(CharacterSelectScreen screen) {
        if (screen == null || screen.options == null) {
            return null;
        }
        for (CharacterOption option : screen.options) {
            if (option == null || !option.selected || option.c == null) {
                continue;
            }
            AbstractPlayer.PlayerClass playerClass = option.c.chosenClass;
            return FinalBossChoiceManager.canChooseFinalBoss(playerClass) ? playerClass : null;
        }
        return null;
    }

    private static void updateSelectedClass(AbstractPlayer.PlayerClass selectedClass) {
        if (lastSelectedClass == selectedClass) {
            return;
        }
        lastSelectedClass = selectedClass;
        FinalBossChoiceManager.clearPendingChoice();
    }

    private static void ensureInit() {
        if (initialized) {
            return;
        }
        updateHitboxes(null);
        initialized = true;
    }

    private static void updateHitboxes(CharacterSelectScreen screen) {
        float[] anchor = getAnchor(screen);
        float centerX = anchor[0];
        float baseY = anchor[1];
        leftHb.move(centerX - ARROW_X_OFFSET * Settings.scale, baseY);
        rightHb.move(centerX + ARROW_X_OFFSET * Settings.scale, baseY);
        leftHb.update();
        rightHb.update();
    }

    private static float[] getAnchor(CharacterSelectScreen screen) {
        float centerX = Settings.WIDTH * FALLBACK_CENTER_X_RATIO;
        float baseY = Settings.HEIGHT * FALLBACK_BASE_Y_RATIO;
        if (screen != null && screen.confirmButton != null && screen.confirmButton.hb != null) {
            centerX = screen.confirmButton.hb.cX - ANCHOR_X_FROM_CONFIRM * Settings.scale;
            baseY = screen.confirmButton.hb.cY + ANCHOR_Y_FROM_CONFIRM * Settings.scale;
        }
        centerX = Math.max(220.0F * Settings.scale, Math.min(Settings.WIDTH - 220.0F * Settings.scale, centerX));
        baseY = Math.max(95.0F * Settings.scale, Math.min(Settings.HEIGHT - 160.0F * Settings.scale, baseY));
        return new float[] { centerX, baseY };
    }

    private static void cycle(AbstractPlayer.PlayerClass selectedClass, int delta) {
        FinalBossChoice[] vals = FinalBossChoice.values();
        FinalBossChoice current = FinalBossChoiceManager.getSelectableChoice(selectedClass);
        int idx = current.ordinal();
        int next = (idx + delta + vals.length) % vals.length;
        FinalBossChoiceManager.setPendingChoice(vals[next]);
    }
}
