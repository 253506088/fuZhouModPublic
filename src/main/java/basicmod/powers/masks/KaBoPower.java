package basicmod.powers.masks;

import basicmod.BasicMod;
import basicmod.cards.shadowkhan.KaBoPincer;
import basicmod.powers.BaseMaskPower;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class KaBoPower extends BaseMaskPower {
    public static final String POWER_ID = BasicMod.makeID(KaBoPower.class.getSimpleName());
    private static final PowerType TYPE = PowerType.BUFF;
    private static final boolean TURN_BASED = false;

    public KaBoPower(AbstractCreature owner, int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
    }

    @Override
    public AbstractCard getLinkedShadowKhanCard() {
        return new KaBoPincer();
    }
}
