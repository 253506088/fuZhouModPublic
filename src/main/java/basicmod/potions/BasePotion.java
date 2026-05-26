package basicmod.potions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 药水基类。
 * 提供药水的基础功能，包括图片访问、本地化、数据初始化等。
 */
public abstract class BasePotion extends AbstractPotion {
    /** 药水图片字段（通过反射访问） */
    private static final Field containerImg, outlineImg, liquidImg, hybridImg, spotsImg;
    static {
        try {
            containerImg = AbstractPotion.class.getDeclaredField("containerImg");
            outlineImg = AbstractPotion.class.getDeclaredField("outlineImg");
            liquidImg = AbstractPotion.class.getDeclaredField("liquidImg");
            hybridImg = AbstractPotion.class.getDeclaredField("hybridImg");
            spotsImg = AbstractPotion.class.getDeclaredField("spotsImg");

            containerImg.setAccessible(true);
            outlineImg.setAccessible(true);
            liquidImg.setAccessible(true);
            hybridImg.setAccessible(true);
            spotsImg.setAccessible(true);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to access potion image fields.", e);
        }
    }

    /** 药水本地化字符串 */
    public PotionStrings potionStrings;
    /** 描述文本数组 */
    public String[] DESCRIPTIONS;
    /** 基础效能值 */
    public int basePotency;
    /** 限定职业（null表示全职业可用） */
    public AbstractPlayer.PlayerClass playerClass = null;

    /**
     * 使用自定义颜色构造药水。
     */
    public BasePotion(String id, int potency, PotionRarity rarity, PotionSize shape, Color liquidColor, Color hybridColor, Color spotsColor) {
        super("", id, rarity, shape, PotionEffect.NONE, liquidColor, hybridColor, spotsColor);
        basePotency = potency;
        checkColors();
        initializeData();
    }

    /**
     * 使用预设颜色构造药水。
     */
    public BasePotion(String id, int potency, PotionRarity rarity, PotionSize size, PotionColor color) {
        super("", id, rarity, size, color);
        basePotency = potency;
        checkColors();
        initializeData();
    }

    /**
     * 检查颜色配置是否正确。
     */
    protected void checkColors() {
        if (hybridColor != null && getHybridImg() == null) {
            throw new RuntimeException("Potion " + ID + " has hybridColor but no hybridImg; if this is intentional, override checkColors. Otherwise, set hybridColor to null or provide a Texture with setHybridImg.");
        }
        if (spotsColor != null && getSpotsImg() == null) {
            throw new RuntimeException("Potion " + ID + " has spotsColor but no spotsImg; if this is intentional, override checkColors. Otherwise, set spotsColor to null or provide a Texture with setSpotsImg.");
        }
    }

    /**
     * 初始化药水数据，包括名称、描述、提示等。
     */
    @Override
    public void initializeData() {
        this.potency = this.getPotency();

        potionStrings = CardCrawlGame.languagePack.getPotionString(ID);
        DESCRIPTIONS = potionStrings.DESCRIPTIONS;
        this.name = potionStrings.NAME;
        this.description = getDescription();

        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        addAdditionalTips();
    }

    @Override
    public int getPotency(int ascension) {
        return basePotency;
    }

    /**
     * 获取药水描述文本。
     *
     * @return 描述文本
     */
    public abstract String getDescription();
    /**
     * 添加额外的提示信息。
     */
    public void addAdditionalTips() {

    }

    public final Texture getOutlineImg() {
        try {
            return (Texture) outlineImg.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public final Texture getContainerImg() {
        try {
            return (Texture) containerImg.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public final  Texture getLiquidImg() {
        try {
            return (Texture) liquidImg.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public final Texture getHybridImg() {
        try {
            return (Texture) hybridImg.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public final Texture getSpotsImg() {
        try {
            return (Texture) spotsImg.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public final void setOutlineImg(Texture t) {
        try {
            outlineImg.set(this, t);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public final void setContainerImg(Texture t) {
        try {
            containerImg.set(this, t);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public final void setLiquidImg(Texture t) {
        try {
            liquidImg.set(this, t);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public final void setHybridImg(Texture t) {
        try {
            hybridImg.set(this, t);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public final void setSpotsImg(Texture t) {
        try {
            spotsImg.set(this, t);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AbstractPotion makeCopy() {
        try {
            return this.getClass().newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Failed to auto-generate makeCopy for potion: " + this.ID);
        }
    }
}
