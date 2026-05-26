package basicmod.actions;

import basicmod.helpers.MaskManager;
import basicmod.relics.ShadowKhanToken;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

/**
 * 明塔消耗动作。
 * 消耗手牌中的攻击牌，根据消耗的卡牌伤害值增加总伤害。
 */
public class MingTaExhaustAction extends AbstractGameAction {
    /** UI字符串 */
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:MaskActionsUI");
    /** 目标怪物 */
    private AbstractMonster m;
    /** 基础伤害值 */
    private int baseDamage;
    /** 源卡牌（不消耗自身） */
    private AbstractCard sourceCard;
    /** 非攻击牌列表 */
    private ArrayList<AbstractCard> nonAttacks = new ArrayList<>();
    /** 可被明塔消耗的攻击牌列表 */
    private ArrayList<AbstractCard> attackCards = new ArrayList<>();

    /**
     * 构造函数。
     *
     * @param target 目标怪物
     * @param baseDamage 基础伤害值
     * @param sourceCard 源卡牌
     */
    public MingTaExhaustAction(AbstractMonster target, int baseDamage, AbstractCard sourceCard) {
        this.actionType = ActionType.EXHAUST;
        this.duration = Settings.ACTION_DUR_FAST;
        this.m = target;
        this.baseDamage = baseDamage;
        this.sourceCard = sourceCard;
    }

    /**
     * 执行动作逻辑：选择攻击牌消耗，根据消耗的卡牌伤害值增加总伤害。
     */
    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                if (c.type != AbstractCard.CardType.ATTACK || c == sourceCard) {
                    nonAttacks.add(c);
                } else {
                    attackCards.add(c);
                }
            }

            if (attackCards.isEmpty()) {
                this.isDone = true;
                doBaseDamage();
                return;
            }

            if (!ShadowKhanToken.isManualExhaustMode()) {
                exhaustAndDamage(getRandomAttackCard());
                this.isDone = true;
                return;
            }

            if (attackCards.size() == 1) {
                exhaustAndDamage(attackCards.get(0));
                this.isDone = true;
                return;
            }

            AbstractDungeon.player.hand.group.removeAll(nonAttacks);
            AbstractDungeon.handCardSelectScreen.open(uiStrings.TEXT[0], 1, false, false, false, false);
            tickDuration();
            return;
        }

        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                AbstractDungeon.player.hand.addToTop(c); 
                exhaustAndDamage(c);
            }
            
            for (AbstractCard c : nonAttacks) {
                AbstractDungeon.player.hand.addToTop(c);
            }
            AbstractDungeon.player.hand.refreshHandLayout();
            
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            this.isDone = true;
        }
    }

    /**
     * 从可消耗攻击牌中随机选择1张。
     *
     * @return 随机攻击牌
     */
    private AbstractCard getRandomAttackCard() {
        int index = AbstractDungeon.cardRandomRng.random(attackCards.size() - 1);
        return attackCards.get(index);
    }

    /**
     * 消耗卡牌并累加伤害。
     *
     * @param c 要消耗的卡牌
     */
    private void exhaustAndDamage(AbstractCard c) {
        int dmg = c.baseDamage > 0 ? c.baseDamage : 0;
        MaskManager.mingTaTotalDamage += dmg;
        AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(c, AbstractDungeon.player.hand));
        doBaseDamage();
    }

    /**
     * 执行基础伤害（基础伤害 + 累计消耗伤害）。
     */
    private void doBaseDamage() {
        int totalDamage = baseDamage + MaskManager.mingTaTotalDamage;
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(AbstractDungeon.player, totalDamage, DamageInfo.DamageType.NORMAL), AttackEffect.SLASH_HEAVY));
    }
}
