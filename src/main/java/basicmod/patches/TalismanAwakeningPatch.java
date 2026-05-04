package basicmod.patches;

import basicmod.helpers.TalismanAwakeningHelper;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;

@SpirePatch(clz = AbstractRelic.class, method = "onEquip")
public class TalismanAwakeningPatch {
    @SpirePostfixPatch
    public static void Postfix(AbstractRelic __instance) {
        // 任何遗物装备时（包括原版遗物），都进行“集齐12符咒”检测
        TalismanAwakeningHelper.checkAndTriggerAwakening();
    }
}
