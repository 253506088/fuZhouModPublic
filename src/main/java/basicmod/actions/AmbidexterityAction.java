package basicmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.PutOnDeckAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class AmbidexterityAction extends AbstractGameAction {
    private int drawAmount;

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
