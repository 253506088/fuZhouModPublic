package basicmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.lang.reflect.Field;

/**
 * 诅咒玉延迟动作。
 * 等待抽牌阶段结束后，执行弃牌和向抽牌堆添加晕眩牌的效果。
 */
public class CurseJadeDeferredAction extends AbstractGameAction {
    /**
     * 执行动作逻辑：如果正在抽牌阶段则延迟执行，否则执行弃牌和添加晕眩牌。
     */
    @Override
    public void update() {
        this.isDone = true;
        if (AbstractDungeon.player == null || AbstractDungeon.actionManager == null) {
            return;
        }
        if (isDrawPhaseInProgress(AbstractDungeon.actionManager)) {
            addToBot(new CurseJadeDeferredAction());
            return;
        }
        if (!AbstractDungeon.player.hand.isEmpty()) {
            addToBot(new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, 1, true));
        }
        addToBot(new MakeTempCardInDrawPileAction(new Dazed(), 1, false, true, false));
    }

    /**
     * 检查是否正在进行抽牌阶段。
     * 通过检查动作队列和当前动作是否为DrawCardAction来判断。
     *
     * @param manager 游戏动作管理器
     * @return 如果正在抽牌返回true，否则返回false
     */
    private boolean isDrawPhaseInProgress(GameActionManager manager) {
        if (manager.actions != null) {
            for (AbstractGameAction action : manager.actions) {
                if (action instanceof DrawCardAction) {
                    return true;
                }
            }
        }
        try {
            Field currentActionField = GameActionManager.class.getDeclaredField("currentAction");
            currentActionField.setAccessible(true);
            Object current = currentActionField.get(manager);
            return current instanceof DrawCardAction;
        } catch (Exception ignored) {
            return false;
        }
    }
}
