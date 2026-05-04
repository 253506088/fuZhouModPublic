package basicmod.powers;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

import static basicmod.BasicMod.makeID;

public class DogTalismanPower extends BasePower {
    public static final String POWER_ID = makeID("DogTalismanPower");
    private final int reviveHeal;
    private final int turnsPerStack;

    public DogTalismanPower(AbstractCreature owner, int reviveHeal, int turnsPerStack) {
        // amount: remaining turns, amount2: remaining revive triggers.
        // Important: disable auto updateDescription in BasePower constructor,
        // otherwise subclass fields are not initialized yet and can NPE.
        super(POWER_ID, PowerType.BUFF, true, owner, null, turnsPerStack, false);
        this.reviveHeal = Math.max(1, reviveHeal);
        this.turnsPerStack = turnsPerStack;
        this.amount2 = 1;
        updateDescription();
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        boolean fromEnemy = info != null && info.owner != null && info.owner != owner;
        if (fromEnemy && this.amount2 > 0 && damageAmount >= owner.currentHealth) {
            flash();
            this.amount2--;

            // Keep 1 HP from lethal damage, then heal to a stable recover amount.
            int preventedDamage = Math.max(0, owner.currentHealth - 1);
            int healAfterSave = Math.max(0, this.reviveHeal - 1);
            if (healAfterSave > 0) {
                addToTop(new HealAction(owner, owner, healAfterSave));
            }

            if (this.amount2 <= 0) {
                addToBot(new RemoveSpecificPowerAction(owner, owner, this));
            }
            updateDescription();
            return preventedDamage;
        }
        return damageAmount;
    }

    public void addStack(int turns) {
        this.fontScale = 8.0F;
        this.amount += Math.max(0, turns);
        this.amount2 += 1;
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        if (stackAmount <= 0) {
            return;
        }
        this.fontScale = 8.0F;
        this.amount += this.turnsPerStack * stackAmount;
        this.amount2 += stackAmount;
        updateDescription();
    }

    @Override
    public void atEndOfRound() {
        this.amount--;
        if (this.amount <= 0 || this.amount2 <= 0) {
            addToBot(new RemoveSpecificPowerAction(owner, owner, this));
        } else {
            updateDescription();
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.reviveHeal + DESCRIPTIONS[2]
                + this.amount2 + DESCRIPTIONS[3];
    }
}
