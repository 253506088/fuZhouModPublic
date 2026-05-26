package basicmod.relics;

import basicmod.BasicMod;
import basicmod.helpers.TalismanInputHelper;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

/**
 * 黑影令牌。
 * 用于切换黑影兵团烧牌方式：1为玩家自选，0为自动随机。
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
            BasicMod.shadowKhanManualExhaustMode = !BasicMod.shadowKhanManualExhaustMode;
            BasicMod.saveConfig();
            syncCounter();
            this.flash();
            BasicMod.logger.info("【黑影令牌】切换黑影兵团烧牌挡位：{}", BasicMod.shadowKhanManualExhaustMode ? "1-自选" : "0-自动随机");
        }
    }

    /**
     * 判断当前黑影兵团烧牌是否应由玩家自选。
     * 未持有黑影令牌时返回true，避免影响其他来源或异常情况下的旧逻辑。
     *
     * @return true表示自选，false表示自动随机
     */
    public static boolean isManualExhaustMode() {
        if (AbstractDungeon.player == null || !AbstractDungeon.player.hasRelic(ID)) {
            return true;
        }
        return BasicMod.shadowKhanManualExhaustMode;
    }

    /**
     * 按配置刷新遗物数字。
     */
    private void syncCounter() {
        this.counter = BasicMod.shadowKhanManualExhaustMode ? 1 : 0;
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
