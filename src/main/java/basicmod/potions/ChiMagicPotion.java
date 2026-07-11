package basicmod.potions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

public class ChiMagicPotion extends BasePotion {
    public static final String POTION_ID = makeID("ChiMagicPotion");

    public ChiMagicPotion() {
        super(POTION_ID, 5, PotionRarity.UNCOMMON, PotionSize.M,
              new Color(0.2f, 0.8f, 0.2f, 1f), null, null);
    }

    @Override
    public void use(AbstractCreature target) {
        AbstractDungeon.actionManager.addToBottom(
            new GainBlockAction(AbstractDungeon.player, potency * 3));
    }

    @Override
    public String getDescription() {
        return DESCRIPTIONS[0];
    }
}
