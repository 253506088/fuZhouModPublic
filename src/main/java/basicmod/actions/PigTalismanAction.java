package basicmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

/**
 * 亥猪符咒动作。
 * 移除目标身上的指定正面Buff（白名单模式）。
 */
public class PigTalismanAction extends AbstractGameAction {
    /** 目标生物 */
    private final AbstractCreature target;

    /**
     * 构造函数。
     *
     * @param target 要移除Buff的目标生物
     */
    public PigTalismanAction(AbstractCreature target) {
        this.target = target;
        this.actionType = ActionType.SPECIAL;
    }

    /**
     * 执行动作逻辑：移除目标身上的白名单中的正面Buff。
     */
    @Override
    public void update() {
        if (target != null && !target.isDeadOrEscaped()) {
            // 新版本：白名单模式，仅允许移除指定的正面 Buff
            // 包含：力量(Strength)、人工制品(Artifact)、荆棘(Thorns)、飞行(Flight)、无实体(Intangible/IntangibleMonster)、金属化(Metallicize)
            java.util.List<String> whitelist = java.util.Arrays.asList(
                    "Strength", "Artifact", "Thorns", "Flight", "Intangible", "IntangibleMonster", "Metallicize"
            );

            java.util.Iterator<AbstractPower> iter = target.powers.iterator();
            while (iter.hasNext()) {
                AbstractPower p = iter.next();
                // 只有当该状态是 BUFF 且在白名单中时，才允许移除
                if (p.type == AbstractPower.PowerType.BUFF && whitelist.contains(p.ID)) {
                    p.onRemove();
                    iter.remove();
                }
            }
        }
        this.isDone = true;
    }

    /**
     * 旧版本备份，旧版本的亥猪是移除全部的正面Buff
     */
    public void update_bak() {
        if(true){
            return;
        }
        if (target != null && !target.isDeadOrEscaped()) {
            // 使用迭代器安全移除目标身上的所有正面 Buff，保留负面 Debuff
            java.util.Iterator<AbstractPower> iter = target.powers.iterator();
            while (iter.hasNext()) {
                AbstractPower p = iter.next();
                if (p.type == AbstractPower.PowerType.BUFF) {
                    p.onRemove();
                    iter.remove();
                }
            }
        }
        this.isDone = true;
    }
}
