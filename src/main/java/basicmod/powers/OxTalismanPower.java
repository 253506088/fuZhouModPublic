package basicmod.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.DexterityPower;

import static basicmod.BasicMod.makeID;

public class OxTalismanPower extends BasePower {
    public static final String POWER_ID = makeID("OxTalismanPower");

    public OxTalismanPower(AbstractCreature owner) {
        super(POWER_ID, PowerType.BUFF, false, owner, 1);
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().monsters != null) {
            int playerStr = getAmount(owner, StrengthPower.POWER_ID);
            int playerDex = getAmount(owner, DexterityPower.POWER_ID);

            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (m != null && !m.isDeadOrEscaped()) {
                    // Check Strength
                    int monsterStr = getAmount(m, StrengthPower.POWER_ID);
                    if (monsterStr > playerStr) {
                        applyStatChange(m, StrengthPower.POWER_ID, playerStr - monsterStr);
                    }
                    // Check Dexterity
                    int monsterDex = getAmount(m, DexterityPower.POWER_ID);
                    if (monsterDex > playerDex) {
                        applyStatChange(m, DexterityPower.POWER_ID, playerDex - monsterDex);
                    }
                }
            }
        }
    }

    private int getAmount(AbstractCreature c, String powerID) {
        AbstractPower p = c.getPower(powerID);
        return p != null ? p.amount : 0;
    }

    private void applyStatChange(AbstractCreature target, String powerID, int diff) {
        AbstractPower p = target.getPower(powerID);
        if (p != null) {
            p.amount += diff;
            p.flash();
            p.updateDescription();
        } else if (diff < 0) {
            AbstractPower newPower;
            if (powerID.equals(StrengthPower.POWER_ID)) {
                newPower = new StrengthPower(target, diff);
            } else {
                newPower = new DexterityPower(target, diff);
            }
            target.powers.add(newPower);
            newPower.flash();
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
