package basicmod.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.NoDrawPower;

import static basicmod.BasicMod.makeID;

public class RoosterTalismanPower extends BasePower {
    public static final String POWER_ID = makeID("RoosterTalismanPower");
    public static final int MAX_HITS = 5;
    private final int dexAmount;

    public RoosterTalismanPower(AbstractCreature owner, int dexAmount) {
        super(POWER_ID, PowerType.BUFF, false, owner, MAX_HITS);
        this.dexAmount = dexAmount;
    }

    @Override
    public void onInitialApplication() {
        addToTop(new ApplyPowerAction(owner, owner, new DexterityPower(owner, dexAmount), dexAmount));
    }

    @Override
    public void onRemove() {
        addToTop(new ReducePowerAction(owner, owner, DexterityPower.POWER_ID, dexAmount));
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType damageType) {
        if (damageType == DamageInfo.DamageType.NORMAL) {
            return damage * 0.5f;
        }
        return damage;
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type == DamageInfo.DamageType.NORMAL && damageAmount > 0) {
            flash();
            this.amount--;
            if (this.amount <= 0) {
                addToBot(new RemoveSpecificPowerAction(owner, owner, this));
                addToBot(new ApplyPowerAction(owner, owner, new NoDrawPower(owner)));
            }
            updateDescription();
        }
        return damageAmount;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + dexAmount + DESCRIPTIONS[1] + amount + DESCRIPTIONS[2];
    }
}
