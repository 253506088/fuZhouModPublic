package basicmod.powers;

import basicmod.powers.interfaces.OnBlackHandAppliedPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.IdentityHashMap;
import java.util.Map;

import static basicmod.BasicMod.makeID;

public class BlackHandNoNameYetPower extends BasePower implements OnBlackHandAppliedPower {
    public static final String POWER_ID = makeID("BlackHandNoNameYetPower");
    private static final int TRIGGER_THRESHOLD = 4;

    private final Map<AbstractCreature, Integer> gainedThisTurn = new IdentityHashMap<>();

    public BlackHandNoNameYetPower(AbstractCreature owner, int triggerDamage) {
        super(POWER_ID, PowerType.BUFF, false, owner, owner, Math.max(0, triggerDamage));
        this.amount2 = 1; // Apply 1 Black Hand stack to all enemies each turn by default.
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        if (stackAmount <= 0) {
            return;
        }
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        this.amount2 += 1;
        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        this.gainedThisTurn.clear();
        if (AbstractDungeon.getCurrRoom() == null || AbstractDungeon.getCurrRoom().monsters == null) {
            return;
        }
        flash();
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped()) {
                BlackHandPower.apply(m, this.owner, this.amount2);
            }
        }
    }

    @Override
    public void atEndOfRound() {
        this.gainedThisTurn.clear();
    }

    @Override
    public void onBlackHandApplied(AbstractCreature target, int appliedAmount) {
        if (!(target instanceof AbstractMonster) || appliedAmount <= 0 || this.amount <= 0) {
            return;
        }
        AbstractMonster monster = (AbstractMonster) target;
        if (monster.isDeadOrEscaped()) {
            return;
        }
        int total = this.gainedThisTurn.getOrDefault(monster, 0) + appliedAmount;
        while (total >= TRIGGER_THRESHOLD) {
            total -= TRIGGER_THRESHOLD;
            flash();
            addToBot(new DamageAction(
                    monster,
                    new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.THORNS),
                    AbstractGameAction.AttackEffect.SLASH_HEAVY
            ));
            addToBot(new DrawCardAction(AbstractDungeon.player, 1));
        }
        if (total > 0) {
            this.gainedThisTurn.put(monster, total);
        } else {
            this.gainedThisTurn.remove(monster);
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount2 + DESCRIPTIONS[1] + TRIGGER_THRESHOLD + DESCRIPTIONS[2] + this.amount + DESCRIPTIONS[3];
    }
}
