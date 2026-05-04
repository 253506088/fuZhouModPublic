package basicmod.helpers;

import basicmod.relics.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.helpers.RelicLibrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TalismanHelper {
    public static final List<String> ALL_TALISMAN_IDS = Arrays.asList(
            RatTalisman.ID,
            OxTalisman.ID,
            TigerTalisman.ID,
            RabbitTalisman.ID,
            DragonTalisman.ID,
            SnakeTalisman.ID,
            HorseTalisman.ID,
            SheepTalisman.ID,
            MonkeyTalisman.ID,
            RoosterTalisman.ID,
            DogTalisman.ID,
            PigTalisman.ID
    );

    /**
     * 获取一个玩家未拥有的随机符咒遗物
     * @return 随机未拥有符咒遗物实例，若全部拥有则返回null
     */
    public static AbstractRelic getRandomMissingTalisman() {
        if (AbstractDungeon.player == null) return null;

        ArrayList<String> missing = new ArrayList<>();
        for (String id : ALL_TALISMAN_IDS) {
            if (!AbstractDungeon.player.hasRelic(id)) {
                missing.add(id);
            }
        }

        if (missing.isEmpty()) {
            return null;
        }

        String chosenId = missing.get(AbstractDungeon.relicRng.random(missing.size() - 1));
        // 从原生遗物池中彻底剔除，防止在其他途径再次掉落
        removeTalismanFromPools(chosenId);
        return RelicLibrary.getRelic(chosenId).makeCopy();
    }

    /**
     * 将指定的符咒从各个原生掉落池中移除，防止在商店或下一个奖励窗口中重复掉落
     */
    public static void removeTalismanFromPools(String relicId) {
        if (AbstractDungeon.commonRelicPool != null) AbstractDungeon.commonRelicPool.remove(relicId);
        if (AbstractDungeon.uncommonRelicPool != null) AbstractDungeon.uncommonRelicPool.remove(relicId);
        if (AbstractDungeon.rareRelicPool != null) AbstractDungeon.rareRelicPool.remove(relicId);
        if (AbstractDungeon.bossRelicPool != null) AbstractDungeon.bossRelicPool.remove(relicId);
        if (AbstractDungeon.shopRelicPool != null) AbstractDungeon.shopRelicPool.remove(relicId);
    }

    /**
     * 获取玩家当前拥有的符咒总数
     */
    public static int getOwnedTalismanCount() {
        if (AbstractDungeon.player == null) return 0;
        int count = 0;
        for (String id : ALL_TALISMAN_IDS) {
            if (AbstractDungeon.player.hasRelic(id)) {
                count++;
            }
        }
        return count;
    }
}
