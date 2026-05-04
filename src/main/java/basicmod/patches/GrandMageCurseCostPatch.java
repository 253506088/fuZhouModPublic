package basicmod.patches;

import basicmod.BasicMod;
import basicmod.powers.GrandMageCurseTaxAttackPower;
import basicmod.powers.GrandMageCurseTaxSkillPower;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class GrandMageCurseCostPatch {
    @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
    public static class TaxTrackFields {
        public static final SpireField<Boolean> taxedByGrandMageCurse = new SpireField<>(() -> false);
        public static final SpireField<Integer> preTaxCostForTurn = new SpireField<>(() -> 0);
    }

    @SpirePatch(clz = AbstractCard.class, method = "applyPowers")
    public static class ApplyPowersPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance) {
            adjustCost(__instance);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "calculateCardDamage")
    public static class CalculateDamagePatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance, com.megacrit.cardcrawl.monsters.AbstractMonster mo) {
            adjustCost(__instance);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerWhenDrawn")
    public static class TriggerWhenDrawnPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance) {
            adjustCost(__instance);
        }
    }

    private static void adjustCost(AbstractCard c) {
        if (c == null || AbstractDungeon.player == null || c.cost < 0) {
            return;
        }

        int taxAmount = getTaxAmount(c);
        if (taxAmount > 0) {
            if (!TaxTrackFields.taxedByGrandMageCurse.get(c)) {
                TaxTrackFields.preTaxCostForTurn.set(c, c.costForTurn);
                TaxTrackFields.taxedByGrandMageCurse.set(c, true);
            }
            int before = c.costForTurn;
            int preTax = TaxTrackFields.preTaxCostForTurn.get(c);
            c.costForTurn = Math.max(0, preTax + taxAmount);
            c.isCostModifiedForTurn = (c.costForTurn != c.cost);
            if (before != c.costForTurn) {
                BasicMod.logger.info("【诅咒税-显示加费】牌={}({}) costForTurn {} -> {}，preTax={}，baseCost={}。",
                        c.name, c.cardID, before, c.costForTurn, preTax, c.cost);
            }
            return;
        }

        if (TaxTrackFields.taxedByGrandMageCurse.get(c)) {
            int before = c.costForTurn;
            int restore = TaxTrackFields.preTaxCostForTurn.get(c);
            c.costForTurn = restore;
            c.isCostModifiedForTurn = (c.costForTurn != c.cost);
            TaxTrackFields.taxedByGrandMageCurse.set(c, false);
            if (before != c.costForTurn) {
                BasicMod.logger.info("【诅咒税-恢复显示】牌={}({}) costForTurn {} -> {}，baseCost={}。",
                        c.name, c.cardID, before, c.costForTurn, c.cost);
            }
        }
    }

    private static int getTaxAmount(AbstractCard c) {
        if (c.type == AbstractCard.CardType.ATTACK) {
            AbstractPower p = AbstractDungeon.player.getPower(GrandMageCurseTaxAttackPower.POWER_ID);
            return p == null ? 0 : Math.max(1, p.amount);
        }
        if (c.type == AbstractCard.CardType.SKILL) {
            AbstractPower p = AbstractDungeon.player.getPower(GrandMageCurseTaxSkillPower.POWER_ID);
            return p == null ? 0 : Math.max(1, p.amount);
        }
        return 0;
    }
}
