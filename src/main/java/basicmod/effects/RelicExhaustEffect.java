package basicmod.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class RelicExhaustEffect extends AbstractGameEffect {
    private final AbstractRelic relic;
    private float sX, sY, tX, tY;
    private float x, y;
    private boolean soundPlayed = false;

    public RelicExhaustEffect(AbstractRelic r) {
        this.relic = r;
        this.duration = 1.6F; // 总时长增加，让玩家有反应时间
        this.startingDuration = 1.6F;
        
        // 起始点：遗物当前位置
        this.sX = r.hb.cX;
        this.sY = r.hb.cY;
        
        // 目标点：屏幕中央
        this.tX = (float)Settings.WIDTH / 2.0F;
        this.tY = (float)Settings.HEIGHT / 2.0F;
        
        this.x = this.sX;
        this.y = this.sY;
        
        this.scale = Settings.scale;
        this.color = Color.WHITE.cpy();
        this.rotation = r.counter >= 0 ? 0.0F : MathUtils.random(-10.0F, 10.0F);
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        
        // 阶段 1 (1.6s -> 1.1s): 停留、颤动、稍微放大，高亮提醒
        if (this.duration > 1.1F) {
            float t = (1.6F - this.duration) / 0.5F; // 0.0 -> 1.0
            this.scale = Interpolation.elasticOut.apply(Settings.scale, Settings.scale * 1.5F, t);
            this.x = this.sX + MathUtils.random(-3.0F * Settings.scale, 3.0F * Settings.scale);
            this.y = this.sY + MathUtils.random(-3.0F * Settings.scale, 3.0F * Settings.scale);
            
            if (!soundPlayed) {
                CardCrawlGame.sound.play("RELIC_DROP_CLINK");
                soundPlayed = true;
            }
        } 
        // 阶段 2 (1.1s -> 0.5s): 平滑移动到屏幕中央
        else if (this.duration > 0.5F) {
            float t = (1.1F - this.duration) / 0.6F; // 0.0 -> 1.0
            this.x = Interpolation.exp5In.apply(this.sX, this.tX, t);
            this.y = Interpolation.exp5In.apply(this.sY, this.tY, t);
        } 
        // 阶段 3 (0.5s -> 0.0s): 在中央放大消失
        else {
            if (this.duration + Gdx.graphics.getDeltaTime() > 0.5F) {
                CardCrawlGame.sound.play("CARD_EXHAUST");
            }
            this.x = this.tX;
            this.y = this.tY;
            // 透明度递减
            this.color.a = Interpolation.pow2In.apply(0.0F, 1.0F, this.duration * 2.0F);
            // 缩放变大消散
            this.scale = Interpolation.pow3In.apply(Settings.scale * 3.0F, Settings.scale * 1.5F, this.duration * 2.0F);
            // 旋转加速
            this.rotation += Gdx.graphics.getDeltaTime() * 200.0F;
        }

        if (this.duration < 0.0F) {
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        // 绘制遗物贴图
        sb.draw(relic.img, x - 64.0F, y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, scale, scale, rotation, 0, 0, 128, 128, false, false);
        
        // 阶段 1 的发光效果 (简单模拟)
        if (this.duration > 1.1F) {
            sb.setBlendFunction(770, 1);
            Color highlightColor = Color.GOLD.cpy();
            highlightColor.a = (1.6F - this.duration) * 2.0F;
            sb.setColor(highlightColor);
            sb.draw(relic.img, x - 64.0F, y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, scale * 1.2F, scale * 1.2F, rotation, 0, 0, 128, 128, false, false);
            sb.setBlendFunction(770, 771);
        }
    }

    @Override
    public void dispose() {
    }
}
