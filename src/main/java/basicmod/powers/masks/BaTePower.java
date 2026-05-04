package basicmod.powers.masks;

import basicmod.BasicMod;
import basicmod.cards.shadowkhan.BaTeBat;
import basicmod.powers.BaseMaskPower;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class BaTePower extends BaseMaskPower {
    public static final String POWER_ID = BasicMod.makeID(BaTePower.class.getSimpleName());
    private static final PowerType TYPE = PowerType.BUFF;
    private static final boolean TURN_BASED = false;

    public BaTePower(AbstractCreature owner, int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
    }

    @Override
    public AbstractCard getLinkedShadowKhanCard() {
        return new BaTeBat();
    }
}
