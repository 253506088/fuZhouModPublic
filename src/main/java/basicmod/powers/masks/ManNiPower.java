package basicmod.powers.masks;

import basicmod.BasicMod;
import basicmod.cards.shadowkhan.ManNiMantis;
import basicmod.powers.BaseMaskPower;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class ManNiPower extends BaseMaskPower {
    public static final String POWER_ID = BasicMod.makeID(ManNiPower.class.getSimpleName());
    private static final PowerType TYPE = PowerType.BUFF;
    private static final boolean TURN_BASED = false;

    public ManNiPower(AbstractCreature owner, int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
    }

    @Override
    public AbstractCard getLinkedShadowKhanCard() {
        return new ManNiMantis();
    }
}
