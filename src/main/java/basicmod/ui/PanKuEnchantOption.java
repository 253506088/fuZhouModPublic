package basicmod.ui;

import basicmod.BasicMod;
import basicmod.relics.PanKuBox;
import basicmod.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;

/**
 * 潘库宝盒营火附魔选项。
 * 在营火界面显示附魔按钮，点击后进入附魔流程。
 */
public class PanKuEnchantOption extends AbstractCampfireOption {
    /** UI字符串 */
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:PanKuUI");
    /** 附魔图标 */
    private static final Texture ENCHANT_ICON = TextureLoader.getTexture(BasicMod.imagePath("ui/campfire/enchant.png"));

    /**
     * 构造函数，初始化附魔选项。
     */
    public PanKuEnchantOption() {
        this.label = uiStrings.TEXT[0];
        this.description = uiStrings.TEXT[1];
        this.img = ENCHANT_ICON != null ? ENCHANT_ICON : ImageMaster.CAMPFIRE_SMITH_BUTTON;

        PanKuBox panKuBox = (PanKuBox) AbstractDungeon.player.getRelic(PanKuBox.ID);
        this.usable = panKuBox != null && panKuBox.hasStoredQi();
        if (!this.usable) {
            this.description = uiStrings.TEXT[2];
        }
    }

    @Override
    public void useOption() {
        if (!usable) {
            return;
        }

        PanKuBox panKuBox = (PanKuBox) AbstractDungeon.player.getRelic(PanKuBox.ID);
        if (panKuBox == null || !panKuBox.hasStoredQi()) {
            return;
        }

        AbstractDungeon.effectList.add(new PanKuCampfireEnchantEffect(panKuBox));
    }
}
