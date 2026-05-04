package basicmod.actions;

import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;

import java.util.Collections;
import java.util.ArrayList;

public class YiKaExhaustAction extends AbstractGameAction {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:MaskActionsUI");
    private AbstractMonster m;

    public YiKaExhaustAction(AbstractMonster target) {
        this.actionType = ActionType.EXHAUST;
        this.duration = Settings.ACTION_DUR_FAST;
        this.m = target;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (AbstractDungeon.player.hand.isEmpty()) {
                this.isDone = true;
                return;
            }

            // 使用 GridSelectScreen，尝试同时设置 isOptional 和 anyNumber 为 true 以恢复取消按钮
            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.hand, 1, uiStrings.TEXT[1], false, false, true, true);
            tickDuration();
            return;
        }

        // 处理从 GridSelectScreen 返回的结果
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                // 消耗所选卡牌
                AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(c, AbstractDungeon.player.hand));
                // 触发加成效果
                triggerEffect(c);
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            AbstractDungeon.player.hand.refreshHandLayout();
        }
        
        this.isDone = true;
    }

    private void triggerEffect(AbstractCard c) {
        switch (c.type) {
            case ATTACK:
                // 消耗攻击牌：造成其伤害的一半（向下取整）
                int bonusDmg = c.damage / 2;
                if (bonusDmg > 0) {
                    AbstractDungeon.actionManager.addToTop(new DamageAction(m, new DamageInfo(AbstractDungeon.player, bonusDmg, DamageInfo.DamageType.NORMAL), AttackEffect.SLASH_HEAVY));
                }
                break;
            case SKILL:
                // 消耗技能牌：并抽取两张牌
                AbstractDungeon.actionManager.addToTop(new DrawCardAction(2));
                break;
            case POWER:
                // 消耗能力牌：随机赋予（虚弱2、易伤2、力量-2、敏捷-2）
                applyRandomPower();
                break;
            case CURSE:
                // 消耗诅咒牌：赋予眩晕
                AbstractDungeon.actionManager.addToTop(new StunMonsterAction(m, AbstractDungeon.player));
                break;
            case STATUS:
                // 消耗状态牌：赋予 (1 + 当前回合数) 的中毒
                int poisonAmount = 1 + com.megacrit.cardcrawl.actions.GameActionManager.turn;
                AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(m, AbstractDungeon.player, new PoisonPower(m, AbstractDungeon.player, poisonAmount), poisonAmount));
                break;
            default:
                break;
        }
    }

    private void applyRandomPower() {
        ArrayList<AbstractPower> powers = new ArrayList<>();
        powers.add(new WeakPower(m, 2, false));
        powers.add(new VulnerablePower(m, 2, false));
        powers.add(new StrengthPower(m, -2));
        powers.add(new DexterityPower(m, -2));
        
        Collections.shuffle(powers);
        AbstractPower chosen = powers.get(0);
        AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(m, AbstractDungeon.player, chosen, chosen.amount));
    }
}
