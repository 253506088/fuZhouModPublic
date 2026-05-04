package basicmod.powers.masks;

import basicmod.BasicMod;
import basicmod.cards.shadowkhan.YiKaSamurai;
import basicmod.powers.BaseMaskPower;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class YiKaPower extends BaseMaskPower {
    public static final String POWER_ID = BasicMod.makeID(YiKaPower.class.getSimpleName());
    private static final PowerType TYPE = PowerType.BUFF;
    private static final boolean TURN_BASED = false;

    public YiKaPower(AbstractCreature owner, int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
    }

    @Override
    public AbstractCard getLinkedShadowKhanCard() {
        return new YiKaSamurai();
    }
}
