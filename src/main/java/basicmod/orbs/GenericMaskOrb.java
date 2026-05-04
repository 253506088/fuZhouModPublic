package basicmod.orbs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import basicmod.BasicMod;

public class GenericMaskOrb extends AbstractOrb {
    public static final String ORB_ID = BasicMod.makeID("GenericMaskOrb");
    public String maskPowerId;

    public GenericMaskOrb(String maskPowerId, String name, int amount) {
        this.maskPowerId = maskPowerId;
        this.ID = maskPowerId; // 用Power的ID可以防止重复添加，或者跟进逻辑
        this.name = name;
        this.basePassiveAmount = amount; // 这里使用 passive amount 作为记录层数的变量
        this.passiveAmount = amount;

        // 根据 maskPowerId 提取对应的核心名字并读取正确的图片
        // 例如："fuZhouMod:YiKaPower" -> "YiKa"
        String shortName = maskPowerId.replace(BasicMod.modID + ":", "").replace("Power", "");
        String imgPath = BasicMod.imagePath("orbs/masks/" + shortName + ".png");
        this.img = basicmod.util.TextureLoader.getTexture(imgPath);
        
        if (this.img == null) {
            this.img = ImageMaster.ORB_DARK;
        }

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.applyFocus(); 
        this.description = this.name + "：当前拥有 " + this.passiveAmount + " 层面具功效。";
    }

    @Override
    public void onEvoke() {
    }

    @Override
    public void playChannelSFX() {
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(new Color(1.0F, 1.0F, 1.0F, this.c.a));
        // 使用该图片真实的宽高进行绘制，锁定最终绘制尺寸为96x96
        if (this.img != null) {
            sb.draw(this.img, this.cX - 48.0F, this.cY - 48.0F + this.bobEffect.y, 48.0F, 48.0F, 96.0F, 96.0F, this.scale, this.scale, this.angle, 0, 0, this.img.getWidth(), this.img.getHeight(), false, false);
        }
        this.renderText(sb);
        this.hb.render(sb);
    }

    @Override
    protected void renderText(SpriteBatch sb) {
        // 重写原本的伤害数字，直接居右下角输出层数
        if (this.passiveAmount > 0) {
            FontHelper.renderFontCentered(
                    sb,
                    FontHelper.cardEnergyFont_L,
                    Integer.toString(this.passiveAmount),
                    this.cX + NUM_X_OFFSET,
                    this.cY + this.bobEffect.y / 2.0F + NUM_Y_OFFSET - 4.0F * Settings.scale,
                    new Color(0.2F, 1.0F, 0.2F, this.c.a),
                    this.fontScale
            );
        }
    }

    @Override
    public void setSlot(int slotNum, int maxOrbs) {
        super.setSlot(slotNum, maxOrbs);
        // 在原版算好的目标 Y 坐标基础上，强行增加高度
        this.tY += 120.0F * com.megacrit.cardcrawl.core.Settings.scale;
    }

    @Override
    public AbstractOrb makeCopy() {
        return new GenericMaskOrb(this.maskPowerId, this.name, this.passiveAmount);
    }
}
