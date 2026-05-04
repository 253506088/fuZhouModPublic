package basicmod.actions;

import basicmod.util.MechanicsContext;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.StrengthPower;

/**
 * 月之恶魔专用的力量反转施加动作：
 * 仅这一条力量反转不吃羊符咒负面翻倍。
 */
public class MoonInversionApplyStrengthAction extends ApplyPowerAction {
    public MoonInversionApplyStrengthAction(AbstractCreature target, AbstractCreature source, int amount) {
        super(target, source, new StrengthPower(target, amount), amount);
    }

    @Override
    public void update() {
        boolean prev = MechanicsContext.isApplyingMoonInversion;
        MechanicsContext.isApplyingMoonInversion = true;
        try {
            super.update();
        } finally {
            MechanicsContext.isApplyingMoonInversion = prev;
        }
    }
}
