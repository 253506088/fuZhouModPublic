package basicmod.actions;

import basemod.BaseMod;
import basicmod.enums.CustomTags;
import basicmod.helpers.MaskManager;
import basicmod.powers.masks.TaLaPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

/**
 * 面具脸动作。
 * 从抽牌堆中选择一张面具牌加入手牌（本回合0费），如果没有面具牌则生成3张随机影子卡。
 */
public class MaskFaceAction extends AbstractGameAction {
    /** 玩家对象 */
    private final AbstractPlayer player;
    /** 是否升级 */
    private final boolean upgraded;

    /**
     * 构造函数。
     *
     * @param upgraded 是否升级（升级时增加1点面具容量）
     */
    public MaskFaceAction(boolean upgraded) {
        this.player = AbstractDungeon.player;
        this.upgraded = upgraded;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_MED;
    }

    /**
     * 执行动作逻辑：从抽牌堆选择面具牌，或生成随机影子卡。
     */
    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_MED) {
            if (upgraded) {
                MaskManager.addExtraMaskCapacity(1);
            }

            CardGroup masks = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard c : player.drawPile.group) {
                if (c.tags.contains(CustomTags.MASK)) {
                    masks.addToBottom(c);
                }
            }

            if (masks.isEmpty()) {
                for (int i = 0; i < 3; i++) {
                    TaLaPower.queueRandomShadowKhanCard();
                }
                this.isDone = true;
                return;
            }

            AbstractDungeon.gridSelectScreen.open(masks, 1, "选择1张面具加入手牌（本回合0费）", false);
            tickDuration();
            return;
        }

        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard chosen = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            chosen.unhover();
            chosen.setCostForTurn(0);
            player.drawPile.removeCard(chosen);
            if (player.hand.size() < BaseMod.MAX_HAND_SIZE) {
                player.hand.addToTop(chosen);
            } else {
                player.discardPile.addToTop(chosen);
                player.createHandIsFullDialog();
            }
        }
        this.isDone = true;
    }
}
