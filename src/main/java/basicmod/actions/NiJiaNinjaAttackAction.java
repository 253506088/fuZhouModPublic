package basicmod.actions;

import basicmod.helpers.NiJiaSupportHelper;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 尼嘉-忍者团攻击动作。
 * 直接记录伤害前后的生命与格挡变化，给黑影护卫、烟雾弹等辅助能力使用。
 */
public class NiJiaNinjaAttackAction extends AbstractGameAction {
    private final DamageInfo info;
    /** 是否为本次出牌的最后一段攻击，最后一段不再追加收尾等待 */
    private boolean finalSegment = false;

    /**
     * 构造函数。
     *
     * @param target 受击目标
     * @param info 伤害信息
     */
    public NiJiaNinjaAttackAction(AbstractMonster target, DamageInfo info) {
        this.target = target;
        this.info = info;
        this.actionType = ActionType.DAMAGE;
        this.attackEffect = AttackEffect.SLASH_DIAGONAL;
        this.duration = Settings.ACTION_DUR_XFAST;
    }

    /**
     * 标记为本次出牌的最后一段攻击。
     */
    public void markAsFinalSegment() {
        this.finalSegment = true;
    }

    /**
     * 执行伤害，并把真实结算结果交给尼嘉辅助工具。
     */
    @Override
    public void update() {
        if (this.target == null || this.target.isDeadOrEscaped() || this.info == null) {
            this.isDone = true;
            return;
        }

        AbstractMonster monster = (AbstractMonster) this.target;
        int hpBefore = monster.currentHealth;
        int blockBefore = monster.currentBlock;
        monster.damage(this.info);
        // 关键点：最后一段攻击不再补收尾等待，缩短多段结算的整体时长；段与段之间的节奏保持不变。
        if (!this.finalSegment && (Settings.FAST_MODE || Settings.ACTION_DUR_XFAST == this.duration) && !AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
            addToTop(new WaitAction(0.1F));
        }

        int hpLoss = Math.max(0, hpBefore - monster.currentHealth);
        int blockedDamage = Math.max(0, blockBefore - monster.currentBlock);
        NiJiaSupportHelper.onNiJiaDamageResolved(monster, hpLoss, blockedDamage, true);
        this.isDone = true;
    }
}
