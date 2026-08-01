package basicmod.patches;

import basicmod.powers.PossessionNoAttackPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 夺舍禁止攻击补丁。
 * 玩家拥有夺舍限制能力时，本回合不能再打出攻击牌。
 */
@SpirePatch(clz = AbstractCard.class, method = "canUse")
public class PossessionNoAttackPatch {
    /**
     * 后置修改 canUse 结果。
     */
    @SpirePostfixPatch
    public static boolean Postfix(boolean __result, AbstractCard __instance, AbstractPlayer p, AbstractMonster m) {
        if (!__result || __instance == null || p == null) {
            return __result;
        }
        if (__instance.type == AbstractCard.CardType.ATTACK && p.hasPower(PossessionNoAttackPower.POWER_ID)) {
            PowerStrings strings = CardCrawlGame.languagePack.getPowerStrings(PossessionNoAttackPower.POWER_ID);
            if (strings != null && strings.DESCRIPTIONS != null && strings.DESCRIPTIONS.length > 1) {
                __instance.cantUseMessage = strings.DESCRIPTIONS[1];
            }
            return false;
        }
        return __result;
    }
}
