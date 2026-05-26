package basicmod.events;

import basicmod.BasicMod;
import basicmod.cards.colorless.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

/**
 * 艾克佐迪亚事件
 * 在埃及考古中遇到了被封印的石板，以老爹的口吻描述。
 * 选项1：获得艾克佐迪亚5件套
 * 选项2：离开，无事发生
 * 不限制角色，谁都能遇到。
 */
public class ExodiaEvent extends AbstractImageEvent {
    public static final String ID = BasicMod.makeID("ExodiaEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    private int screenNum = 0;

    public ExodiaEvent() {
        super(NAME, DESCRIPTIONS[0], BasicMod.imagePath("events/exodia_event.png"));

        // 选项1：获得艾克佐迪亚的力量
        this.imageEventText.setDialogOption(OPTIONS[0]);
        // 选项2：离开
        this.imageEventText.setDialogOption(OPTIONS[1]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        // 获得艾克佐迪亚5件套
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);

                        float xOffset = (float) Settings.WIDTH / 2.0F;
                        float yOffset = (float) Settings.HEIGHT / 2.0F;

                        // 添加5张卡到牌组
                        AbstractCard exodia = new ExodiaSealed();
                        AbstractCard rightArm = new ExodiaRightArm();
                        AbstractCard leftArm = new ExodiaLeftArm();
                        AbstractCard rightLeg = new ExodiaRightLeg();
                        AbstractCard leftLeg = new ExodiaLeftLeg();

                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(exodia, xOffset - 300.0F * Settings.scale, yOffset));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(rightArm, xOffset - 150.0F * Settings.scale, yOffset));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(leftArm, xOffset, yOffset));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(rightLeg, xOffset + 150.0F * Settings.scale, yOffset));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(leftLeg, xOffset + 300.0F * Settings.scale, yOffset));

                        BasicMod.logger.info("艾克佐迪亚事件：玩家选择获得5件套");
                        break;
                    case 1:
                        // 离开
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        BasicMod.logger.info("艾克佐迪亚事件：玩家选择离开");
                        break;
                }
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[2]);
                this.screenNum = 1;
                break;
            case 1:
                this.openMap();
                break;
        }
    }
}
