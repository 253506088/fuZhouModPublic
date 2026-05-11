package basicmod.helpers;

import basicmod.BasicMod;
import basicmod.powers.GrandMageBlessingPower;
import basicmod.powers.MoonUncapPower;
import com.megacrit.cardcrawl.core.AbstractCreature;

/**
 * 恶魔异常伤害结算帮助类：
 * 统一处理“异常自身伤害上限”与“大法师的庇佑伤害上限”。
 */
public final class DemonQiDamageHelper {
    private DemonQiDamageHelper() {}

    /**
     * 判断本次恶魔异常伤害是否应该跳过全部伤害上限。
     *
     * @param source 伤害来源，一般为施加异常的玩家或触发额外结算的能力拥有者
     * @return true 表示本次伤害应当无视所有单次伤害上限
     */
    public static boolean shouldIgnoreDamageCap(AbstractCreature source) {
        return source != null && source.hasPower(MoonUncapPower.POWER_ID);
    }

    /**
     * 按统一规则结算恶魔异常伤害。
     * 规则顺序：
     * 1. 若来源带有【解除限制】，直接返回原始伤害。
     * 2. 否则先套恶魔异常自己的单次伤害上限。
     * 3. 再套【大法师的庇佑】的单次伤害上限。
     *
     * @param source 伤害来源
     * @param target 伤害目标
     * @param powerId 伤害对应的异常能力 ID
     * @param damageAmount 原始伤害
     * @param selfCap 异常自己定义的单次伤害上限，若 <= 0 视为无此层限制
     * @param context 日志场景
     * @return 处理后的最终伤害
     */
    public static int applyDemonQiDamageCaps(AbstractCreature source,
                                             AbstractCreature target,
                                             String powerId,
                                             int damageAmount,
                                             int selfCap,
                                             String context) {
        if (damageAmount <= 0) {
            return damageAmount;
        }

        if (shouldIgnoreDamageCap(source)) {
            BasicMod.logger.info("【恶魔异常伤害】检测到【解除限制】，跳过全部伤害上限。来源="
                    + powerId + "，场景=" + context + "，伤害=" + damageAmount + "。");
            return damageAmount;
        }

        int adjusted = damageAmount;
        if (selfCap > 0) {
            int selfCapped = Math.min(adjusted, selfCap);
            if (selfCapped != adjusted) {
                BasicMod.logger.info("【恶魔异常伤害】触发异常自身上限。来源="
                        + powerId + "，场景=" + context + "，原始伤害=" + adjusted
                        + "，自身封顶后=" + selfCapped + "。");
            }
            adjusted = selfCapped;
        }

        return GrandMageBlessingPower.capDemonQiDamage(target, source, powerId, adjusted, context);
    }
}
