package basicmod.relics;

import basicmod.BasicMod;
import basemod.abstracts.CustomRelic;
import basemod.helpers.RelicType;
import basicmod.util.GeneralUtils;
import basicmod.util.TextureLoader;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.RelicStrings;

import static basicmod.BasicMod.relicPath;

/**
 * 遗物基类。
 * 提供遗物的基础功能，包括图片加载、本地化、卡池设置等。
 */
public abstract class BaseRelic extends CustomRelic {
    /** 遗物所属卡池 */
    public AbstractCard.CardColor pool = null;
    /** 遗物类型（共享/职业专属） */
    public basemod.helpers.RelicType relicType = basemod.helpers.RelicType.SHARED;
    /** 遗物图片名称 */
    protected String imageName;

    /**
     * 安全检查：当前是否在可以交互的战斗中
     * 解决在 RelicViewScreen (图鉴) 中 update 导致的 NullPointerException
     */
    public boolean canInteractInCombat() {
        return CardCrawlGame.isInARun() && 
               com.megacrit.cardcrawl.dungeons.AbstractDungeon.id != null && 
               com.megacrit.cardcrawl.dungeons.AbstractDungeon.player != null &&
               com.megacrit.cardcrawl.dungeons.AbstractDungeon.currMapNode != null && 
               com.megacrit.cardcrawl.dungeons.AbstractDungeon.getCurrRoom() != null && 
               com.megacrit.cardcrawl.dungeons.AbstractDungeon.getCurrRoom().phase == com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.COMBAT;
    }

    /**
     * 职业专属遗物构造函数。
     */
    public BaseRelic(String id, String imageName, AbstractCard.CardColor pool, RelicTier tier, LandingSound sfx) {
        this(id, imageName, tier, sfx);

        setPool(pool);
    }

    /**
     * 使用ID作为图片名称的构造函数。
     */
    public BaseRelic(String id, RelicTier tier, LandingSound sfx) {
        this(id, GeneralUtils.removePrefix(id), tier, sfx);
    }

    /**
     * 完整构造函数。
     * 要使用原版遗物图片，传入原版遗物使用的图片名称，如 "calendar.png"。
     */
    public BaseRelic(String id, String imageName, RelicTier tier, LandingSound sfx) {
        super(testStrings(id), notPng(imageName) ? "" : imageName, tier, sfx);

        this.imageName = imageName;
        if (notPng(imageName)) {
            loadTexture();
        }
    }

    /**
     * 加载遗物纹理图片。
     */
    protected void loadTexture() {
        this.img = TextureLoader.getTextureNull(relicPath(imageName + ".png"), true);
        if (img != null) {
            outlineImg = TextureLoader.getTextureNull(relicPath(imageName + "Outline.png"), true);
            if (outlineImg == null)
                outlineImg = img;
        }
        else {
            ImageMaster.loadRelicImg("Derp Rock", "derpRock.png");
            this.img = ImageMaster.getRelicImg("Derp Rock");
            this.outlineImg = ImageMaster.getRelicOutlineImg("Derp Rock");
        }
    }

    @Override
    public void loadLargeImg() {
        if (notPng(imageName)) {
            if (largeImg == null) {
                this.largeImg = ImageMaster.loadImage(relicPath("large/" + imageName + ".png"));
            }
        }
        else {
            super.loadLargeImg();
        }
    }

    private void setPool(AbstractCard.CardColor pool) {
        switch (pool) { //Basegame pools are handled differently
            case RED:
                relicType = RelicType.RED;
                break;
            case GREEN:
                relicType = RelicType.GREEN;
                break;
            case BLUE:
                relicType = RelicType.BLUE;
                break;
            case PURPLE:
                relicType = RelicType.PURPLE;
                break;
            default:
                this.pool = pool;
                break;
        }
    }

    /**
     * Checks whether relic has localization set up correctly and gives a more accurate error message if it does not
     * @param ID the relic's ID
     * @return the relic's ID, to allow use in super constructor invocation
     */
    private static String testStrings(String ID) {
        RelicStrings text = CardCrawlGame.languagePack.getRelicStrings(ID);
        if (text == null) {
            BasicMod.logger.error("RelicStrings for ID " + ID + " is null!");
            return ID; // 保证 super(ID, ...) 能继续运行
        }
        return ID;
    }

    private static boolean notPng(String name) {
        return !name.endsWith(".png");
    }

    @Override
    public void onEquip() {
        super.onEquip();
        // 装备时立刻通过清单判断：若为十二生肖符咒，则将其在原版遗物池中彻底摘除。
        // （应对事件或者其他强制赋予手段时的双重保险防重复获取）
        if (basicmod.helpers.TalismanHelper.ALL_TALISMAN_IDS.contains(this.relicId)) {
            basicmod.helpers.TalismanHelper.removeTalismanFromPools(this.relicId);
        }
    }

    /**
     * 刷新遗物描述和提示。
     */
    public void refreshDescription() {
        this.description = this.getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new com.megacrit.cardcrawl.helpers.PowerTip(this.name, this.description));
        this.initializeTips();
    }
}