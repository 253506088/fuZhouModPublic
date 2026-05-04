package basicmod.patches;
 
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.cards.AbstractCard;
import basicmod.powers.ShenZhuStatuePower;
import basicmod.relics.OxTalisman;
 
public class ShenZhuMechanicsPatch {
 
    // 1. 护甲额外增加 5 点
    @SpirePatch(clz = AbstractCreature.class, method = "addBlock")
    public static class DoubleBlockPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractCreature __instance, @ByRef int[] blockAmount) {
            // 如果获得护甲的是玩家，而且是圣主，而且身上有“圣主石像”的Power
            // 且：并没有正在执行如“倍增格挡”类的主动卡牌动作（避免重复叠加的问题）
            if (__instance != null && __instance instanceof AbstractPlayer
                    && __instance.hasPower(ShenZhuStatuePower.POWER_ID)
                    && !basicmod.util.MechanicsContext.isProcessingDoubling) {
                if (false) {
                    // 旧逻辑：护甲翻倍
                    blockAmount[0] *= 2;
                } else {
                    // 新逻辑：最终取得的护甲直接 + 4
                    blockAmount[0] += 4;
                }
            }
        }
    }
 
    // 2. 攻击牌费用增加 1 点，并直接显示在卡牌上
    @SpirePatch(clz = AbstractCard.class, method = "applyPowers")
    public static class CardCostApplyPowersPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance) {
            modifyCost(__instance);
            modifyDamage(__instance);
        }
    }
 
    @SpirePatch(clz = AbstractCard.class, method = "calculateCardDamage")
    public static class CardCostCalculateCardDamagePatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance, com.megacrit.cardcrawl.monsters.AbstractMonster mo) {
            modifyCost(__instance);
            modifyDamage(__instance);
        }
    }
 
    private static void modifyCost(AbstractCard c) {
        // 仅对攻击牌生效，且玩家拥有圣主石像，且基础费用大于 0 (0费牌不涨价)
        if (c.type == AbstractCard.CardType.ATTACK && AbstractDungeon.player != null
                && AbstractDungeon.player.hasPower(ShenZhuStatuePower.POWER_ID)) {
            
            // 老大爷要求：石盘形态不再加费，改用 if(false) 屏蔽原逻辑，以备回调
            if (false) {
                // 如果该牌已被设置为本回合 0 费（例如通过组合技），则不应用涨价
                if (c.cost > 0 && !(c.isCostModifiedForTurn && c.costForTurn == 0)) {
                    // 稳健方案：涨价后的费用 = 基础费用 + 1
                    c.costForTurn = c.cost + 1;
                    c.isCostModifiedForTurn = true;
                }
            }
        }
    }

    private static void modifyDamage(AbstractCard c) {
        // 关键：牛符咒翻倍逻辑。在所有力量、遗物加成计算完后的最后一步执行。
        // BUG修复：牛符咒只对单体攻击生效（修复携带猪符咒打出电眼逼人的组合问题）
        if (c.type == AbstractCard.CardType.ATTACK && 
           (c.target == AbstractCard.CardTarget.ENEMY || c.target == AbstractCard.CardTarget.SELF_AND_ENEMY) && 
           OxTalisman.isActivated()) {
            c.damage *= 2;
            if (c.baseDamage > 0 && c.damage != c.baseDamage) {
                c.isDamageModified = true;
            }
        }
    }
}
