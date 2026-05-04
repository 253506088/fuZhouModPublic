package basicmod.powers;

import basicmod.BasicMod;
import basicmod.powers.interfaces.OnBlackHandAppliedPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BlackHandPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("BlackHandPower");

    public BlackHandPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(POWER_ID, PowerType.DEBUFF, false, owner, source, amount);
        this.amount = Math.max(0, amount);
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        notifyApplied(this.amount);
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        this.amount = Math.max(0, this.amount);
        notifyApplied(Math.max(0, stackAmount));
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    private void notifyApplied(int appliedAmount) {
        if (appliedAmount <= 0 || AbstractDungeon.player == null || this.source != AbstractDungeon.player) {
            return;
        }
        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (p instanceof OnBlackHandAppliedPower) {
                ((OnBlackHandAppliedPower) p).onBlackHandApplied(this.owner, appliedAmount);
            }
        }
    }

    public static int getAmount(AbstractCreature target) {
        if (target == null || !target.hasPower(POWER_ID)) {
            return 0;
        }
        AbstractPower p = target.getPower(POWER_ID);
        return p == null ? 0 : Math.max(0, p.amount);
    }

    public static boolean hasAny(AbstractCreature target) {
        return getAmount(target) > 0;
    }

    public static void apply(AbstractCreature target, AbstractCreature source, int amount) {
        if (target == null || amount <= 0 || AbstractDungeon.actionManager == null) {
            return;
        }
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(target, source, new BlackHandPower(target, source, amount), amount)
        );
    }

    public static void consume(AbstractCreature target, AbstractCreature source, int amount) {
        if (target == null || amount <= 0 || !target.hasPower(POWER_ID) || AbstractDungeon.actionManager == null) {
            return;
        }
        int current = getAmount(target);
        int consumeAmount = Math.min(amount, current);
        if (consumeAmount <= 0) {
            return;
        }

        if (consumeAmount >= current) {
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(target, source, POWER_ID));
        } else {
            AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(target, source, POWER_ID, consumeAmount));
        }
    }

    public static int consumeAll(AbstractCreature target, AbstractCreature source) {
        int current = getAmount(target);
        consume(target, source, current);
        return current;
    }
}
