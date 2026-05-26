package basicmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 独木桥动作。
 * 消耗手牌，从抽牌堆顶打出等量的卡牌（免费且消耗后移除）。
 */
public class SinglePlankBridgeAction extends AbstractGameAction {
    /** 是否升级 */
    private boolean upgraded;

    /**
     * 构造函数。
     *
     * @param upgraded 是否升级（升级时额外打出1张）
     */
    public SinglePlankBridgeAction(boolean upgraded) {
        this.actionType = ActionType.WAIT;
        this.duration = Settings.ACTION_DUR_FAST;
        this.upgraded = upgraded;
    }

    /**
     * 执行动作逻辑：消耗手牌，从抽牌堆顶打出等量卡牌。
     */
    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (AbstractDungeon.player.hand.isEmpty()) {
                this.isDone = true;
                return;
            }
            AbstractDungeon.handCardSelectScreen.open("选择消耗手牌以打出抽牌堆顶部的卡", 99, true, true);
            tickDuration();
            return;
        }

        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            int count = AbstractDungeon.handCardSelectScreen.selectedCards.size();
            // 消耗选中的牌 (如果有选的话)
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                AbstractDungeon.player.hand.moveToExhaustPile(c);
            }
            
            // 计算总播放次数
            int totalPlays = count;
            if (upgraded) {
                totalPlays += 1;
            }

            // 只要播放次数大于 0 且抽牌堆有牌，就执行打出逻辑
            if (totalPlays > 0 && !AbstractDungeon.player.drawPile.isEmpty()) {
                AbstractCard card = AbstractDungeon.player.drawPile.getTopCard();
                // 将原卡从抽牌堆移除，防止中途被干扰
                AbstractDungeon.player.drawPile.group.remove(card);
                AbstractDungeon.getCurrRoom().souls.remove(card);
                
                for (int i = 0; i < totalPlays; i++) {
                    AbstractCard tmp = card.makeStatEquivalentCopy();
                    tmp.freeToPlayOnce = true;
                    
                    // 除了最后一次播放，其他的播放后都自动移除(purge)，防止产生多个实体卡
                    if (i < totalPlays - 1) {
                        tmp.purgeOnUse = true;
                    }

                    // 为每次打击寻找随机目标（针对攻击牌）
                    AbstractMonster target = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                    
                    // 将卡牌加入 limbo 区域以进行播放
                    AbstractDungeon.player.limbo.addToBottom(tmp);
                    addToBot(new NewQueueCardAction(tmp, target, false, true));
                    addToBot(new UnlimboAction(tmp));
                }
            }
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            this.isDone = true;
        }
        tickDuration();
    }
}
