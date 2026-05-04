package basicmod.effects;

import basicmod.helpers.TalismanAwakeningHelper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;

/**
 * 岁月史书十二符咒获得效果。
 * 先把十二符咒围绕屏幕中央旋转展示，再按顺序发放到遗物栏。
 */
public class HistoryBookTalismanObtainEffect extends AbstractGameEffect {
    private static final float SPIN_TIME = 1.8F;
    private static final float OBTAIN_INTERVAL = 0.25F;

    private final ArrayList<AbstractRelic> relics;
    private int obtainIndex = 0;
    private float timer = SPIN_TIME;
    private boolean spinning = true;
    private boolean talismanAwakeningChecked = false;
    private float rotation = 0.0F;

    /**
     * 十二符咒先在屏幕中央旋转三圈，再依次飞入遗物栏。
     */
    public HistoryBookTalismanObtainEffect(ArrayList<AbstractRelic> relics) {
        this.relics = relics;
        this.duration = 999.0F;
        this.color = Color.WHITE.cpy();
        this.scale = Settings.scale;
    }

    /**
     * 先更新旋转阶段，旋转结束后按固定间隔发放符咒。
     */
    @Override
    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        this.timer -= delta;

        if (this.spinning) {
            float progress = MathUtils.clamp(1.0F - this.timer / SPIN_TIME, 0.0F, 1.0F);
            this.rotation = progress * 1080.0F;
            if (this.timer <= 0.0F) {
                this.spinning = false;
                this.timer = 0.0F;
            }
            return;
        }

        if (this.obtainIndex >= this.relics.size()) {
            // 十二符咒批量发放结束后，主动补一次觉醒检测，避免某些时序下延迟到战后才触发
            if (!this.talismanAwakeningChecked) {
                this.talismanAwakeningChecked = true;
                TalismanAwakeningHelper.checkAndTriggerAwakening();
            }
            this.isDone = true;
            return;
        }

        if (this.timer <= 0.0F) {
            AbstractRelic relic = this.relics.get(this.obtainIndex);
            CardCrawlGame.sound.play("RELIC_DROP_CLINK");
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                    Settings.WIDTH / 2.0F,
                    Settings.HEIGHT / 2.0F,
                    relic
            );
            this.obtainIndex++;
            this.timer = OBTAIN_INTERVAL;
        }
    }

    /**
     * 旋转阶段把尚未入栏的符咒画在屏幕中央附近，形成“岁月史书改写现实”的展示感。
     */
    @Override
    public void render(SpriteBatch sb) {
        if (!this.spinning) {
            return;
        }

        sb.setColor(this.color);
        float centerX = Settings.WIDTH / 2.0F;
        float centerY = Settings.HEIGHT / 2.0F;
        float radius = 180.0F * Settings.scale;
        int count = Math.max(1, this.relics.size());

        for (int i = 0; i < this.relics.size(); i++) {
            AbstractRelic relic = this.relics.get(i);
            float angle = this.rotation + 360.0F * i / count;
            float x = centerX + MathUtils.cosDeg(angle) * radius;
            float y = centerY + MathUtils.sinDeg(angle) * radius;
            sb.draw(relic.img, x - 64.0F, y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F,
                    Settings.scale, Settings.scale, angle, 0, 0, 128, 128, false, false);
        }
    }

    /**
     * 没有额外资源需要释放。
     */
    @Override
    public void dispose() {
    }
}
