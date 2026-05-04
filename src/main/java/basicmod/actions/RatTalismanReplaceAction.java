package basicmod.actions;

import basicmod.relics.RatTalisman;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;
import java.util.UUID;

// 鼠符咒替换流程动作：处理自动替换与多选一
public class RatTalismanReplaceAction extends AbstractGameAction {
    private final RatTalisman relic;
    // 是否已经打开过选择界面
    private boolean openedSelection = false;

    public RatTalismanReplaceAction(RatTalisman relic) {
        this.relic = relic;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (relic == null || !isCombatActive()) {
            finish();
            return;
        }
        // 只允许在玩家回合执行，避免队列延迟到敌方回合时误触发
        if (AbstractDungeon.actionManager == null || AbstractDungeon.actionManager.turnHasEnded) {
            finish();
            return;
        }

        if (!openedSelection) {
            ArrayList<AbstractCard> candidates = relic.getReplaceCandidates();
            if (candidates.isEmpty()) {
                finish();
                return;
            }

            // 只有1张候选牌时直接替换
            if (candidates.size() == 1) {
                relic.tryReplaceCard(candidates.get(0));
                finish();
                return;
            }

            // 有其他界面时先等待，避免与现有界面冲突
            if (AbstractDungeon.isScreenUp) {
                return;
            }

            // 多张候选牌时弹出选择界面，由玩家决定替换目标
            CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard card : candidates) {
                group.addToTop(card);
            }

            AbstractDungeon.gridSelectScreen.open(group, 1, RatTalisman.SELECT_PROMPT, false, false, false, false);
            openedSelection = true;
            return;
        }

        if (AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            return;
        }

        AbstractCard chosen = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
        AbstractDungeon.gridSelectScreen.selectedCards.clear();

        AbstractCard targetInHand = findCardInHand(chosen.uuid);
        if (targetInHand != null) {
            relic.tryReplaceCard(targetInHand);
        } else {
            // 若所选牌已不在手牌，退化为替换当前第一张候选牌
            ArrayList<AbstractCard> fallback = relic.getReplaceCandidates();
            if (!fallback.isEmpty()) {
                relic.tryReplaceCard(fallback.get(0));
            }
        }

        finish();
    }

    // 通过UUID在当前手牌中定位玩家选中的那张牌
    private AbstractCard findCardInHand(UUID uuid) {
        if (AbstractDungeon.player == null || AbstractDungeon.player.hand == null) {
            return null;
        }

        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (card.uuid.equals(uuid)) {
                return card;
            }
        }
        return null;
    }

    private boolean isCombatActive() {
        if (AbstractDungeon.player == null || AbstractDungeon.getCurrRoom() == null) {
            return false;
        }
        return AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;
    }

    // 结束动作并通知遗物释放排队标记
    private void finish() {
        relic.markCheckResolved();
        this.isDone = true;
    }
}
