package basicmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.lang.reflect.Field;

public class CurseJadeDeferredAction extends AbstractGameAction {
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
