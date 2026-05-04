package basicmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DragonTalismanAction extends AbstractGameAction {
    private final int[] damage;
    private final DamageInfo.DamageType damageType;
    private final AbstractPlayer p;

    public DragonTalismanAction(AbstractPlayer p, int[] damage, DamageInfo.DamageType damageType) {
        this.p = p;
        this.damage = damage;
        this.damageType = damageType;
        this.actionType = ActionType.DAMAGE;
        this.duration = Settings.ACTION_DUR_FAST;
    }

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
