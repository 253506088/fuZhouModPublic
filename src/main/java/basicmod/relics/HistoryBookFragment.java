package basicmod.relics;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import basicmod.BasicMod;
import basicmod.helpers.HistoryBookRewriteManager;
import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;

import java.util.ArrayList;

/**
 * 岁月史书残卷遗物。
 * 右键后会根据当前场景打开不同的“改写现实”菜单，并用 counter 保存剩余使用次数。
 */
public class HistoryBookFragment extends BaseRelic implements CustomSavable<Integer> {
    public static final String ID = BasicMod.makeID(HistoryBookFragment.class.getSimpleName());
    private static final int MAX_USES = 12;
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(BasicMod.makeID("HistoryBookFragmentUI"));

    private final ArrayList<AbstractCard> startingDrawPile = new ArrayList<>();
    private boolean selectingRewrite = false;

    /**
     * 创建残卷并初始化 12 次可用次数。
     */
    public HistoryBookFragment() {
        super(ID, "HistoryBookFragment", RelicTier.SPECIAL, LandingSound.MAGICAL);
        this.counter = MAX_USES;
        this.grayscale = false;
        refreshUsableState();
    }

    /**
     * 获得遗物时设置 12 次改写现实机会。
     */
    @Override
    public void onEquip() {
        super.onEquip();
        if (this.counter < 0) {
            this.counter = MAX_USES;
        }
        refreshUsableState();
    }

    /**
     * 进入新房间时清理只影响当前房间的岁月史书标记。
     */
    @Override
    public void onEnterRoom(AbstractRoom room) {
        HistoryBookRewriteManager.clearFlagsIfLeavingSpecialRoom(room);
    }

    /**
     * 战斗开局保存抽牌堆快照，用于后续“重置战斗开局牌堆”。
     */
    @Override
    public void atBattleStartPreDraw() {
        this.startingDrawPile.clear();
        if (AbstractDungeon.player != null && AbstractDungeon.player.drawPile != null) {
            for (AbstractCard card : AbstractDungeon.player.drawPile.group) {
                this.startingDrawPile.add(card.makeStatEquivalentCopy());
            }
        }
    }

    /**
     * 战斗结束后清理快照，避免跨战斗误用。
     */
    @Override
    public void onVictory() {
        this.startingDrawPile.clear();
        this.selectingRewrite = false;
    }

    /**
     * 处理右键打开菜单，以及玩家在菜单中选择或取消。
     */
    @Override
    public void update() {
        super.update();
        handleRewriteSelection();

        if (!canOpenRewriteMenu()) {
            return;
        }

        if (this.hb.hovered && InputHelper.justClickedRight) {
            openRewriteMenu();
        }
    }

    /**
     * 判断当前是否能打开残卷菜单。
     */
    private boolean canOpenRewriteMenu() {
        return CardCrawlGame.isInARun()
                && AbstractDungeon.player != null
                && this.counter > 0
                && !this.selectingRewrite;
    }

    /**
     * 根据当前房间类型打开对应的改写现实菜单。
     */
    private void openRewriteMenu() {
        CardGroup choices = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        AbstractRoom room = AbstractDungeon.getCurrRoom();

        if (canInteractInCombat()) {
            addCombatChoices(choices);
        } else if (room instanceof RestRoom) {
            addRestChoices(choices);
        } else if (room instanceof ShopRoom) {
            addShopChoices(choices);
        }

        if (choices.isEmpty()) {
            this.flash();
            return;
        }

        CardCrawlGame.sound.play("UI_CLICK_1");
        this.selectingRewrite = true;
        AbstractDungeon.gridSelectScreen.open(choices, 1, uiStrings.TEXT[0], false, false, true, false);
        AbstractDungeon.overlayMenu.cancelButton.show(uiStrings.TEXT[1]);
    }

    /**
     * 添加战斗中的改写选项。
     */
    private void addCombatChoices(CardGroup choices) {
        addChoiceIfAffordable(choices, RewriteOption.MONSTER_HP_TO_ONE, 2, uiStrings.TEXT[2], uiStrings.TEXT[3]);
        addChoiceIfAffordable(choices, RewriteOption.STUN_ALL_MONSTERS, 1, uiStrings.TEXT[4], uiStrings.TEXT[5]);
        addChoiceIfAffordable(choices, RewriteOption.DRAW_FULL_AND_GAIN_ENERGY, 1, uiStrings.TEXT[6], uiStrings.TEXT[7]);
        addChoiceIfAffordable(choices, RewriteOption.RESET_COMBAT_PILES, 1, uiStrings.TEXT[8], uiStrings.TEXT[9]);
        addChoiceIfAffordable(choices, RewriteOption.EXHAUST_STATUS_AND_CURSE, 1, uiStrings.TEXT[10], uiStrings.TEXT[11]);
    }

