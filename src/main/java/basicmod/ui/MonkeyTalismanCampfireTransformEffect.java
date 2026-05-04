package basicmod.ui;

import basicmod.BasicMod;
import basicmod.cards.demons.CardDemonQiFake;
import basicmod.enums.CustomTags;
import basicmod.relics.CollaborationRelic;
import basicmod.relics.MonkeyTalisman;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class MonkeyTalismanCampfireTransformEffect extends AbstractGameEffect {
    private enum Phase {
        CHOOSE_TARGET,
        CHOOSE_REPLACEMENT
    }

    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:MonkeyTalismanCampfireUI");
    private static final String CANCEL_CARD_ID = BasicMod.makeID("MonkeyTalismanCampfireCancel");

    private final MonkeyTalisman monkeyTalisman;
    private Phase phase = Phase.CHOOSE_TARGET;
    private AbstractCard targetCard;

    /**
     * 创建猴符咒营火【七十二变】流程，并立刻打开第一段选牌界面。
     */
    public MonkeyTalismanCampfireTransformEffect(MonkeyTalisman monkeyTalisman) {
        this.monkeyTalisman = monkeyTalisman;
        this.duration = 0.1f;
        openTargetSelection();
    }

    /**
     * 根据当前阶段轮询玩家的网格选择结果。
     */
    @Override
    public void update() {
        if (phase == Phase.CHOOSE_TARGET) {
            updateTargetSelection();
        } else {
            updateReplacementSelection();
        }
    }

    /**
     * 打开第一段界面：让玩家选择要被七十二变替换掉的原型牌。
     */
    private void openTargetSelection() {
        AbstractDungeon.gridSelectScreen.selectedCards.clear();
        CardGroup targets = AbstractDungeon.player.masterDeck.getPurgeableCards();
        addCancelCardToFirstGridSlot(targets);
        if (targets.isEmpty()) {
            showNoTargetMessage();
            cancelAndReturnToCampfire();
            return;
        }

        AbstractDungeon.gridSelectScreen.open(targets, 1, uiStrings.TEXT[3], false, false, true, false);
        phase = Phase.CHOOSE_TARGET;
    }

    /**
     * 处理第一段选择；如果玩家取消，则回到营火且不消耗机会。
     */
    private void updateTargetSelection() {
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            targetCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            if (isCancelCard(targetCard)) {
                cancelAndReturnToCampfire();
                return;
            }
            openReplacementSelection();
            return;
        }

        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.GRID) {
            // 取消第一段选择时，只返回营火，不做任何卡组变更。
            cancelAndReturnToCampfire();
        }
    }

    /**
     * 打开第二段界面：让玩家选择变幻后的目标牌。
     */
    private void openReplacementSelection() {
        CardGroup replacements = buildReplacementPool();
        addCancelCardToFirstGridSlot(replacements);
        if (replacements.isEmpty()) {
            showNoReplacementMessage();
            cancelAndReturnToCampfire();
            return;
        }

        AbstractDungeon.gridSelectScreen.open(replacements, 1, uiStrings.TEXT[4], false, false, true, false);
        phase = Phase.CHOOSE_REPLACEMENT;
    }

    /**
     * 处理第二段选择；成功后替换主卡组中的牌，并消耗本次营火机会。
     */
    private void updateReplacementSelection() {
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard replacementCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            if (isCancelCard(replacementCard)) {
                cancelAndReturnToCampfire();
                return;
            }
            transformCard(replacementCard);
            consumeRestAndFinish();
            return;
        }

        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.GRID) {
            // 取消第二段选择时，原型牌还没有被移除，可以安全返回营火。
            cancelAndReturnToCampfire();
        }
    }

    /**
     * 构建猴符咒营火变牌的目标牌池。
     */
    private CardGroup buildReplacementPool() {
        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        boolean hasCollaboration = AbstractDungeon.player.hasRelic(CollaborationRelic.ID);

        for (AbstractCard card : CardLibrary.getAllCards()) {
            if (isValidReplacementCard(card, hasCollaboration)) {
                group.addToBottom(card.makeStatEquivalentCopy());
            }
        }

        group.sortAlphabetically(true);
        group.sortByRarityPlusStatusCardType(false);
        return group;
    }

    /**
     * 在网格显示的第一个位置加入【返回营火】假卡，确保玩家一进界面就能看到退出入口。
     */
    private void addCancelCardToFirstGridSlot(CardGroup group) {
        // 网格界面会优先显示 CardGroup 底部元素，所以这里用 addToBottom 放到左上第一格。
        group.addToBottom(createCancelCard());
    }

    /**
     * 创建七十二变专用的【返回营火】假卡。
     */
    private AbstractCard createCancelCard() {
        AbstractCard cancelCard = new CardDemonQiFake(
                uiStrings.TEXT[5],
                BasicMod.imagePath("ui/back.png"),
                uiStrings.TEXT[6]);
        cancelCard.cardID = CANCEL_CARD_ID;
        return cancelCard;
    }

    /**
     * 判断玩家选择的是否是【返回营火】假卡。
     */
    private boolean isCancelCard(AbstractCard card) {
        return card != null && CANCEL_CARD_ID.equals(card.cardID);
    }

    /**
     * 判断某张牌能不能作为七十二变的目标牌。
     */
    private boolean isValidReplacementCard(AbstractCard card, boolean hasCollaboration) {
        if (card.color != AbstractDungeon.player.getCardColor()) {
            return false;
        }
        if (card.type == AbstractCard.CardType.CURSE || card.type == AbstractCard.CardType.STATUS) {
            return false;
        }

        // 常规情况下不允许特殊牌；持有【合作】时，额外放行龙小组特殊牌。
        return card.rarity != AbstractCard.CardRarity.SPECIAL || (hasCollaboration && card.hasTag(CustomTags.TEAM_JACKIE));
    }

    /**
     * 执行主卡组替换：移除原型牌，并把玩家选择的新牌加入主卡组。
     */
    private void transformCard(AbstractCard replacementCard) {
        if (targetCard == null || replacementCard == null) {
            return;
        }

        AbstractDungeon.player.masterDeck.removeCard(targetCard);
        AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
                replacementCard.makeStatEquivalentCopy(),
                (float)Settings.WIDTH / 2.0F,
                (float)Settings.HEIGHT / 2.0F
        ));
        CardCrawlGame.metricData.addCampfireChoiceData("MONKEY_TALISMAN_TRANSFORM", targetCard.getMetricID());
        monkeyTalisman.flash();
        targetCard = null;
    }

    /**
     * 显示没有原型牌可选时的提示。
     */
    private void showNoTargetMessage() {
        AbstractDungeon.effectsQueue.add(new ThoughtBubble(
                AbstractDungeon.player.dialogX,
                AbstractDungeon.player.dialogY,
                2.5f,
                uiStrings.TEXT[2],
                true));
    }

    /**
     * 显示没有目标牌可选时的提示。
     */
    private void showNoReplacementMessage() {
        AbstractDungeon.effectsQueue.add(new ThoughtBubble(
                AbstractDungeon.player.dialogX,
                AbstractDungeon.player.dialogY,
                2.5f,
                uiStrings.TEXT[7],
                true));
    }

    /**
     * 取消七十二变并返回营火；取消不会消耗本次营火机会。
     */
    private void cancelAndReturnToCampfire() {
        AbstractDungeon.gridSelectScreen.selectedCards.clear();
        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID) {
            AbstractDungeon.closeCurrentScreen();
        }

        AbstractRoom room = AbstractDungeon.getCurrRoom();
        room.phase = AbstractRoom.RoomPhase.INCOMPLETE;
        if (room instanceof RestRoom) {
            RestRoom restRoom = (RestRoom) room;
            if (restRoom.campfireUI != null) {
                restRoom.campfireUI.reopen();
            }
        }
        this.isDone = true;
    }

    /**
     * 成功完成七十二变后，消耗营火机会并结束当前休息房间。
     */
    private void consumeRestAndFinish() {
        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID) {
            AbstractDungeon.closeCurrentScreen();
        }

        AbstractRoom.waitTimer = 0.0f;
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
        if (AbstractDungeon.getCurrRoom() instanceof RestRoom) {
            ((RestRoom) AbstractDungeon.getCurrRoom()).fadeIn();
        }
        this.isDone = true;
    }

    /**
     * 该营火流程不需要额外渲染内容，界面由网格选择器负责。
     */
    @Override
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch sb) {
    }

    /**
     * 没有额外贴图或声音资源需要手动释放。
     */
    @Override
    public void dispose() {
    }
}
