package basicmod.potions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

public class RewriteInk extends BasePotion {
    public static final String POTION_ID = makeID("RewriteInk");

    public RewriteInk() {
        super(POTION_ID, 3, PotionRarity.UNCOMMON, PotionSize.M,
              new Color(0.1f, 0.2f, 0.8f, 1f), null, null);
    }

    @Override
    public void use(AbstractCreature target) {
        AbstractDungeon.actionManager.addToBottom(
            new DrawCardAction(AbstractDungeon.player, potency));
    }

    @Override
    public String getDescription() {
        return DESCRIPTIONS[0];
    }
}
