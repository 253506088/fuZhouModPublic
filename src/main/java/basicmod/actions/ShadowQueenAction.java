package basicmod.actions;

import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class ShadowQueenAction extends AbstractGameAction {
    public ShadowQueenAction() {
        this.actionType = ActionType.SPECIAL;
    }

    @Override
    public void update() {
        // 1. 扫描消耗牌堆，找出所有的黑影兵团卡片
        // AbstractDungeon.player.exhaustPile.group 的 0 号元素即为“最早进入消耗堆”的卡牌
        ArrayList<AbstractCard> shadowKhanCards = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.exhaustPile.group) {
            if (c.tags.contains(CustomTags.SHADOW_KHAN)) {
                shadowKhanCards.add(c);
            }
        }

        // 2. 释放虚影并净化原始卡牌
        for (AbstractCard original : shadowKhanCards) {
            AbstractCard copy = original.makeStatEquivalentCopy();
            copy.purgeOnUse = true; // 设置为释放后直接从战斗中移除
            
            // 强化后不再净化：保留消耗牌堆中的原始兵团卡
            
            // 使用 NewQueueCardAction 将释放动作加入队列
            addToBot(new NewQueueCardAction(copy, true, true, true));
        }

        this.isDone = true;
    }
}
