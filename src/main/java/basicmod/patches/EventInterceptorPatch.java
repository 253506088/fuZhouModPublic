package basicmod.patches;

import basicmod.events.DragoEvent;
import basicmod.events.PanKuBoxEvent;
import basicmod.events.RobTalismanEvent;
import basicmod.helpers.TalismanHelper;
import basicmod.relics.CollaborationRelic;
import basicmod.relics.PanKuBox;
import basicmod.BasicMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.random.Random;

@SpirePatch(clz = AbstractDungeon.class, method = "generateEvent", paramtypez = { Random.class })
public class EventInterceptorPatch {
    @SpirePrefixPatch
    public static SpireReturn<AbstractEvent> Prefix(Random rng) {
        // --- 老大爷的新要求：非圣主角色不进行任何劫持 ---
        if (AbstractDungeon.player == null || AbstractDungeon.player.chosenClass != basicmod.enums.CharacterEnums.SHENGZHU) {
            return SpireReturn.Continue();
        }

        // --- 强制劫持：恶魔小龙事件 ---
        // 判断条件：圣主角色、第二层或以上，且没有【合作】遗物
        if (AbstractDungeon.player != null && AbstractDungeon.player.chosenClass == basicmod.enums.CharacterEnums.SHENGZHU &&
            AbstractDungeon.actNum > 1 && !AbstractDungeon.player.hasRelic(CollaborationRelic.ID)) {
            BasicMod.logger.info(">>> [事件房内容判定] 触发强制劫持！检测到圣主在第二层以上且无合作遗物，强制返回 DragoEvent。");
            return SpireReturn.Return(new DragoEvent());
        }

        // --- 老大爷的新要求：强制劫持：潘库宝盒事件 ---
        // 判断条件：圣主角色 第二层或以上，且没有【潘库宝盒】遗物
        if (AbstractDungeon.player != null && AbstractDungeon.player.chosenClass == basicmod.enums.CharacterEnums.SHENGZHU &&
                AbstractDungeon.actNum >= 2 && !AbstractDungeon.player.hasRelic(PanKuBox.ID)) {
            if (BasicMod.hasDeclinedPanKuBoxEventThisRun()) {
                BasicMod.logger.info(">>> [事件房内容判定] 检测到玩家此前已在【远古的封印】中选择离开，本次跳过二层保底强制触发。");
                return SpireReturn.Continue();
            }
            BasicMod.logger.info(">>> [事件房内容判定] 触发强制劫持！检测到圣主在第二层及以上且无潘库宝盒，强制返回 PanKuBoxEvent。");
            return SpireReturn.Return(new PanKuBoxEvent());
        }

        // --- 概率劫持：抢夺符咒事件 ---
        // 只有圣主角色才会参与劫持判定
        if (AbstractDungeon.player != null && AbstractDungeon.player.chosenClass == basicmod.enums.CharacterEnums.SHENGZHU) {
            int count = TalismanHelper.getOwnedTalismanCount();

            // 老大爷，按您的新要求：每符咒 3% 概率
            float chance = count * 0.03f;
            boolean roll = chance > 0 && rng.randomBoolean(chance);

            BasicMod.logger.info(">>> [事件房内容判定] 开始! 符咒数: " + count + ", 劫持概率: " + (chance * 100) + "%, 结果: " + roll);

            if (roll && count > 0) {
                BasicMod.logger.info(">>> [事件房内容判定] 劫持成功！强制返回 RobTalismanEvent。");
                return SpireReturn.Return(new RobTalismanEvent());
            }
        }

        // 没中或者没符咒，就走原版的抽签流程
        return SpireReturn.Continue();
    }
}
