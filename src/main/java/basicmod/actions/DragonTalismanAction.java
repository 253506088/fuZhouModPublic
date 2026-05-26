package basicmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 龙符咒动作。
 * 对所有敌人造成伤害，如果击杀敌人则触发连锁效果再次攻击。
 */
public class DragonTalismanAction extends AbstractGameAction {
    /** 伤害值数组 */
    private final int[] damage;
    /** 伤害类型 */
    private final DamageInfo.DamageType damageType;
    /** 玩家对象 */
    private final AbstractPlayer p;

    /**
     * 构造函数。
     *
     * @param p 玩家对象
     * @param damage 伤害值数组
     * @param damageType 伤害类型
     */
    public DragonTalismanAction(AbstractPlayer p, int[] damage, DamageInfo.DamageType damageType) {
        this.p = p;
        this.damage = damage;
        this.damageType = damageType;
        this.actionType = ActionType.DAMAGE;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    /**
     * 执行动作逻辑：对所有敌人造成伤害，击杀敌人后触发连锁效果。
     */
    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            // Check if there are enemies alive
            boolean anyAlive = false;
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!m.isDead && !m.isDying) {
                    anyAlive = true;
                    break;
                }
            }

            if (!anyAlive) {
                this.isDone = true;
                return;
            }

            // Record current health of enemies before damage
            int aliveCountBefore = getAliveCount();

            // Logic to check kill: because we use addToTop which is LIFO, we add the check FIRST so it executes SECOND.
            addToTop(new AbstractGameAction() {
                @Override
                public void update() {
                    int aliveCountAfter = getAliveCount();
                    if (aliveCountAfter < aliveCountBefore) {
                        // Kill occurred! Trigger Dragon again.
                        addToTop(new DragonTalismanAction(p, damage, damageType));
                    }
                    this.isDone = true;
                }
            });

            // Add damage action LAST so it executes FIRST.
            addToTop(new DamageAllEnemiesAction(p, damage, damageType, AttackEffect.FIRE));
        }
        this.tickDuration();
    }

    /**
     * 获取存活怪物数量。
     *
     * @return 存活怪物数量
     */
    private int getAliveCount() {
        int count = 0;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDead && !m.isDying) {
                count++;
            }
        }
        return count;
    }
}
