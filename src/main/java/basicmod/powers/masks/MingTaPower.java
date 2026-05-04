package basicmod.powers.masks;

import basicmod.BasicMod;
import basicmod.cards.shadowkhan.MingTaShadow;
import basicmod.powers.BaseMaskPower;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class MingTaPower extends BaseMaskPower {
    public static final String POWER_ID = BasicMod.makeID(MingTaPower.class.getSimpleName());
    private static final PowerType TYPE = PowerType.BUFF;
    private static final boolean TURN_BASED = false;

    public MingTaPower(AbstractCreature owner, int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
    }

    @Override
    public AbstractCard getLinkedShadowKhanCard() {
        return new MingTaShadow();
    }
}
