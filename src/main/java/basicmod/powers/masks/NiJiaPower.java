package basicmod.powers.masks;

import basicmod.BasicMod;
import basicmod.cards.shadowkhan.NiJiaNinja;
import basicmod.powers.BaseMaskPower;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class NiJiaPower extends BaseMaskPower {
    public static final String POWER_ID = BasicMod.makeID(NiJiaPower.class.getSimpleName());
    private static final PowerType TYPE = PowerType.BUFF;
    private static final boolean TURN_BASED = false;

    public NiJiaPower(AbstractCreature owner, int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
    }

    @Override
    public AbstractCard getLinkedShadowKhanCard() {
        return new NiJiaNinja();
    }
}
