package basicmod.potions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;

import static basicmod.BasicMod.makeID;

public class BountyPotion extends BasePotion {
    public static final String POTION_ID = makeID("BountyPotion");

    public BountyPotion() {
        super(POTION_ID, 1, PotionRarity.COMMON, PotionSize.M,
              new Color(0.9f, 0.5f, 0.1f, 1f), null, null);
    }

    @Override
    public void use(AbstractCreature target) {
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped()) {
                AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(m, AbstractDungeon.player,
                        new VulnerablePower(m, potency, false), potency));
            }
        }
    }

    @Override
    public String getDescription() {
        return DESCRIPTIONS[0];
    }
}
