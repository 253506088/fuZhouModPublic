package basicmod.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;

import static basicmod.BasicMod.makeID;

public class FlightAfuPower extends BasePower {
    public static final String POWER_ID = makeID("FlightAfuPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public FlightAfuPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
        this.loadRegion("flight");
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType damageType) {
        if (damageType == DamageInfo.DamageType.NORMAL && damage > 0.0F) {
            return damage * 0.5F;
        }
        return damage;
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type == DamageInfo.DamageType.NORMAL && damageAmount > 0) {
            this.flash();
            addToBot(new ReducePowerAction(this.owner, this.owner, this, 1));
        }
        return damageAmount;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
