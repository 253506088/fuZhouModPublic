package basicmod.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.map.MapRoomNode;

@SpirePatch(clz = MapRoomNode.class, method = SpirePatch.CLASS)
public class MapNodeDemonPortalField {
    public static SpireField<Boolean> isDemonPortal = new SpireField<>(() -> false);
}
