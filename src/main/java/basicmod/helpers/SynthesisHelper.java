package basicmod.helpers;

import basicmod.BasicMod;
import basicmod.powers.BurningPower;
import basicmod.powers.SoakedPower;
import basicmod.powers.SteamPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class SynthesisHelper {
    public static void checkSteam(AbstractCreature target, AbstractCreature source, com.megacrit.cardcrawl.powers.AbstractPower newlyAppliedPower) {
        int burnAmount = 0;
        int soakedAmount = 0;
        
        // 强力检查：使用杀戮尖塔标准的 ID 获取法，弃用 instanceof 遍历
        com.megacrit.cardcrawl.powers.AbstractPower pFire = target.getPower(BurningPower.POWER_ID);
        if (pFire != null) {
            burnAmount = pFire.amount;
        }

        com.megacrit.cardcrawl.powers.AbstractPower pWater = target.getPower(SoakedPower.POWER_ID);
        if (pWater != null) {
            soakedAmount = pWater.amount;
        }

        // 最强兜底：BaseMod 的 PostPowerApply 钩子经常在实体附加到集合【之前】触发。
        // 如果 target.getPower 是 null，说明是第一次赋予该状态，直接从 newlyAppliedPower 取层数。
        if (newlyAppliedPower != null) {
            if (BurningPower.POWER_ID.equals(newlyAppliedPower.ID)) {
                if (pFire == null) {
                    burnAmount = newlyAppliedPower.amount;
                }
            } else if (SoakedPower.POWER_ID.equals(newlyAppliedPower.ID)) {
                if (pWater == null) {
                    soakedAmount = newlyAppliedPower.amount;
                }
            }
        }

        // 计算最小值合成 H (木桶效应)
        int h = Math.min(burnAmount, soakedAmount);
        
        if (burnAmount > 0 || soakedAmount > 0) {
            BasicMod.logger.info("【高温蒸汽合成检测】目标: " + target.name + " | 探测到火: " + burnAmount + " | 探测到水: " + soakedAmount);
        }

        if (h > 0) {
            BasicMod.logger.info("【高温蒸汽合成检测】条件达成！正在执行合成: " + h + " 层蒸汽");
            // 扣除两种状态
            AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(target, source, BurningPower.POWER_ID, h));
            AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(target, source, SoakedPower.POWER_ID, h));
            
            // 合成高温蒸汽
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(target, source, new SteamPower(target, source, h), h));
        } else if (burnAmount > 0 || soakedAmount > 0) {
            BasicMod.logger.info("【高温蒸汽合成检测】条件不足，无法合成。");
        }
    }
}
