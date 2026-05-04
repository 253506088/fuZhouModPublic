package basicmod.ui;

import basicmod.BasicMod;
import basicmod.relics.MonkeyTalisman;
import basicmod.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;

public class MonkeyTalismanCampfireOption extends AbstractCampfireOption {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:MonkeyTalismanCampfireUI");
    private static final Texture TRANSFORM_ICON = TextureLoader.getTexture(BasicMod.imagePath("ui/campfire/monkey_72_transform.png"));

    /**
     * 创建猴符咒在休息处显示的【七十二变】选项。
     */
    public MonkeyTalismanCampfireOption() {
        this.label = uiStrings.TEXT[0];
        this.description = uiStrings.TEXT[1];
        this.img = TRANSFORM_ICON != null ? TRANSFORM_ICON : ImageMaster.CAMPFIRE_SMITH_BUTTON;
        this.usable = AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(MonkeyTalisman.ID);
    }

    /**
     * 点击【七十二变】后，启动猴符咒的营火变牌流程。
     */
    @Override
    public void useOption() {
        if (!usable) {
            return;
        }

        MonkeyTalisman monkeyTalisman = (MonkeyTalisman) AbstractDungeon.player.getRelic(MonkeyTalisman.ID);
        if (monkeyTalisman == null) {
            return;
        }

        AbstractDungeon.effectList.add(new MonkeyTalismanCampfireTransformEffect(monkeyTalisman));
    }
}
