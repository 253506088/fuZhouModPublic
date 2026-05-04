package basicmod.patches;

import basicmod.monsters.GrandMageDad;
import basicmod.powers.FearPower;
import basicmod.powers.GrandMageBlessingPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.lang.reflect.Field;

public class GrandMageDadIntentPatch {
    private static final int HEAVEN_DEMON_FEAR_DAMAGE = 8;

    private static Field powerToApplyField;
    private static boolean powerFieldInitialized = false;

    @SpirePatch(clz = ApplyPowerAction.class, method = "update")
    public static class BlessingNegateDebuffPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(ApplyPowerAction __instance) {
            AbstractCreature target = __instance.target;
            AbstractCreature source = __instance.source;
            AbstractPower toApply = getPowerToApply(__instance);
            if (target == null || toApply == null) {
                return SpireReturn.Continue();
            }
            if (!target.hasPower(GrandMageBlessingPower.POWER_ID)) {
                return SpireReturn.Continue();
            }
            if (source == target) {
                return SpireReturn.Continue();
            }

            if (shouldConvertFearToDamage(target, toApply)) {
                GrandMageDad.logDetail("【大法师的庇佑】改判恐惧成功：来源="
                        + describeCreature(source)
                        + "，目标=" + describeCreature(target)
                        + "，原效果=恐惧，改为额外伤害=" + HEAVEN_DEMON_FEAR_DAMAGE + "。");
                AbstractDungeon.actionManager.addToTop(new LoseHPAction(target, source, HEAVEN_DEMON_FEAR_DAMAGE));
                __instance.isDone = true;
                return SpireReturn.Return(null);
            }

            if (toApply.type != AbstractPower.PowerType.DEBUFF) {
                return SpireReturn.Continue();
            }
            AbstractPower p = target.getPower(GrandMageBlessingPower.POWER_ID);
            if (p instanceof GrandMageBlessingPower) {
                GrandMageBlessingPower blessing = (GrandMageBlessingPower) p;
                if (blessing.canBlockDebuff()) {
                    GrandMageDad.logDetail("【大法师的庇佑】拦截异常成功：来源="
                            + describeCreature(source)
                            + "，目标=" + describeCreature(target)
                            + "，异常=" + describePower(toApply) + "。");
                    blessing.consumeBlock();
                    __instance.isDone = true;
                    return SpireReturn.Return(null);
                }
            }
            return SpireReturn.Continue();
        }
    }

    private static boolean shouldConvertFearToDamage(AbstractCreature target, AbstractPower toApply) {
        return target instanceof GrandMageDad && FearPower.POWER_ID.equals(toApply.ID);
    }

    private static String describeCreature(AbstractCreature creature) {
        if (creature == null) {
            return "未知单位";
        }
        return creature.name + "/" + creature.id;
    }

    private static String describePower(AbstractPower power) {
        if (power == null) {
            return "未知Power";
        }
        return power.name + "/" + power.ID + " x" + power.amount;
    }

    private static AbstractPower getPowerToApply(ApplyPowerAction action) {
        if (!powerFieldInitialized) {
            try {
                powerToApplyField = ApplyPowerAction.class.getDeclaredField("powerToApply");
                powerToApplyField.setAccessible(true);
            } catch (Exception ignored) {
                powerToApplyField = null;
            } finally {
                powerFieldInitialized = true;
            }
        }
        if (powerToApplyField == null) {
            return null;
        }
        try {
            return (AbstractPower) powerToApplyField.get(action);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }
}
