package basicmod.relics;

import basicmod.enums.CustomTags;
import basicmod.ui.MonkeyTalismanCampfireOption;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;

import static basicmod.BasicMod.makeID;

/**
 * 猴符咒：七十二变 (正式版 v3.1)
 * 修改后的效果：在休息处通过独立【七十二变】选项，从卡组里定向变幻一张卡片。
 * 流程：选一张现有的牌 -> 选一张本职业的所有牌 -> 永久替换。
 * 
 * 备注：
 * 1. 已移除战斗开始时的触发逻辑。
 * 2. 已解除与兔符咒的加速联动。
 * 3. 触发时机：在营火点击“休息”后。
 * 4. 体验优化：全流程使用“变幻”字眼，不提“删除”。
 * 5. 营地冲突处理 [v3.1新增]：
 *    - 捕梦网 (Dream Catcher): [重点冲突] 休息后抓牌会占用 RewardScreen/GridSelectScreen。
 *    - 皇家枕头/永恒羽毛: [兼容] 仅回血。
 *    - 歌唱碗: [兼容] 依附并兼容捕梦网。
 *    - 解决方案：逻辑调整为异步等待 (update)，只有当屏幕空闲 (!isScreenUp) 时才弹出申猴菜单，实现顺序执行。
 *
 * 6. 第三版，休息处有专门的【七十二变】选项，休息不再能变换卡片，上述内容都已是经是过去式
 */
