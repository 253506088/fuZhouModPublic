package basicmod.events;

import basicmod.BasicMod;
import basicmod.effects.HistoryBookSequentialMaskObtainEffect;
import basicmod.effects.HistoryBookTalismanObtainEffect;
import basicmod.helpers.HistoryBookMaskHelper;
import basicmod.helpers.HistoryBookTalismanHelper;
import basicmod.relics.HistoryBookFragment;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;

/**
 * 岁月史书事件。
 * 负责展示三条奖励路线：重写十二符咒、重写十面具、撕下残卷。
 * 具体奖励逻辑拆到 helper 和 effect 中，避免事件按钮回调过重。
 */
public class HistoryBookEvent extends AbstractImageEvent {
    public static final String ID = BasicMod.makeID("HistoryBookEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final int TALISMAN_MAX_HP = 12;
    private static final int MASK_MAX_HP = 5;

    private int screenNum = 0;

    /**
     * 创建岁月史书事件，并展示三个改写现实的入口。
     */
    public HistoryBookEvent() {
        super(NAME, DESCRIPTIONS[0], "basicmod/images/events/history_book_event.png");
        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.imageEventText.setDialogOption(OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[2], new HistoryBookFragment());
    }

    /**
     * 根据玩家选择执行三种奖励路线，执行后只保留离开按钮。
     */
    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                if (buttonPressed == 0) {
                    gainAllTalismans();
                } else if (buttonPressed == 1) {
                    gainAllMasks();
                } else if (buttonPressed == 2) {
                    gainHistoryBookFragment();
                }
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[3]);
                this.screenNum = 1;
                break;
            case 1:
                this.openMap();
                break;
        }
    }

    /**
     * 执行十二符咒路线：先剥夺已有符咒补生命，再通过动画重新集齐。
     */
    private void gainAllTalismans() {
        int removed = HistoryBookTalismanHelper.removeOwnedTalismansAndGainMaxHp(TALISMAN_MAX_HP);
        AbstractDungeon.topLevelEffectsQueue.add(new HistoryBookTalismanObtainEffect(HistoryBookTalismanHelper.makeAllTalismanCopies()));
        this.imageEventText.updateBodyText(DESCRIPTIONS[1] + removed + DESCRIPTIONS[2]);
    }

    /**
     * 执行十面具路线：先按已有种类补生命并删除，再慢速加入十张基础面具。
     */
    private void gainAllMasks() {
        int removedTypes = HistoryBookMaskHelper.removeOwnedMaskTypesAndGainMaxHp(MASK_MAX_HP);
        AbstractDungeon.topLevelEffectsQueue.add(new HistoryBookSequentialMaskObtainEffect(HistoryBookMaskHelper.getAllMaskIdsCopy()));
        this.imageEventText.updateBodyText(DESCRIPTIONS[3] + removedTypes + DESCRIPTIONS[4]);
    }

    /**
     * 执行撕下一页路线：获得岁月史书残卷遗物。
     */
    private void gainHistoryBookFragment() {
        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new HistoryBookFragment());
        this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
    }
}
