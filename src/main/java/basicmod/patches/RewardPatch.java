package basicmod.patches;
 
import basicmod.enums.CustomTags;
import basicmod.relics.CollaborationRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
 
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
 
@SpirePatch(clz = AbstractDungeon.class, method = "getRewardCards")
public class RewardPatch {
    // 战斗胜利结算时获取奖励卡牌列表
    public static ArrayList<AbstractCard> Postfix(ArrayList<AbstractCard> __result) {
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(CollaborationRelic.ID)) {
            // 如果玩家持有合作遗物，且结果列表存在
            if (__result != null) {
                // 获取所有带有 TEAM_JACKIE 标签的卡牌原型
                List<AbstractCard> jackieCards = CardLibrary.getAllCards().stream()
                        .filter(c -> c.hasTag(CustomTags.TEAM_JACKIE))
                        .filter(c -> !c.name.contains("MISSING_TITLE") && !c.cardID.contains("JackieCard")) // 双重保险：排除幽灵卡
                        .collect(Collectors.toList());
                
                if (!jackieCards.isEmpty()) {
                    // 打印当前池子里都有哪些卡
                    basicmod.BasicMod.logger.info("【TEAM_JACKIE 奖励池】可用卡牌总数: " + jackieCards.size());
                    for (AbstractCard c : jackieCards) {
                        basicmod.BasicMod.logger.info("【TEAM_JACKIE 奖励池】候选卡牌: " + c.cardID);
                    }

                    List<AbstractCard> drawPool = new ArrayList<>(jackieCards);
                    for (int i = 0; i < 2; i++) {
                        if (drawPool.isEmpty()) {
                            drawPool = new ArrayList<>(jackieCards);
                        }
                        // 使用游戏原生的 cardRng 随机选一个索引，确保真正的随机性
                        int index = AbstractDungeon.cardRng.random(drawPool.size() - 1);
                        basicmod.BasicMod.logger.info("【TEAM_JACKIE 奖励池】第" + (i + 1) + "张抽取的随机索引: " + index);
                        
                        AbstractCard specialCard = drawPool.get(index).makeCopy();
                        // 合作牌是在原版奖励预览流程之后追加的，这里补跑遗物预览逻辑，确保毒素蛋等遗物能正确显示升级版。
                        for (AbstractRelic relic : AbstractDungeon.player.relics) {
                            relic.onPreviewObtainCard(specialCard);
                        }
                        basicmod.BasicMod.logger.info("【TEAM_JACKIE 奖励池】第" + (i + 1) + "张最终生成卡牌: " + specialCard.cardID);
                        
                        // 塞进结果列表的末尾，作为额外的奖励卡
                        __result.add(specialCard);
                        drawPool.remove(index);
                    }
                }
            }
        }
        return __result;
    }
}
