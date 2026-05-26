package basicmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

/**
 * 双倍格挡动作。
 * 将玩家当前格挡值翻倍，通过标志位防止补丁重复触发。
 */
public class DoubleBlockAction extends AbstractGameAction {
    /**
     * 构造函数。
     */
    public DoubleBlockAction() {
        this.actionType = ActionType.BLOCK;
        this.duration = Settings.ACTION_DUR_XFAST;
    }

    /**
     * 执行动作逻辑：通过压栈实现格挡翻倍，使用标志位防止补丁重复触发。
     */
    @Override
    public void update() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p.currentBlock > 0) {
            // 按照压栈顺序（addToTop是顶端加入），后加的先执行
            // 3. 恢复标志位
            addToTop(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
                @Override
                public void update() {
                    basicmod.util.MechanicsContext.isProcessingDoubling = false;
                    this.isDone = true;
                }
            });
            // 2. 执行加格挡（由于标志位已开启，此时不会触发补丁翻倍）
            addToTop(new GainBlockAction(p, p, p.currentBlock));
            // 1. 开启标志位
            addToTop(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
                @Override
                public void update() {
                    basicmod.util.MechanicsContext.isProcessingDoubling = true;
                    this.isDone = true;
                }
            });
        }
        this.isDone = true;
    }
}
