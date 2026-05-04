package basicmod.patches;

import basicmod.helpers.GrandMageDadCycleCurseHelper;
import basicmod.monsters.GrandMageDad;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.DiscardAtEndOfTurnAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class GrandMageDadCycleCursePatch {
    @SpirePatch(clz = AbstractCard.class, method = "applyPowers")
    public static class ApplyPowersPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractCard __instance) {
            GrandMageDadCycleCurseHelper.ensureCollapsed(__instance);
        }

        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance) {
            applyDecay(__instance);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "calculateCardDamage")
    public static class CalculateCardDamagePatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractCard __instance, AbstractMonster mo) {
            GrandMageDadCycleCurseHelper.ensureCollapsed(__instance);
        }

        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance, AbstractMonster mo) {
            applyDecay(__instance);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerWhenDrawn")
    public static class TriggerWhenDrawnPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance) {
            GrandMageDadCycleCurseHelper.ensureCollapsed(__instance);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "canUse")
    public static class CanUsePatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractCard __instance, com.megacrit.cardcrawl.characters.AbstractPlayer p, AbstractMonster m) {
            GrandMageDadCycleCurseHelper.ensureCollapsed(__instance);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "useCard")
    public static class UseCardPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance, AbstractCard card, AbstractMonster monster, int energyOnUse) {
            GrandMageDadCycleCurseHelper.onPlayerCardUseFinished(card);
        }
    }

    @SpirePatch(clz = DiscardAtEndOfTurnAction.class, method = "update")
    public static class DiscardAtEndOfTurnPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(DiscardAtEndOfTurnAction __instance) {
            if (!GrandMageDadCycleCurseHelper.isStageExhaustHandActive()) {
                return SpireReturn.Continue();
            }
            GrandMageDadCycleCurseHelper.exhaustRemainingHandAtEndOfTurn();
            __instance.isDone = true;
            return SpireReturn.Return(null);
        }

        @SpirePostfixPatch
        public static void Postfix(DiscardAtEndOfTurnAction __instance) {
            if (__instance.isDone && GrandMageDadCycleCurseHelper.hasActiveCyclePower()) {
                GrandMageDadCycleCurseHelper.endPlayerCurseTurn();
            }
            if (__instance.isDone) {
                GrandMageDad.onPlayerEndTurnBorrowCleanup();
            }
        }
    }

    private static void applyDecay(AbstractCard card) {
        int decay = GrandMageDadCycleCurseHelper.getCurrentDecayAmount();
        if (card == null || decay <= 0) {
            return;
        }

        if (card.baseDamage >= 0) {
            int before = card.damage;
            card.damage = Math.max(0, card.damage - decay);
            if (card.multiDamage != null) {
                for (int i = 0; i < card.multiDamage.length; i++) {
                    card.multiDamage[i] = Math.max(0, card.multiDamage[i] - decay);
                }
            }
            if (before != card.damage || card.damage != card.baseDamage) {
                card.isDamageModified = true;
            }
        }

        if (card.baseBlock >= 0) {
            int before = card.block;
            card.block = Math.max(0, card.block - decay);
            if (before != card.block || card.block != card.baseBlock) {
                card.isBlockModified = true;
            }
        }
    }
}
