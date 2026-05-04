package basicmod.helpers;

import basemod.abstracts.CustomSavable;
import basicmod.BasicMod;
import basicmod.enums.FinalBossChoice;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class FinalBossChoiceManager implements CustomSavable<Integer> {
    public static final String SAVE_KEY = BasicMod.makeID("FinalBossChoice");
    private static final FinalBossChoiceManager INSTANCE = new FinalBossChoiceManager();

    private static FinalBossChoice defaultChoice = FinalBossChoice.DAD;
    private static FinalBossChoice runChoice = null;
    private static FinalBossChoice resolvedChoice = null;
    private static FinalBossChoice pendingChoice = null;

    public static FinalBossChoiceManager getInstance() {
        return INSTANCE;
    }

    public static void setDefaultChoice(FinalBossChoice choice) {
        defaultChoice = choice == null ? FinalBossChoice.DAD : choice;
    }

    public static FinalBossChoice getDefaultChoice() {
        return defaultChoice;
    }

    public static void resetForNewRun() {
        runChoice = pendingChoice == null ? defaultChoice : pendingChoice;
        pendingChoice = null;
        resolvedChoice = null;
    }

    public static void setRunChoice(FinalBossChoice choice) {
        runChoice = choice == null ? defaultChoice : choice;
        resolvedChoice = null;
    }

    public static void setPendingChoice(FinalBossChoice choice) {
        pendingChoice = choice == null ? defaultChoice : choice;
        runChoice = pendingChoice;
        resolvedChoice = null;
    }

    public static FinalBossChoice getSelectableChoice() {
        if (pendingChoice != null) {
            return pendingChoice;
        }
        return getRunChoice();
    }

    public static FinalBossChoice getRunChoice() {
        return runChoice == null ? defaultChoice : runChoice;
    }

    public static FinalBossChoice resolveForCurrentRun() {
        if (resolvedChoice != null) {
            return resolvedChoice;
        }
        FinalBossChoice current = getRunChoice();
        if (current == FinalBossChoice.RANDOM) {
            boolean dad = AbstractDungeon.miscRng.randomBoolean();
            resolvedChoice = dad ? FinalBossChoice.DAD : FinalBossChoice.HEART;
        } else {
            resolvedChoice = current;
        }
        return resolvedChoice;
    }

    public static boolean shouldUseGrandMageDad() {
        return resolveForCurrentRun() == FinalBossChoice.DAD;
    }

    @Override
    public Integer onSave() {
        return getRunChoice().ordinal();
    }

    @Override
    public void onLoad(Integer integer) {
        runChoice = FinalBossChoice.fromOrdinal(integer, defaultChoice);
        pendingChoice = null;
        resolvedChoice = null;
    }
}
