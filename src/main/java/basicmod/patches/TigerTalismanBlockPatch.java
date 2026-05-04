package basicmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import basicmod.relics.TigerTalisman;

public class TigerTalismanBlockPatch {
    
    // 阳平衡：当力量高于敏捷时，叠甲瞬间补齐差值
    @SpirePatch(clz = AbstractCreature.class, method = "addBlock")
    public static class AddBlockStrengthBonusPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractCreature __instance, @ByRef int[] blockAmount) {
            // 如果是玩家获得格挡，且拥有虎符咒
            if (__instance instanceof AbstractPlayer && AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(TigerTalisman.ID)) {
                int str = 0;
                if (AbstractDungeon.player.hasPower(StrengthPower.POWER_ID)) {
                    str = AbstractDungeon.player.getPower(StrengthPower.POWER_ID).amount;
                }
                
                int dex = 0;
                if (AbstractDungeon.player.hasPower(DexterityPower.POWER_ID)) {
                    dex = AbstractDungeon.player.getPower(DexterityPower.POWER_ID).amount;
                }
                
                // 敏借力威：如果力量更高，补齐差值
                if (str > dex) {
                    blockAmount[0] += (str - dex);
                }
            }
        }
    }
}
