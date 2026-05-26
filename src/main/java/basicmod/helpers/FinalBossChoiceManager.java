package basicmod.helpers;

import basemod.abstracts.CustomSavable;
import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.enums.FinalBossChoice;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
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

    /**
     * 判断当前角色是否支持在选人界面切换第四层最终Boss。
     *
     * @param playerClass 当前角色类型
     * @return 圣主和原版四角色返回true，其他Mod角色暂不支持
     */
    public static boolean canChooseFinalBoss(AbstractPlayer.PlayerClass playerClass) {
        return playerClass == CharacterEnums.SHENGZHU || isBaseGameClass(playerClass);
    }

    /**
     * 根据角色获取本局默认的第四层最终Boss。
     *
     * @param playerClass 当前角色类型
     * @return 圣主默认老爹，原版四角色默认心脏
     */
    public static FinalBossChoice getDefaultChoiceForCharacter(AbstractPlayer.PlayerClass playerClass) {
        if (playerClass == CharacterEnums.SHENGZHU) {
            return FinalBossChoice.DAD;
        }
        if (isBaseGameClass(playerClass)) {
            return FinalBossChoice.HEART;
        }
        return defaultChoice;
    }

    public static void resetForNewRun() {
        runChoice = pendingChoice == null ? getDefaultChoiceForCharacter(getCurrentPlayerClass()) : pendingChoice;
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

    /**
     * 清理选人界面的临时Boss选择。
     */
    public static void clearPendingChoice() {
        pendingChoice = null;
        resolvedChoice = null;
    }

    public static FinalBossChoice getSelectableChoice() {
        if (pendingChoice != null) {
            return pendingChoice;
        }
        return getRunChoice();
    }

    /**
     * 获取选人界面当前应该显示的第四层最终Boss选项。
     *
     * @param playerClass 当前选中的角色类型
     * @return 未手动切换时显示该角色自己的默认值
     */
    public static FinalBossChoice getSelectableChoice(AbstractPlayer.PlayerClass playerClass) {
        if (pendingChoice != null) {
            return pendingChoice;
        }
        return getDefaultChoiceForCharacter(playerClass);
    }

    public static FinalBossChoice getRunChoice() {
        return runChoice == null ? getDefaultChoiceForCharacter(getCurrentPlayerClass()) : runChoice;
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
        if (!canChooseFinalBoss(getCurrentPlayerClass())) {
            return false;
        }
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

    /**
     * 判断是否为游戏原版四角色。
     *
     * @param playerClass 当前角色类型
     * @return 战士、猎人、机器人、观者返回true
     */
    private static boolean isBaseGameClass(AbstractPlayer.PlayerClass playerClass) {
        return playerClass == AbstractPlayer.PlayerClass.IRONCLAD
                || playerClass == AbstractPlayer.PlayerClass.THE_SILENT
                || playerClass == AbstractPlayer.PlayerClass.DEFECT
                || playerClass == AbstractPlayer.PlayerClass.WATCHER;
    }

    /**
     * 获取当前正在运行的角色类型。
     *
     * @return 当前玩家角色类型，拿不到时返回null
     */
    private static AbstractPlayer.PlayerClass getCurrentPlayerClass() {
        if (AbstractDungeon.player == null) {
            return null;
        }
        return AbstractDungeon.player.chosenClass;
    }
}
