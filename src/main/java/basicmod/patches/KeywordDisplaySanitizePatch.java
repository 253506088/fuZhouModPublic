package basicmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DescriptionLine;

/**
 * 关键词显示清理补丁：
 * 内部仍使用 *fuZhouMod:关键词 触发自定义关键词悬浮，
 * 但卡面上不展示关键词前缀，保持玩家可读性。
 * 注意：不能无差别清理 fuZhouMod:，否则会误伤 !fuZhouMod:变量名! 这类动态变量占位符。
 */
@SpirePatch(clz = AbstractCard.class, method = "initializeDescription")
public class KeywordDisplaySanitizePatch {
    @SpirePostfixPatch
    public static void Postfix(AbstractCard __instance) {
        if (__instance == null || __instance.description == null) {
            return;
        }
        for (DescriptionLine line : __instance.description) {
            if (line != null && line.text != null && line.text.contains("fuZhouMod:")) {
                line.text = line.text.replace("*fuZhouMod:", "*");
            }
        }
    }
}

