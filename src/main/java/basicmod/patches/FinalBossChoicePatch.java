package basicmod.patches;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.helpers.FinalBossChoiceManager;
import basicmod.monsters.GrandMageDad;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;

public class FinalBossChoicePatch {
    @SpirePatch(clz = MonsterRoomBoss.class, method = "onPlayerEntry")
    public static class ReplaceHeartWithGrandMageDadPatch {
        @SpirePostfixPatch
        public static void Postfix(MonsterRoomBoss __instance) {
            if (!"TheEnding".equals(AbstractDungeon.id)) {
                return;
            }
            if (AbstractDungeon.player == null || AbstractDungeon.player.chosenClass != CharacterEnums.SHENGZHU) {
                return;
            }
            if (__instance.monsters == null || __instance.monsters.monsters == null || __instance.monsters.monsters.isEmpty()) {
                return;
            }
            boolean hasHeart = __instance.monsters.monsters.stream()
                    .anyMatch(m -> m != null && "CorruptHeart".equals(m.id));
            if (!hasHeart) {
                return;
            }
            if (!FinalBossChoiceManager.shouldUseGrandMageDad()) {
                return;
            }

            BasicMod.logger.info("[最终Boss选项补丁] 用大法师父亲替换腐化之心");
            __instance.monsters = new MonsterGroup(new GrandMageDad());
        }
    }
}