public class MonkeyTalisman extends BaseRelic {
    public static final String NAME = "MonkeyTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    private enum State {
        IDLE, SELECTING_TARGET, SELECTING_REPLACEMENT
    }

    private State state = State.IDLE;
    private AbstractCard targetCard = null;
    private boolean usedInCombat = false;
    private boolean activated = false;
    private float autoTriggerTimer = 0f;

    public MonkeyTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atPreBattle() {
        usedInCombat = false;
        this.activated = false;
        this.autoTriggerTimer = 0f;
        this.grayscale = false;
        stopPulse();
    }

    @Override
    public void atTurnStart() {
        if (this.usedInCombat) return;

        this.activated = false;
        this.autoTriggerTimer = 0f;
        stopPulse();

        // 半自动自启逻辑：仅在持有鼠符咒（共鸣）且手牌非空时触发
        if (TalismanLocator.isSemiAuto() && AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(RatTalisman.ID)) {
            // 注意：atTurnStart 时手牌可能还没抽。我们给一个“预备”标志，在 update 中检测
            this.activated = true;
            this.beginLongPulse();
            this.autoTriggerTimer = 0.6f; // 延迟触发，给玩家反应时间
        }
    }

    @Override
    public void onVictory() {
        usedInCombat = false;
        this.activated = false;
        this.autoTriggerTimer = 0f;
        this.grayscale = false;
        stopPulse();
    }

    public void setActivated(boolean activate) {
        // 猴符咒只有在持有鼠符咒共鸣时才存在局内激活
        if (!com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(RatTalisman.ID)) return;
        if (this.usedInCombat) return;

        this.activated = activate;
        if (activate) {
            beginLongPulse();
            this.autoTriggerTimer = 0.6f;
        } else {
            stopPulse();
            this.autoTriggerTimer = 0f;
        }
    }

    /**
     * 在营火处添加猴符咒专属的【七十二变】按钮。
     */
    @Override
    public void addCampfireOption(ArrayList<AbstractCampfireOption> options) {
        options.add(new MonkeyTalismanCampfireOption());
    }

    @Override
    public void onRest() {
        // 旧版逻辑保留但屏蔽：以前点击“休息”回血后会自动触发七十二变。
        // 现在七十二变已经拆成独立营火选项，休息只负责回血，不再触发换牌。
        if (false && !AbstractDungeon.player.masterDeck.getPurgeableCards().isEmpty()) {
            this.flash();
            // 旧版休息触发入口：不再立即开窗，改为置状态位让 update 处理顺序执行，避免与捕梦网冲突。
            this.state = State.SELECTING_TARGET;
        }
    }

    @Override
    public void update() {
        super.update();
        
        // --- 处理右键点击：切换预备状态 ---
        if (canInteractInCombat() && !usedInCombat) {
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(RatTalisman.ID)) {
                if (this.hb.hovered && com.megacrit.cardcrawl.helpers.input.InputHelper.justClickedRight) {
                    this.activated = !this.activated;
                    if (this.activated) {
                        com.megacrit.cardcrawl.core.CardCrawlGame.sound.play("UI_CLICK_1");
                        this.beginLongPulse();
                        this.autoTriggerTimer = 0f;
                        // 逻辑统一：手动点开直接触发（保持原样）
                        triggerCombatAction();
                        this.activated = false;
                    } else {
                        com.megacrit.cardcrawl.core.CardCrawlGame.sound.play("UI_CLICK_2");
                        this.stopPulse();
                        this.autoTriggerTimer = 0f;
                    }
                }
            }
        }

        // --- 半自动倒计时触发 ---
        if (this.activated && this.autoTriggerTimer > 0) {
            this.autoTriggerTimer -= com.badlogic.gdx.Gdx.graphics.getDeltaTime();
            if (this.autoTriggerTimer <= 0) {
                this.autoTriggerTimer = 0f;
                if (!AbstractDungeon.player.hand.isEmpty()) {
                   triggerCombatAction();
                }
                this.activated = false;
                this.stopPulse();
            }
        }

        // --- 营地变幻第一阶段：等待界面空闲（如捕梦网）后再打开网格 ---
        if (this.state == State.SELECTING_TARGET && !AbstractDungeon.isScreenUp && AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractDungeon.gridSelectScreen.open(
                    AbstractDungeon.player.masterDeck.getPurgeableCards(), 
                    1, 
                    "选择一张卡牌进行“七十二变” (变幻原型)", 
                    false, false, false, false
            );
        }

        // --- 营地变幻处理逻辑：选好了变幻原型 ---
        if (this.state == State.SELECTING_TARGET && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            this.targetCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();

            // 构建本职业全卡池供玩家挑选
            CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            boolean hasCollaboration = AbstractDungeon.player.hasRelic(CollaborationRelic.ID);

            for (AbstractCard c : CardLibrary.getAllCards()) {
                // 筛选出本职业颜色的、非诅咒、非状态牌
                if (c.color == AbstractDungeon.player.getCardColor() && 
                    c.type != AbstractCard.CardType.CURSE && 
                    c.type != AbstractCard.CardType.STATUS) {
                    
                    // 1. 常规卡池：排除掉 SPECIAL（特殊卡）
                    if (c.rarity != AbstractCard.CardRarity.SPECIAL) {
                        group.addToBottom(c.makeStatEquivalentCopy());
                    } 
                    // 2. 龙小组扩展包：如果持有【合作】遗物，额外允许带有 TEAM_JACKIE 标签的卡牌进入池子
                    else if (hasCollaboration && c.hasTag(CustomTags.TEAM_JACKIE)) {
                        group.addToBottom(c.makeStatEquivalentCopy());
                    }
                }
            }
            
            this.state = State.SELECTING_REPLACEMENT;
            // 第二步：打开网格选变幻后的模样
            AbstractDungeon.gridSelectScreen.open(group, 1, "选择变幻后的模样 (七十二变)", false);
        } 
        
        // 第二阶段回调：选好了营地变幻目标新牌
        else if (this.state == State.SELECTING_REPLACEMENT && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard replacementCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            this.state = State.IDLE;

            if (this.targetCard != null) {
                // 营地主卡组替换 (还原为直接操作，Action 在营地不生效)
                AbstractDungeon.player.masterDeck.removeCard(this.targetCard);
                AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
                        replacementCard.makeStatEquivalentCopy(), 
                        (float)Settings.WIDTH / 2.0F, 
                        (float)Settings.HEIGHT / 2.0F
                ));
            }
            
            this.flash();
            this.targetCard = null;
        }
    }

    private void triggerCombatAction() {
        if (usedInCombat) return;

        usedInCombat = true;
        this.grayscale = true;
        this.flash();
        com.megacrit.cardcrawl.core.CardCrawlGame.sound.play("MAGICAL");
        AbstractDungeon.actionManager.addToBottom(new basicmod.actions.MonkeyTalismanCombatAction());
    }

    @Override
    public String getUpdatedDescription() {
        return com.megacrit.cardcrawl.dungeons.AbstractDungeon.player != null && com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(RatTalisman.ID) ? (DESCRIPTIONS.length > 1 ? DESCRIPTIONS[1] : DESCRIPTIONS[0]) : DESCRIPTIONS[0];
    }
}
