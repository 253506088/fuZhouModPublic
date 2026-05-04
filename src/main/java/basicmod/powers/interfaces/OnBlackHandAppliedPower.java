package basicmod.powers.interfaces;

import com.megacrit.cardcrawl.core.AbstractCreature;

public interface OnBlackHandAppliedPower {
    void onBlackHandApplied(AbstractCreature target, int appliedAmount);
}
