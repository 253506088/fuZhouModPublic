package basicmod.actions;

import basicmod.enums.CustomTags;
import basicmod.relics.CollaborationRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;

public class MonkeyTalismanCombatAction extends AbstractGameAction {
    private boolean openedGridScreen = false;
    private AbstractCard targetCard = null;
    private CardGroup replacementPool;

    public MonkeyTalismanCombatAction() {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
        
        // 构建全职业牌池
        this.replacementPool = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        boolean hasCollaboration = AbstractDungeon.player.hasRelic(CollaborationRelic.ID);
        for (AbstractCard c : CardLibrary.getAllCards()) {
            if (c.color == AbstractDungeon.player.getCardColor() && 
                c.type != AbstractCard.CardType.CURSE && 
                c.type != AbstractCard.CardType.STATUS &&
                (c.rarity != AbstractCard.CardRarity.SPECIAL || (hasCollaboration && c.hasTag(CustomTags.TEAM_JACKIE)))) {
                this.replacementPool.addToBottom(c.makeStatEquivalentCopy());
            }
        }
        this.replacementPool.sortAlphabetically(true);
        this.replacementPool.sortByRarityPlusStatusCardType(false);
    }

    @Override
    public void update() {
        // 如果手牌为空，或者牌池没牌，无法变幻
        if (AbstractDungeon.player.hand.isEmpty() || this.replacementPool.isEmpty()) {
            this.isDone = true;
            return;
        }

        // 阶段1：打开手牌选择界面
        if (this.duration == Settings.ACTION_DUR_FAST) {
            AbstractDungeon.handCardSelectScreen.open("选择一张要变幻的卡牌", 1, false, false);
            this.tickDuration();
            return;
        }

        // 阶段2：处理手牌选择结果
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                this.targetCard = c;
                // 从展示板上取下来放入手中，等待后续销毁或替换（原版手牌选择界面的标准做法，避免卡片丢失）
                AbstractDungeon.player.hand.addToTop(c);
            }
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
        }

        // 阶段3：如果已经选好了被变幻的手牌，打开网格界面挑选新牌
        if (this.targetCard != null && !this.openedGridScreen) {
            this.openedGridScreen = true;
            AbstractDungeon.gridSelectScreen.open(this.replacementPool, 1, "选择一张卡牌变幻到手牌中", false);
            return;
        }

        // 阶段4：处理网格界面选择的新牌
        if (this.openedGridScreen && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard newCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0).makeStatEquivalentCopy();
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            
            // 继承升级状态
            if (this.targetCard.upgraded) {
                newCard.upgrade();
            }
            // 真正地将原来选中的那张手牌消耗或者移除（这里使用直接移除）
            AbstractDungeon.player.hand.removeCard(this.targetCard);
            
            // 将选择到的新卡牌加入手牌
            AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(newCard, 1));
            
            this.isDone = true;
        }
    }
}
