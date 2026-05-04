package basicmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import basicmod.relics.RoosterTalisman;

public class RoosterMapPatch {
    
    // 鸡符咒：凌空虚渡，无视路径选择节点
    @SpirePatch(clz = MapRoomNode.class, method = "isConnectedTo")
    public static class IgnorePathPatch {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> Prefix(MapRoomNode __instance, MapRoomNode node) {
            // 如果玩家拥有鸡符咒
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(RoosterTalisman.ID)) {
                // 如果目标节点在当前节点的下一层
                if (node.y == __instance.y + 1) {
                    // 只要在下一层，统统视为“已连接”，实现横向和跨行无缝腾飞
                    return SpireReturn.Return(true);
                }
            }
            return SpireReturn.Continue();
        }
    }
}
