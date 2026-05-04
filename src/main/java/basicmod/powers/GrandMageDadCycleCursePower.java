package basicmod.powers;

import basicmod.BasicMod;
import basicmod.helpers.GrandMageDadCycleCurseHelper;
import basicmod.monsters.GrandMageDad;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class GrandMageDadCycleCursePower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("GrandMageDadCycleCursePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public GrandMageDadCycleCursePower(AbstractCreature owner, int stage) {
        super(POWER_ID, PowerType.BUFF, true, owner, stage);
        this.name = NAME;
        this.amount = stage;
        this.amount2 = 0;
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        if (this.owner instanceof GrandMageDad) {
            GrandMageDadCycleCurseHelper.onCyclePowerApplied((GrandMageDad) this.owner, this.amount);
        }
    }

    public void resetCardsPlayed() {
        this.amount2 = 0;
        updateDescription();
    }

    public void recordCardPlayed() {
        this.amount2 += 1;
        flash();
        updateDescription();
    }

    @Override
    public void updateDescription() {
        switch (this.amount) {
            case GrandMageDadCycleCurseHelper.STAGE_COLLAPSE_UPGRADES:
                this.description = DESCRIPTIONS[0];
                break;
            case GrandMageDadCycleCurseHelper.STAGE_EXHAUST_HAND:
                this.description = DESCRIPTIONS[1];
                break;
            case GrandMageDadCycleCurseHelper.STAGE_CARD_DAMAGE:
                this.description = DESCRIPTIONS[2];
                break;
            case GrandMageDadCycleCurseHelper.STAGE_CARD_DECAY:
                this.description = DESCRIPTIONS[3] + this.amount2 + DESCRIPTIONS[4];
                break;
            case GrandMageDadCycleCurseHelper.STAGE_TEMP_THORNS:
                this.description = DESCRIPTIONS[5];
                break;
            default:
                this.description = DESCRIPTIONS[6];
                break;
        }
    }
}
