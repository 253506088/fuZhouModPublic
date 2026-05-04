package basicmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

/**
 * 能量保留补丁：
 * 使用“方案A”（全面复刻原版逻辑）。
 * 之前由于使用“冰激凌替身”策略（方案B），导致引擎以为玩家有冰激凌后，
 * 去索要带有 "Ice Cream" ID的遗物并试图播放动画时由于找不到抛出了 NullPointerException。
 * 现在完全改为在这里面正正规规地完成保留和特效逻辑。
 */
@SpirePatch(clz = EnergyManager.class, method = "recharge")
public class EnergyRetentionPatch {
    
    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(EnergyManager __instance) {
        // 游戏尚未完全初始化时防止空指针
        if (AbstractDungeon.player == null) {
            return SpireReturn.Continue();
        }
        
        // 如果玩家已经拥有了原版的冰淇淋，让原版核心逻辑处理即可，防止冲突。
        if (AbstractDungeon.player.hasRelic("Ice Cream")) {
            return SpireReturn.Continue();
        }

        // 如果没有冰淇淋，但有羊符咒
        if (AbstractDungeon.player.hasRelic("fuZhouMod:SheepTalisman")) {
            // 模仿冰淇淋的特效：如果上一回合真的余下了能量，闪烁羊符咒并播放头顶文字
            if (com.megacrit.cardcrawl.ui.panels.EnergyPanel.totalCount > 0) {
                AbstractDungeon.player.getRelic("fuZhouMod:SheepTalisman").flash();
                AbstractDungeon.actionManager.addToTop(
                    new com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction(
                        AbstractDungeon.player, 
                        AbstractDungeon.player.getRelic("fuZhouMod:SheepTalisman")
                    )
                );
            }
            
            // 核心：在保留面板的基础上，稳健地用原版的能量添加方法增加本回合的最大能量
            com.megacrit.cardcrawl.ui.panels.EnergyPanel.addEnergy(__instance.energyMaster);
            // 同步 ActionManager 使 UI 和内部监听正确识别
            AbstractDungeon.actionManager.updateEnergyGain(__instance.energyMaster);
            
            // 必须返回 Return，拦截掉原版本来会将能量强行归置的 fallback 逻辑
            return SpireReturn.Return();
        }

        return SpireReturn.Continue();
    }
}
