package basicmod.cards;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.helpers.MaskManager;
import basicmod.powers.BaseMaskPower;
import basicmod.powers.masks.*;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class EndlessDarkness extends BaseCard {
    public static final String ID = BasicMod.makeID(EndlessDarkness.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.POWER,
            CardRarity.SPECIAL,
            CardTarget.SELF,
            0
    );

    public EndlessDarkness() {
        super(ID, info);
        this.isInnate = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        MaskManager.maximizeMaskCapacityThisCombat();
        addToBot(new ApplyPowerAction(p, p, new TaLaPower(p, 1), 1));
        final boolean castUpgraded = this.upgraded;
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (p != null && p.getPower(TaLaPower.POWER_ID) instanceof TaLaPower) {
                    ((TaLaPower) p.getPower(TaLaPower.POWER_ID)).recordTalaMaskCast(castUpgraded);
                }
                this.isDone = true;
            }
        });

        // Helper method to apply 1 stack of a mask power, with upgrade flag if needed
        applyMaskPower(p, new NiJiaPower(p, 1));
        applyMaskPower(p, new LaZuoPower(p, 1));
        applyMaskPower(p, new SaMoPower(p, 1));
        applyMaskPower(p, new BaTePower(p, 1));
        applyMaskPower(p, new KaBoPower(p, 1));
        applyMaskPower(p, new LeiSuPower(p, 1));
        applyMaskPower(p, new ManNiPower(p, 1));
        applyMaskPower(p, new MingTaPower(p, 1));
        applyMaskPower(p, new YiKaPower(p, 1));
    }

    private void applyMaskPower(AbstractPlayer p, BaseMaskPower power) {
        if (this.upgraded) {
            power.upgradedAmount += 1; // Mark the power as upgraded to give upgraded shadowkhan cards
        }
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractPower existingPower = p.getPower(power.ID);
                if (existingPower instanceof BaseMaskPower) {
                    BaseMaskPower existingMaskPower = (BaseMaskPower) existingPower;
                    existingMaskPower.recordAppliedMaskStacks(1, upgraded, 0);
                    if (upgraded) {
                        existingMaskPower.upgradedAmount += 1;
                    }
                } else {
                    power.recordAppliedMaskStacks(1, upgraded, 0);
                }
                this.isDone = true;
            }
        });
        addToBot(new ApplyPowerAction(p, p, power, 1));
    }
}
