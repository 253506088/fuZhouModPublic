package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class GrandMageChantShieldPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("GrandMageChantShieldPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public GrandMageChantShieldPower(AbstractCreature owner, int shieldAmount) {
        super(POWER_ID, PowerType.BUFF, false, owner, shieldAmount);
        this.name = NAME;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}