    /**
     * 添加休息处的改写选项。
     */
    private void addRestChoices(CardGroup choices) {
        addChoiceIfAffordable(choices, RewriteOption.CAMPFIRE_MULTI_SELECT, 2, uiStrings.TEXT[12], uiStrings.TEXT[13]);
        if (!HistoryBookRewriteManager.isRestRoomCreatedFromShop()) {
            addChoiceIfAffordable(choices, RewriteOption.REST_TO_FREE_SHOP, 4, uiStrings.TEXT[14], uiStrings.TEXT[15]);
        }
    }

    /**
     * 添加商人房的改写选项。
     */
    private void addShopChoices(CardGroup choices) {
        addChoiceIfAffordable(choices, RewriteOption.SHOP_FREE, 2, uiStrings.TEXT[16], uiStrings.TEXT[17]);
        if (!HistoryBookRewriteManager.isShopCreatedFromRestRoom()) {
            addChoiceIfAffordable(choices, RewriteOption.SHOP_TO_CAMPFIRE, 4, uiStrings.TEXT[18], uiStrings.TEXT[19]);
        }
        addChoiceIfAffordable(choices, RewriteOption.SHOP_RESTOCK, 6, uiStrings.TEXT[20], uiStrings.TEXT[21]);
    }

    /**
     * 次数足够时才把选项放进菜单。
     */
    private void addChoiceIfAffordable(CardGroup choices, RewriteOption option, int cost, String name, String description) {
        if (this.counter >= cost) {
            choices.addToBottom(new RewritePreviewCard(option, cost, name, description));
        }
    }

