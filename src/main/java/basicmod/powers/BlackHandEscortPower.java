package basicmod.powers;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static basicmod.BasicMod.makeID;

public class BlackHandEscortPower extends BasePower {
    public static final String POWER_ID = makeID("BlackHandEscortPower");

    public BlackHandEscortPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, true, owner, owner, Math.max(1, amount));
        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        int totalBlackHand = 0;
        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().monsters != null) {
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!m.isDeadOrEscaped()) {
                    totalBlackHand += BlackHandPower.getAmount(m);
                }
            }
        }

        if (totalBlackHand > 0) {
            flash();
            addToBot(new GainBlockAction(this.owner, this.owner, totalBlackHand * this.amount));
        }
        addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}
