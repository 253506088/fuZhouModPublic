package basicmod.effects;

import basicmod.helpers.HistoryBookMaskHelper;
import basicmod.helpers.CardRewardHelper;
import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;

/**
 * 岁月史书十面具慢速获得效果。
 * 负责按固定间隔依次展示获得面具，并在全部结束后再触发无尽黑暗检查。
 */
public class HistoryBookSequentialMaskObtainEffect extends AbstractGameEffect {
    private static final float CARD_INTERVAL = 0.55F;
    private static final float FINAL_WAIT = 1.8F;

    private final ArrayList<String> maskIds;
    private int index = 0;
    private float timer = 0.0F;
    private boolean waitingFinalCards = false;

    /**
     * 慢速依次获得十张基础面具，避免无尽黑暗彩蛋抢在动画中途触发。
     */
    public HistoryBookSequentialMaskObtainEffect(ArrayList<String> maskIds) {
        this.maskIds = maskIds;
        this.duration = 999.0F;
        HistoryBookMaskHelper.delayingEndlessDarknessCheck = true;
    }

    /**
     * 每隔一小段时间安全入组并展示一张面具，所有展示结束后再检查无尽黑暗。
     */
    @Override
    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        this.timer -= delta;

        if (waitingFinalCards) {
            if (this.timer <= 0.0F) {
                HistoryBookMaskHelper.finishDelayedEndlessDarknessCheck();
                this.isDone = true;
            }
            return;
        }

        if (this.index >= this.maskIds.size()) {
            this.waitingFinalCards = true;
            this.timer = FINAL_WAIT;
            return;
        }

        if (this.timer <= 0.0F) {
            AbstractCard card = HistoryBookMaskHelper.makeMaskCopy(this.maskIds.get(this.index));
            if (card != null) {
                if (CardRewardHelper.grantCardToMasterDeck(card)) {
                    showGrantedMaskCard(card);
                }
            }
            this.index++;
            this.timer = CARD_INTERVAL;
        }
    }

    /**
     * 入组后只播放展示特效，不再依赖展示特效完成后才发奖。
     */
    private void showGrantedMaskCard(AbstractCard card) {
        CardCrawlGame.sound.play("CARD_OBTAIN");
        CardRewardHelper.showCardBriefly(card);
    }

    /**
     * 本效果只负责调度获得卡牌，不额外绘制内容。
     */
    @Override
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch sb) {
    }

    /**
     * 没有额外资源需要释放。
     */
    @Override
    public void dispose() {
    }
}
