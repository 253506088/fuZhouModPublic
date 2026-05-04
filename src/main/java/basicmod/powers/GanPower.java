package basicmod.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;

import static basicmod.BasicMod.makeID;

public class GanPower extends BasePower {
    public static final String POWER_ID = makeID("GanPower");
    private static final PowerType TYPE = PowerType.BUFF;
    private static final boolean TURN_BASED = false;

    public GanPower(AbstractCreature owner, int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + (this.amount * 10) + DESCRIPTIONS[1];
    }
}
