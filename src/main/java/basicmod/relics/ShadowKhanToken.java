package basicmod.relics;

import basicmod.BasicMod;
import basicmod.helpers.TalismanInputHelper;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

/**
 * 黑影令牌。
 * 用于切换黑影兵团烧牌方式：1为玩家自选，0为自动随机，2为明塔随机且伊卡白板。
 */
public class ShadowKhanToken extends BaseRelic {
    public static final String NAME = "ShadowKhanToken";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.SPECIAL;
    private static final LandingSound SOUND = LandingSound.CLINK;

    /**
     * 创建黑影令牌，并按配置显示当前挡位。
     */
    public ShadowKhanToken() {
        super(ID, NAME, RARITY, SOUND);
        syncCounter();
    }

    /**
     * 进入战斗前同步挡位显示。
     */
    @Override
    public void atPreBattle() {
        syncCounter();
    }

    /**
     * 处理右键切换挡位。
     */
    @Override
    public void update() {
        super.update();
        if (TalismanInputHelper.isRelicRightClickTriggered(this)) {
            CardCrawlGame.sound.play("UI_CLICK_1");
            BasicMod.shadowKhanExhaustMode = getNextMode(BasicMod.shadowKhanExhaustMode);
            BasicMod.shadowKhanManualExhaustMode = BasicMod.shadowKhanExhaustMode == 1;
            BasicMod.saveConfig();
            syncCounter();
            this.flash();
            BasicMod.logger.info("【黑影令牌】切换黑影兵团烧牌挡位：{}", getModeLogText(BasicMod.shadowKhanExhaustMode));
        }
    }

    /**
     * 判断当前黑影兵团烧牌是否应由玩家自选。
     * 未持有黑影令牌时返回true，避免影响其他来源或异常情况下的旧逻辑。
     *
     * @return true表示自选，false表示不走自选烧牌
     */
    public static boolean isManualExhaustMode() {
        return getCurrentMode() == 1;
    }

    /**
     * 判断明塔是否应该自动随机消耗攻击牌。
     *
     * @return true表示明塔自动随机烧牌
     */
    public static boolean isMingTaRandomExhaustMode() {
        int mode = getCurrentMode();
        return mode == 0 || mode == 2;
    }

    /**
     * 判断伊卡是否应该当作白板打出。
     *
     * @return true表示伊卡不烧牌、不触发追加效果
     */
    public static boolean isYiKaBlankMode() {
        return getCurrentMode() == 2;
    }

    /**
     * 获取当前黑影令牌挡位。
     * 未持有黑影令牌时返回1，避免影响旧逻辑。
     *
     * @return 当前挡位
     */
    private static int getCurrentMode() {
        if (AbstractDungeon.player == null || !AbstractDungeon.player.hasRelic(ID)) {
            return 1;
        }
        return BasicMod.shadowKhanExhaustMode;
    }

    /**
     * 按配置刷新遗物数字。
     */
    private void syncCounter() {
        this.counter = BasicMod.shadowKhanExhaustMode;
    }

    /**
     * 获取右键点击后的下一个挡位。
     *
     * @param currentMode 当前挡位
     * @return 下一个挡位
     */
    private int getNextMode(int currentMode) {
        if (currentMode == 1) {
            return 0;
        }
        if (currentMode == 0) {
            return 2;
        }
        return 1;
    }

    /**
     * 获取日志中显示的挡位说明。
     *
     * @param mode 当前挡位
     * @return 中文日志说明
     */
    private String getModeLogText(int mode) {
        if (mode == 1) {
            return "1-自选";
        }
        if (mode == 0) {
            return "0-自动随机";
        }
        return "2-明塔随机，伊卡白板";
    }

    /**
     * 获取当前描述。
     *
     * @return 本地化描述
     */
    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
