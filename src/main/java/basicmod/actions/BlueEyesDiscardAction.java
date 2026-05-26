package basicmod.actions;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

/**
 * 青眼白龙消耗手牌Action
 * 从手牌中随机消耗指定数量的卡牌
 */
public class BlueEyesDiscardAction extends AbstractGameAction {
    private AbstractPlayer player;
    private int exhaustCount;

    /**
     * @param player       玩家
     * @param exhaustCount 需要消耗的手牌数量
     */
    public BlueEyesDiscardAction(AbstractPlayer player, int exhaustCount) {
        this.player = player;
        this.exhaustCount = exhaustCount;
        this.duration = Settings.ACTION_DUR_FAST;
        this.actionType = ActionType.EXHAUST;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            // 从手牌中随机选择卡牌消耗
            int actualCount = Math.min(exhaustCount, player.hand.size());
            BasicMod.logger.info("青眼白龙：随机消耗 " + actualCount + " 张手牌");

            for (int i = 0; i < actualCount; i++) {
                if (player.hand.isEmpty()) break;
                // 随机选一张手牌
                AbstractCard randomCard = player.hand.getRandomCard(AbstractDungeon.cardRandomRng);
                if (randomCard != null) {
                    player.hand.moveToExhaustPile(randomCard);
                }
            }
        }
        tickDuration();
    }
}
