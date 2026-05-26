package basicmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.PutOnDeckAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

/**
 * 左右手互搏动作。
 * 执行顺序：抽牌 -> 弃牌 -> 置顶（通过addToTop实现逆序执行）。
 */
public class AmbidexterityAction extends AbstractGameAction {
    /** 抽牌数量 */
    private int drawAmount;

    /**
     * 构造函数。
     *
     * @param drawAmount 抽牌数量
     */
    public AmbidexterityAction(int drawAmount) {
        this.drawAmount = drawAmount;
        this.actionType = ActionType.DRAW;
    }

    @Override
    public void update() {
        // 使用 addToTop 以保证执行顺序为：抽牌 -> 丢弃 -> 置顶
        addToTop(new PutOnDeckAction(AbstractDungeon.player, AbstractDungeon.player, 1, false));
        addToTop(new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, 1, false));
        addToTop(new DrawCardAction(drawAmount));
        this.isDone = true;
    }
}
