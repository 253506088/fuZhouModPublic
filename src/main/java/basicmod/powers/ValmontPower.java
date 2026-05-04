package basicmod.powers;

import basicmod.enums.CustomTags;
import basicmod.helpers.BlackHandCardHelper;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

public class ValmontPower extends BasePower {
    public static final String POWER_ID = makeID("ValmontPower");
    private static final PowerType TYPE = PowerType.BUFF;
    private static final boolean TURN_BASED = true;

    public ValmontPower(AbstractCreature owner, int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void onInitialApplication() {
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (shouldReduceCost(c)) {
                c.setCostForTurn(0);
            }
        }
        for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
            if (shouldReduceCost(c)) {
                c.setCostForTurn(0);
            }
        }
        for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
            if (shouldReduceCost(c)) {
                c.setCostForTurn(0);
            }
        }
        this.flash();
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        if (AbstractDungeon.player != null && AbstractDungeon.player.hand != null) {
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                if (shouldReduceCost(c) && c.costForTurn > 0) {
                    c.setCostForTurn(0);
                    this.flash();
                }
            }
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }

    private boolean shouldReduceCost(AbstractCard c) {
        return BlackHandCardHelper.isAfuOrBlackHandCard(c)
                || c.hasTag(CustomTags.DAOLONG_DARK_ASSASSIN);
    }
}