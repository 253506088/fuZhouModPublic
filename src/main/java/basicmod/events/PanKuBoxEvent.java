package basicmod.events;

import basicmod.relics.PanKuBox;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Normality;
import com.megacrit.cardcrawl.cards.curses.Parasite;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static basicmod.BasicMod.makeID;

public class PanKuBoxEvent extends AbstractImageEvent {
    public static final String ID = makeID("PanKuBoxEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    private int screenNum = 0; // 当前屏幕编号
    private static final int MAX_HP_LOSS = 15;

    public PanKuBoxEvent() {
        super(NAME, DESCRIPTIONS[0], "basicmod/images/events/panku_box.png");

        // 选项 0：滴血解谜 (扣血上限)
        if (AbstractDungeon.player.maxHealth > MAX_HP_LOSS) {
            this.imageEventText.setDialogOption(OPTIONS[0], new PanKuBox());
        } else {
            this.imageEventText.setDialogOption(OPTIONS[3], true); // 灰化锁定
        }

        // 选项 1：接纳暗影 (诅咒)
        this.imageEventText.setDialogOption(OPTIONS[1], new PanKuBox());

        // 选项 2：转身离开
        this.imageEventText.setDialogOption(OPTIONS[2]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // 滴血解谜
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.decreaseMaxHealth(MAX_HP_LOSS);
                        obtainRelic();
                        break;
                    case 1: // 接纳暗影
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        // 获得诅咒：凡庸、寄生
                        AbstractCard c1 = CardLibrary.getCard(Normality.ID).makeCopy();
                        AbstractCard c2 = CardLibrary.getCard(Parasite.ID).makeCopy();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c1, (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2)));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c2, (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2)));
                        obtainRelic();
                        break;
                    case 2: // 转身离开
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        break;
                }
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[2]); // 离开
                screenNum = 1;
                break;

            case 1:
                openMap();
                break;
        }
    }

    private void obtainRelic() {
        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2),
                new PanKuBox()
        );
    }
}
