package basicmod.powers;

import basicmod.util.GeneralUtils;
import basicmod.util.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import basicmod.BasicMod;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

/**
 * 能力基类。
 * 提供能力的基础功能，包括本地化、图片加载、双数值显示等。
 */
public abstract class BasePower extends AbstractPower {
    /**
     * 获取能力本地化字符串。
     */
    private static PowerStrings getPowerStrings(String ID)
    {
        return CardCrawlGame.languagePack.getPowerStrings(ID);
    }
    /** 能力来源 */
    protected AbstractCreature source;
    /** 描述文本数组 */
    protected String[] DESCRIPTIONS;

    /** 第二个数值，为0时不显示。不会像amount一样在堆叠时自动累加 */
    public int amount2 = 0;
    /** 第二个数值的红色（减少） */
    protected Color redColor2 = Color.RED.cpy();
    /** 第二个数值的绿色（增加） */
    protected Color greenColor2 = Color.GREEN.cpy();

    /**
     * 基础构造函数。
     */
    public BasePower(String id, PowerType powerType, boolean isTurnBased, AbstractCreature owner, int amount) {
        this(id, powerType, isTurnBased, owner, null, amount);
    }
    public BasePower(String id, PowerType powerType, boolean isTurnBased, AbstractCreature owner, AbstractCreature source, int amount) {
        this(id, powerType, isTurnBased, owner, source, amount, true);
    }
    public BasePower(String id, PowerType powerType, boolean isTurnBased, AbstractCreature owner, AbstractCreature source, int amount, boolean initDescription) {
        this(id, powerType, isTurnBased, owner, source, amount, initDescription, true);
    }
    public BasePower(String id, PowerType powerType, boolean isTurnBased, AbstractCreature owner, AbstractCreature source, int amount, boolean initDescription, boolean loadImage) {
        this.ID = id;
        this.isTurnBased = isTurnBased;

        PowerStrings strings = getPowerStrings(this.ID);
        if (strings != null) {
            this.name = strings.NAME;
            this.DESCRIPTIONS = strings.DESCRIPTIONS;
        } else {
            BasicMod.logger.error("PowerStrings for ID " + this.ID + " is null!");
            this.name = "[缺失文本描述]";
            this.DESCRIPTIONS = new String[]{"[缺失文本描述]", ""};
        }

        this.owner = owner;
        this.source = source;
        this.amount = amount;
        this.type = powerType;

        if (loadImage)
        {
            String unPrefixed = GeneralUtils.removePrefix(id);
            Texture normalTexture = TextureLoader.getPowerTexture(unPrefixed);
            Texture hiDefImage = TextureLoader.getHiDefPowerTexture(unPrefixed);
            if (hiDefImage != null)
            {
                region128 = new TextureAtlas.AtlasRegion(hiDefImage, 0, 0, hiDefImage.getWidth(), hiDefImage.getHeight());
                if (normalTexture != null)
                    region48 = new TextureAtlas.AtlasRegion(normalTexture, 0, 0, normalTexture.getWidth(), normalTexture.getHeight());
            }
            else
            {
                this.img = normalTexture;
                region48 = new TextureAtlas.AtlasRegion(normalTexture, 0, 0, normalTexture.getWidth(), normalTexture.getHeight());
            }
        }

        if (initDescription)
            this.updateDescription();
    }

    /**
     * 渲染能力数值，支持显示第二个数值。
     */
    public void renderAmount(SpriteBatch sb, float x, float y, Color c) {
        super.renderAmount(sb, x, y, c);

        if (this.amount2 != 0) {
            if (!this.isTurnBased) {
                float alpha = c.a;
                c = this.amount2 > 0 ? this.greenColor2 : this.redColor2;
                c.a = alpha;
            }

            FontHelper.renderFontRightTopAligned(sb, FontHelper.powerAmountFont, Integer.toString(this.amount2), x, y + 15.0F * Settings.scale, this.fontScale, c);
        }
    }
}