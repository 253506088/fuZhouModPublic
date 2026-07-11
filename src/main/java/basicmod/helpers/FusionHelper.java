package basicmod.helpers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

/**
 * 融合卡牌辅助工具。
 * 提供统计玩家当前持有的动物符咒遗物数量、以及判断是否持有指定符咒遗物的方法。
 */
public class FusionHelper {

    /**
     * 统计玩家当前持有的十二生肖动物符咒遗物数量。
     *
     * @return 持有的动物符咒遗物数量（0-12）
     */
    public static int countTalismanRelics() {
        int count = 0;
        if (AbstractDungeon.player != null && AbstractDungeon.player.relics != null) {
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (TalismanHelper.ALL_TALISMAN_IDS.contains(r.relicId)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 判断玩家是否持有指定遗物。
     *
     * @param relicId 遗物 ID
     * @return 是否持有
     */
    public static boolean hasRelic(String relicId) {
        return AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(relicId);
    }
}
