package basicmod.potions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

public class TalismanPotion extends BasePotion {
    public static final String POTION_ID = makeID("TalismanPotion");

    public TalismanPotion() {
        super(POTION_ID, 1, PotionRarity.COMMON, PotionSize.M,
              new Color(1f, 0.84f, 0f, 1f), null, null);
    }

    @Override
    public void use(AbstractCreature target) {
        AbstractDungeon.actionManager.addToBottom(
            new GainEnergyAction(potency));
    }

    @Override
    public String getDescription() {
        return DESCRIPTIONS[0];
    }
}
