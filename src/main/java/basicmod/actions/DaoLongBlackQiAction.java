package basicmod.actions;

import basicmod.cards.CardBlackHandAhFen;
import basicmod.cards.CardBlackHandChow;
import basicmod.cards.CardBlackHandRatso;
import basicmod.cards.CardCui;
import basicmod.cards.CardGan;
import basicmod.cards.CardTaiShanPress;
import basicmod.cards.CardTornado;
import basicmod.cards.CardWen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.List;

/**
 * 刀龙黑气动作。
 * 将牌组中的特定卡牌替换为对应的强化版本。
 */
public class DaoLongBlackQiAction extends AbstractGameAction {
    /** 是否升级 */
    private boolean upgraded;

    /**
     * 构造函数。
     *
     * @param upgraded 是否升级替换后的卡牌
     */
    public DaoLongBlackQiAction(boolean upgraded) {
        this.upgraded = upgraded;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    /**
     * 执行动作逻辑：替换手牌、抽牌堆、弃牌堆中的特定卡牌。
     */
    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            replaceInGroup(AbstractDungeon.player.hand);
            replaceInGroup(AbstractDungeon.player.drawPile);
            replaceInGroup(AbstractDungeon.player.discardPile);
            this.isDone = true;
        }
        tickDuration();
    }

    /**
     * 在指定卡牌组中替换特定卡牌。
     * 替换规则：周→甘，老鼠→文，阿芬→翠，泰山压顶→龙卷风。
     *
     * @param group 要处理的卡牌组
     */
    private void replaceInGroup(CardGroup group) {
        List<AbstractCard> toReplace = new ArrayList<>();
        for (AbstractCard c : group.group) {
            String id = c.cardID;
            if (id.equals(CardBlackHandChow.ID) || 
                id.equals(CardBlackHandRatso.ID) || 
                id.equals(CardBlackHandAhFen.ID) || 
                id.equals(CardTaiShanPress.ID)) {
                toReplace.add(c);
            }
        }

        for (AbstractCard oldCard : toReplace) {
            AbstractCard newCard = null;
            String id = oldCard.cardID;
            
            if (id.equals(CardBlackHandChow.ID)) newCard = new CardGan();
            else if (id.equals(CardBlackHandRatso.ID)) newCard = new CardWen();
            else if (id.equals(CardBlackHandAhFen.ID)) newCard = new CardCui();
            else if (id.equals(CardTaiShanPress.ID)) newCard = new CardTornado();

            if (newCard != null) {
                if (this.upgraded || oldCard.upgraded) {
                    newCard.upgrade();
                }

                int index = group.group.indexOf(oldCard);
                if (index != -1) {
                    group.group.set(index, newCard);
                    newCard.applyPowers();
                    if (group.type == CardGroup.CardGroupType.HAND) {
                        newCard.superFlash();
                    }
                }
            }
        }
    }
}
