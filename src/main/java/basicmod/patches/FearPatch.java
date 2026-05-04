package basicmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import basicmod.powers.FearPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 恐惧补丁：拦截怪物的意图显示。
 * 通过拦截 rollMove，我们让怪物的意图永远显示为防御。
 */
public class FearPatch {
    private static final Logger logger = LogManager.getLogger(FearPatch.class.getName());
    // 增加一个静态标志位，用于在补丁恢复意图时跳过过滤逻辑，防止死循环/连续发呆
    public static boolean isBypassing = false;

    // 补丁 1：拦截 rollMove，确保怪物在被恐惧期间显示防御意图
    @SpirePatch(clz = AbstractMonster.class, method = "rollMove")
    public static class FearRollMovePatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AbstractMonster __instance) {
            // 如果正处于恢复过程中，直接跳过拦截，让怪物正常摇号
            if (isBypassing) return SpireReturn.Continue();
            
            if (__instance.hasPower(FearPower.POWER_ID)) {
                logger.info("恐惧生效中：强制怪物 " + __instance.name + " 的意图显示为防御。");
                
                // 设为一个相对中性的防御意图，字节码设为 99 （通常比 -127 安全）
                // 配合下方的 takeTurn 拦截，这个字节码其实不会被实际执行到。
                __instance.setMove((byte)99, AbstractMonster.Intent.DEFEND);
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    // 补丁 2：核心拦截！拦截动作管理器的执行。
    // 在这里拦截可以完美避开 AbstractMonster.takeTurn 是抽象方法导致的子类拦截失败问题。
    @SpirePatch(clz = com.megacrit.cardcrawl.actions.GameActionManager.class, method = "getNextAction")
    public static class FearSkipTurnPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(com.megacrit.cardcrawl.actions.GameActionManager __instance) {
            // 安全检查：确保当前处于战斗房间，且怪物列表非空
            if (com.megacrit.cardcrawl.dungeons.AbstractDungeon.getMonsters() == null || 
                com.megacrit.cardcrawl.dungeons.AbstractDungeon.getMonsters().areMonstersBasicallyDead() || 
                __instance.monsterQueue.isEmpty()) {
                return SpireReturn.Continue();
            }

            com.megacrit.cardcrawl.monsters.MonsterQueueItem item = __instance.monsterQueue.get(0);
            AbstractMonster m = item.monster;
            
            if (m != null && m.hasPower(FearPower.POWER_ID)) {
                logger.info("恐惧拦截成功：跳过怪物 " + m.name + " 的动作，并为其预备下回合意图。");
                
                // 1. 弹出动作队列，代表本回合动作已“完成”
                __instance.monsterQueue.remove(0);
                
                // 2. 核心补救：手动调用摇号和更新意图。此时设置标志位，防止被自身的补丁拦截成 99
                isBypassing = true;
                m.rollMove();
                m.createIntent();
                isBypassing = false;
                
                // 3. 移除恐惧，确保下回合不再拦截（除非再次被施加）
                com.megacrit.cardcrawl.dungeons.AbstractDungeon.actionManager.addToTop(
                    new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(m, m, FearPower.POWER_ID)
                );
                
                // 4. 返回 Return，告诉 GameActionManager 跳过它自己原本的 getNextAction 逻辑
                return SpireReturn.Return(null);
            }
            
            return com.evacipated.cardcrawl.modthespire.lib.SpireReturn.Continue();
        }
    }
}
