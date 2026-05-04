package basicmod.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
public class ShengZhuEnergyOrb extends com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbRed {
    private Texture img;
    private float angle = 0.0F;

    public ShengZhuEnergyOrb() {
        super();
        // 直接读取您准备好的 256x256 图层
        this.img = TextureLoader.getTexture("basicmod/images/character/shengzhu/orb/layer1.png");
    }

    @Override
    public void updateOrb(int energyCount) {
        // 让它慢慢转动
        this.angle += Gdx.graphics.getDeltaTime() * 45.0F; 
    }

    @Override
    public void renderOrb(SpriteBatch sb, boolean enabled, float x, float y) {
        if (enabled) {
            sb.setColor(Color.WHITE);
        } else {
            sb.setColor(Color.LIGHT_GRAY);
        }
        
        // 渲染逻辑：以 128x128 绘制，居中
        sb.draw(img, x - 64.0F, y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, 
                Settings.scale, Settings.scale, angle, 0, 0, 256, 256, false, false);
    }
}
