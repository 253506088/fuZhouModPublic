package basicmod.potions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static basicmod.BasicMod.makeID;

public class RagePotion extends BasePotion {
    public static final String POTION_ID = makeID("RagePotion");

    public RagePotion() {
        super(POTION_ID, 2, PotionRarity.UNCOMMON, PotionSize.M,
              new Color(0.9f, 0.1f, 0.1f, 1f), null, null);
    }

    @Override
    public void use(AbstractCreature target) {
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                new StrengthPower(AbstractDungeon.player, potency), potency));
    }

    @Override
    public String getDescription() {
        return DESCRIPTIONS[0];
    }
}
