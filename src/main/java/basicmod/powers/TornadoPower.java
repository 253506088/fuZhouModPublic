package basicmod.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

import static basicmod.BasicMod.makeID;

public class TornadoPower extends BasePower {
    public static final String POWER_ID = makeID("TornadoPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int damageDealtThisTurn = 0;

    public TornadoPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
        this.damageDealtThisTurn = 0;
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.type == DamageInfo.DamageType.NORMAL && damageAmount > 0) {
            this.damageDealtThisTurn += damageAmount;
            updateDescription();
        }
    }

    @Override
    public void atStartOfTurn() {
        this.damageDealtThisTurn = 0;
        updateDescription();
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer && this.damageDealtThisTurn > 0) {
            int tornadoDamage = (int)(this.damageDealtThisTurn * 0.3);
            if (tornadoDamage > 0) {
                this.flash();
                addToBot(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(tornadoDamage, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.SLASH_HEAVY));
            }
        }
    }

    @Override
    public void updateDescription() {
        int tornadoDamage = (int)(this.damageDealtThisTurn * 0.3);
        this.description = DESCRIPTIONS[0] + tornadoDamage + DESCRIPTIONS[1];
    }
}
