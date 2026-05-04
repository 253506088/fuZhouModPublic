package basicmod.events;
 
import basicmod.relics.CollaborationRelic;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
 
import static basicmod.BasicMod.makeID;
 
public class DragoEvent extends AbstractImageEvent {
    public static final String ID = makeID("DragoEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final java.util.List<String> TALISMAN_IDS = java.util.Arrays.asList(
            makeID("RatTalisman"), makeID("OxTalisman"), makeID("TigerTalisman"),
            makeID("RabbitTalisman"), makeID("DragonTalisman"), makeID("SnakeTalisman"),
            makeID("HorseTalisman"), makeID("SheepTalisman"), makeID("MonkeyTalisman"),
            makeID("RoosterTalisman"), makeID("DogTalisman"), makeID("PigTalisman")
    );
 
    private int screenNum = 0; // 当前屏幕编号
 
    public DragoEvent() {
        super(NAME, DESCRIPTIONS[0], "basicmod/images/events/drago.png");

        // 初始界面
        this.imageEventText.setDialogOption(OPTIONS[0], new CollaborationRelic()); // 合作
        this.imageEventText.setDialogOption(OPTIONS[1]); // 拒绝合作
    }
 
    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                if (buttonPressed == 0) {
                    // 1. 选择合作
                    this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                    
                    if (AbstractDungeon.player.hasRelic(CollaborationRelic.ID)) {
                        // 已持有合作，给予进阶奖励
                        giveSpecialReward();
                    } else {
                        // 未持有合作，给予合作遗物
                        AbstractRelic relic = new CollaborationRelic();
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic);
                    }
                    
                    this.imageEventText.updateDialogOption(0, OPTIONS[4]); // 离开
                    this.imageEventText.clearRemainingOptions();
                    screenNum = 100; // 完结标记
                } else {
                    // 2. 选择拒绝
                    this.imageEventText.updateBodyText(DESCRIPTIONS[3]); // 老爹：你还要想多久？
                    this.imageEventText.updateDialogOption(0, OPTIONS[2]); // 拒绝 (确认死亡)
                    this.imageEventText.updateDialogOption(1, OPTIONS[3]); // 再想想
                    screenNum = 1;
                }
                break;
 
            case 1:
                if (buttonPressed == 0) {
                    // 3. 确认拒绝（死亡）
                    this.imageEventText.updateBodyText(DESCRIPTIONS[2]); // 毁灭提示
                    
                    AbstractDungeon.player.currentHealth = 0;
                    AbstractDungeon.player.isDead = true;
                    // 事件房没有怪物，这里必须传 null，否则会报 NullPointerException
                    AbstractDungeon.deathScreen = new com.megacrit.cardcrawl.screens.DeathScreen(null);
                    // 强制关闭事件对话框并清空，防止渲染冲突
                    this.imageEventText.clearAllDialogs();
                    // 这里不设置 screenNum = 100，因为我们直接用死亡界面覆盖它
                } else {
                    // 4. 再想想（回滚）
                    this.imageEventText.updateBodyText(DESCRIPTIONS[0]);
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[0], new CollaborationRelic());
                    this.imageEventText.setDialogOption(OPTIONS[1]);
                    screenNum = 0;
                }
                break;
 
            case 100:
                openMap();
                break;
        }
    }

    private void giveSpecialReward() {
        java.util.List<String> unownedTalismans = new java.util.ArrayList<>();
        for (String id : TALISMAN_IDS) {
            if (!AbstractDungeon.player.hasRelic(id)) {
                unownedTalismans.add(id);
            }
        }

        AbstractRelic rewardRelic;
        if (!unownedTalismans.isEmpty()) {
            // 随机获取一个未拥有的符咒
            String relicId = unownedTalismans.get(AbstractDungeon.miscRng.random(unownedTalismans.size() - 1));
            rewardRelic = com.megacrit.cardcrawl.helpers.RelicLibrary.getRelic(relicId).makeCopy();
        } else {
            // 已集齐12符咒，随机给一个未持有的遗物 (普通/罕见/稀有)
            AbstractRelic.RelicTier tier = rollTier();
            rewardRelic = AbstractDungeon.returnRandomRelic(tier);
        }

        if (rewardRelic != null) {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, rewardRelic);
        }
    }

    private AbstractRelic.RelicTier rollTier() {
        int roll = AbstractDungeon.miscRng.random(0, 99);
        if (roll < 50) return AbstractRelic.RelicTier.COMMON;
        if (roll < 85) return AbstractRelic.RelicTier.UNCOMMON;
        return AbstractRelic.RelicTier.RARE;
    }
}