    /**
     * 处理菜单选择结果，取消不会扣次数。
     */
    private void handleRewriteSelection() {
        if (!this.selectingRewrite) {
            return;
        }

        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard selected = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            this.selectingRewrite = false;
            if (selected instanceof RewritePreviewCard) {
                applyRewrite((RewritePreviewCard) selected);
            }
        } else if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.GRID) {
            this.selectingRewrite = false;
        }
    }

    /**
     * 扣除次数并执行对应的改写现实效果。
     */
    private void applyRewrite(RewritePreviewCard card) {
        if (this.counter < card.cost) {
            this.flash();
            return;
        }

        this.counter -= card.cost;
        this.flash();
        executeRewrite(card.option);
        refreshUsableState();
    }

    /**
     * 分发具体的改写现实效果。
     */
    private void executeRewrite(RewriteOption option) {
        switch (option) {
            case MONSTER_HP_TO_ONE:
                lowerAllMonsterHpToOne();
                break;
            case STUN_ALL_MONSTERS:
                stunAllMonsters();
                break;
            case DRAW_FULL_AND_GAIN_ENERGY:
                drawFullHandAndGainEnergy();
                break;
            case RESET_COMBAT_PILES:
                resetCombatPiles();
                break;
            case EXHAUST_STATUS_AND_CURSE:
                exhaustStatusAndCurseCards();
                break;
            case CAMPFIRE_MULTI_SELECT:
                HistoryBookRewriteManager.enableCampfireMultiSelect();
                break;
            case REST_TO_FREE_SHOP:
                HistoryBookRewriteManager.transformRestRoomToShop(true);
                break;
            case SHOP_FREE:
                HistoryBookRewriteManager.enableShopFree();
                break;
            case SHOP_TO_CAMPFIRE:
                HistoryBookRewriteManager.transformShopToRestRoom(true);
                break;
            case SHOP_RESTOCK:
                HistoryBookRewriteManager.enableShopRestock();
                break;
        }
    }

    /**
     * 将所有存活怪物的当前血量降为 1。
     */
    private void lowerAllMonsterHpToOne() {
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (!monster.isDeadOrEscaped() && monster.currentHealth > 1) {
                monster.currentHealth = 1;
                monster.healthBarUpdatedEvent();
            }
        }
        CardCrawlGame.sound.play("ATTACK_HEAVY");
    }

    /**
     * 眩晕所有存活怪物 1 回合。
     */
    private void stunAllMonsters() {
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (!monster.isDeadOrEscaped()) {
                AbstractDungeon.actionManager.addToBottom(new StunMonsterAction(monster, AbstractDungeon.player, 1));
            }
        }
    }

    /**
     * 抽到当前手牌上限，并获得 12 点能量。
     */
    private void drawFullHandAndGainEnergy() {
        int drawAmount = Math.max(0, BaseMod.MAX_HAND_SIZE - AbstractDungeon.player.hand.size());
        if (drawAmount > 0) {
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, drawAmount));
        }
        AbstractDungeon.player.gainEnergy(12);
    }

    /**
     * 清空手牌、弃牌堆、抽牌堆，恢复到战斗开局保存的抽牌堆状态，然后抽5张牌。
     */
    private void resetCombatPiles() {
        AbstractDungeon.player.hand.clear();
        AbstractDungeon.player.discardPile.clear();
        AbstractDungeon.player.drawPile.clear();
        for (AbstractCard card : this.startingDrawPile) {
            AbstractDungeon.player.drawPile.addToBottom(card.makeStatEquivalentCopy());
        }
        AbstractDungeon.player.hand.refreshHandLayout();
        CardCrawlGame.sound.play("CARD_BURN");
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, 5));
    }

    /**
     * 将手牌、弃牌堆、抽牌堆里的状态牌和诅咒牌全部移动到消耗堆。
     */
    private void exhaustStatusAndCurseCards() {
        exhaustMatchingCards(AbstractDungeon.player.hand);
        exhaustMatchingCards(AbstractDungeon.player.discardPile);
        exhaustMatchingCards(AbstractDungeon.player.drawPile);
        AbstractDungeon.player.hand.refreshHandLayout();
    }

    /**
     * 消耗指定牌堆中的所有状态和诅咒。
     */
    private void exhaustMatchingCards(CardGroup group) {
        ArrayList<AbstractCard> targets = new ArrayList<>();
        for (AbstractCard card : group.group) {
            if (card.type == AbstractCard.CardType.STATUS || card.type == AbstractCard.CardType.CURSE) {
                targets.add(card);
            }
        }
        for (AbstractCard card : targets) {
            group.moveToExhaustPile(card);
        }
    }

    /**
     * 根据剩余次数刷新遗物显示状态。
     */
    private void refreshUsableState() {
        this.grayscale = this.counter <= 0;
        refreshDescription();
    }

    /**
     * 保存剩余次数。
     */
    @Override
    public Integer onSave() {
        return this.counter;
    }

    /**
     * 读取剩余次数。
     */
    @Override
    public void onLoad(Integer data) {
        this.counter = data == null ? MAX_USES : Math.max(0, data);
        refreshUsableState();
    }

    /**
     * 根据次数显示动态描述。
     */
    @Override
    public String getUpdatedDescription() {
        String base = DESCRIPTIONS[0];
        if (this.counter <= 0 && DESCRIPTIONS.length > 2) {
            return base + DESCRIPTIONS[2];
        }
        return base + DESCRIPTIONS[1] + Math.max(0, this.counter);
    }

    /**
     * 残卷支持的所有改写现实类型。
     */
    private enum RewriteOption {
        MONSTER_HP_TO_ONE,
        STUN_ALL_MONSTERS,
        DRAW_FULL_AND_GAIN_ENERGY,
        RESET_COMBAT_PILES,
        EXHAUST_STATUS_AND_CURSE,
        CAMPFIRE_MULTI_SELECT,
        REST_TO_FREE_SHOP,
        SHOP_FREE,
        SHOP_TO_CAMPFIRE,
        SHOP_RESTOCK
    }

    /**
     * 用于 GridSelectScreen 展示残卷选项的虚拟卡。
     * 它不会进入牌组，也不会被正常打出，只承载标题、描述、消耗和选项类型。
     */
    private static class RewritePreviewCard extends AbstractCard {
        private final RewriteOption option;
        private final int cost;
        private final String title;
        private final String detail;

        /**
         * 用虚拟卡片展示一次改写现实选项。
         */
        private RewritePreviewCard(RewriteOption option, int cost, String title, String detail) {
            super(BasicMod.makeID("HistoryBookRewrite_" + option.name()), title, null, -2,
                    detail + " NL NL " + uiStrings.TEXT[22] + " [#71b1d1]" + cost + "[]",
                    CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);
            this.option = option;
            this.cost = cost;
            this.title = title;
            this.detail = detail;
            this.initializeDescription();
        }

        /**
         * 虚拟卡片不会升级。
         */
        @Override
        public void upgrade() {
        }

        /**
         * 虚拟卡片不会被正常打出。
         */
        @Override
        public void use(com.megacrit.cardcrawl.characters.AbstractPlayer p, AbstractMonster m) {
        }

        /**
         * 网格界面需要复制卡片时，返回同一个展示选项的新实例。
         */
        @Override
        public AbstractCard makeCopy() {
            return new RewritePreviewCard(this.option, this.cost, this.title, this.detail);
        }
    }

    /**
     * 岁月史书残卷属于特殊遗物，不进入普通遗物池。
     */
    @Override
    public AbstractRelic makeCopy() {
        return new HistoryBookFragment();
    }
}
