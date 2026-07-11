package basicmod.relics;

import basicmod.enums.CharacterEnums;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

public class DragonBlood extends BaseRelic {
    public static final String NAME = "DragonBlood";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.UNCOMMON;
    private static final LandingSound SOUND = LandingSound.HEAVY;

    public DragonBlood() {
        super(ID, NAME, CharacterEnums.SHENGZHU_COLOR, RARITY, SOUND);
    }

    @Override
    public void atTurnStart() {
        flash();
        AbstractDungeon.player.heal(1);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
