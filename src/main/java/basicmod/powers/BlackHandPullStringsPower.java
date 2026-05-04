package basicmod.powers;

import basicmod.actions.BlackHandDiscardToHandAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

import static basicmod.BasicMod.makeID;

public class BlackHandPullStringsPower extends BasePower {
    public static final String POWER_ID = makeID("BlackHandPullStringsPower");

    public BlackHandPullStringsPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, owner, Math.max(1, amount));
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        if (stackAmount <= 0) {
            return;
        }
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        updateDescription();
    }

    @Override
    public void atStartOfTurnPostDraw() {
        if (this.amount > 0) {
            addToBot(new BlackHandDiscardToHandAction(this.amount));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}
