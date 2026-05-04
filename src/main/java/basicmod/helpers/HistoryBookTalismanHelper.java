package basicmod.helpers;

import basicmod.effects.RelicExhaustEffect;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

/**
 * 岁月史书符咒奖励工具。
 * 负责剥夺已有符咒、补偿最大生命值，并创建十二符咒奖励副本。
 */
public class HistoryBookTalismanHelper {
    /**
     * 剥夺玩家当前已有的符咒遗物，并按数量增加最大生命值。
     */
    public static int removeOwnedTalismansAndGainMaxHp(int maxHpPerRelic) {
        if (AbstractDungeon.player == null) {
            return 0;
        }

        int removed = 0;
        for (String relicId : TalismanHelper.ALL_TALISMAN_IDS) {
            AbstractRelic relic = AbstractDungeon.player.getRelic(relicId);
            if (relic != null) {
                AbstractDungeon.topLevelEffects.add(new RelicExhaustEffect(relic));
                relic.onUnequip();
                AbstractDungeon.player.relics.remove(relic);
                AbstractDungeon.player.reorganizeRelics();
                AbstractDungeon.player.increaseMaxHp(maxHpPerRelic, true);
                removed++;
            }
        }
        return removed;
    }

    /**
     * 创建十二符咒遗物副本，供岁月史书动画依次发放。
     */
    public static ArrayList<AbstractRelic> makeAllTalismanCopies() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        for (String relicId : TalismanHelper.ALL_TALISMAN_IDS) {
            AbstractRelic template = RelicLibrary.getRelic(relicId);
            if (template != null) {
                relics.add(template.makeCopy());
                TalismanHelper.removeTalismanFromPools(relicId);
            }
        }
        return relics;
    }
}
