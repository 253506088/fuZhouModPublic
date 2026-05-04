package basicmod.powers;

import basicmod.powers.interfaces.OnBlackHandAppliedPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

import static basicmod.BasicMod.makeID;

public class BountyPower extends BasePower implements OnBlackHandAppliedPower {
    public static final String POWER_ID = makeID("BountyPower");
    private final Set<AbstractMonster> trackedTargets = Collections.newSetFromMap(new IdentityHashMap<>());

    public BountyPower(AbstractCreature owner, int goldReward) {
        super(POWER_ID, PowerType.BUFF, false, owner, owner, 2);
        this.amount2 = Math.max(0, goldReward);
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        if (stackAmount <= 0) {
            return;
        }
        this.fontScale = 8.0F;

        int oldDamage = this.amount;
        int oldGold = this.amount2;

        this.amount += stackAmount;
        int goldPerDamage = oldDamage > 0 ? Math.max(1, oldGold / oldDamage) : 4;
        this.amount2 += goldPerDamage * stackAmount;
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        if (AbstractDungeon.getCurrRoom() == null || AbstractDungeon.getCurrRoom().monsters == null) {
            return;
        }
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped() && m.hasPower(BlackHandPower.POWER_ID)) {
                this.trackedTargets.add(m);
            }
        }
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        if (this.trackedTargets.isEmpty()) {
            return;
        }

        for (Iterator<AbstractMonster> it = this.trackedTargets.iterator(); it.hasNext();) {
            AbstractMonster m = it.next();
            if (m == null) {
                it.remove();
                continue;
            }

            boolean dead = m.currentHealth <= 0 || m.isDying || m.isDead;
            if (dead) {
                if (this.amount2 > 0 && AbstractDungeon.player != null) {
                    flash();
                    AbstractDungeon.player.gainGold(this.amount2);
                    CardCrawlGame.sound.play("GOLD_JINGLE");
                }
                it.remove();
                continue;
            }

            if (m.isDeadOrEscaped()) {
                it.remove();
                continue;
            }

            if (!m.hasPower(BlackHandPower.POWER_ID)) {
                it.remove();
            }
        }
    }

    @Override
    public void onBlackHandApplied(AbstractCreature target, int appliedAmount) {
        if (!(target instanceof AbstractMonster) || this.amount <= 0) {
            return;
        }
        AbstractMonster monster = (AbstractMonster) target;
        if (monster.isDeadOrEscaped()) {
            return;
        }
        this.trackedTargets.add(monster);
        flash();
        addToBot(new DamageAction(
                monster,
                new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.THORNS),
                AbstractGameAction.AttackEffect.BLUNT_LIGHT
        ));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.amount2 + DESCRIPTIONS[2];
    }
}
