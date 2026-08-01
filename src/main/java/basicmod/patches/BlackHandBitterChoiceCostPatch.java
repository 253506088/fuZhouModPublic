package basicmod.patches;

import basicmod.BasicMod;
import basicmod.powers.BlackHandBitterChoicePenaltyPower;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

/**
 * 苦涩的抉择加费补丁。
 * 在惩罚回合把所有非 X 费卡牌的本回合费用显示与实际费用增加。
 */
public class BlackHandBitterChoiceCostPatch {
    @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
    public static class BitterChoiceTaxFields {
        public static final SpireField<Boolean> taxedByBitterChoice = new SpireField<>(() -> false);
        public static final SpireField<Integer> preBitterChoiceCostForTurn = new SpireField<>(() -> 0);
    }

    /**
     * 手牌预览刷新时调整费用。
     */
    @SpirePatch(clz = AbstractCard.class, method = "applyPowers")
    public static class ApplyPowersPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance) {
            adjustCost(__instance);
        }
    }

    /**
     * 悬停目标计算伤害时调整费用。
     */
    @SpirePatch(clz = AbstractCard.class, method = "calculateCardDamage")
    public static class CalculateDamagePatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance, com.megacrit.cardcrawl.monsters.AbstractMonster mo) {
            adjustCost(__instance);
        }
    }

    /**
     * 抽到牌时调整费用。
     */
    @SpirePatch(clz = AbstractCard.class, method = "triggerWhenDrawn")
    public static class TriggerWhenDrawnPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance) {
            adjustCost(__instance);
        }
    }

    private static void adjustCost(AbstractCard card) {
        if (card == null || AbstractDungeon.player == null || card.cost < 0) {
            return;
        }

        boolean alreadyTaxed = BitterChoiceTaxFields.taxedByBitterChoice.get(card);
        // 关键点：没被加过费且玩家身上没有惩罚能力时立即返回，避免高频刷新路径上做无用功。
        if (!alreadyTaxed && !AbstractDungeon.player.hasPower(BlackHandBitterChoicePenaltyPower.POWER_ID)) {
            return;
        }

        int taxAmount = BlackHandBitterChoicePenaltyPower.getActiveTaxAmount();
        if (taxAmount > 0) {
            if (!alreadyTaxed) {
                BitterChoiceTaxFields.preBitterChoiceCostForTurn.set(card, card.costForTurn);
                BitterChoiceTaxFields.taxedByBitterChoice.set(card, true);
            }
            int before = card.costForTurn;
            int preTax = BitterChoiceTaxFields.preBitterChoiceCostForTurn.get(card);
            card.costForTurn = Math.max(0, preTax + taxAmount);
            card.isCostModifiedForTurn = card.costForTurn != card.cost;
            if (before != card.costForTurn) {
                BasicMod.logger.debug("【苦涩的抉择-加费】牌={}({}) 费用 {} -> {}，原本本回合费用={}。",
                        card.name, card.cardID, before, card.costForTurn, preTax);
            }
            return;
        }

        if (alreadyTaxed) {
            int before = card.costForTurn;
            int restore = BitterChoiceTaxFields.preBitterChoiceCostForTurn.get(card);
            card.costForTurn = restore;
            card.isCostModifiedForTurn = card.costForTurn != card.cost;
            BitterChoiceTaxFields.taxedByBitterChoice.set(card, false);
            if (before != card.costForTurn) {
                BasicMod.logger.debug("【苦涩的抉择-恢复费用】牌={}({}) 费用 {} -> {}。",
                        card.name, card.cardID, before, card.costForTurn);
            }
        }
    }
}
