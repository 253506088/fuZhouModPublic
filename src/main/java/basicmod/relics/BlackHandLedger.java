package basicmod.relics;

import basicmod.enums.CharacterEnums;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

public class BlackHandLedger extends BaseRelic {
    public static final String NAME = "BlackHandLedger";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.COMMON;
    private static final LandingSound SOUND = LandingSound.CLINK;

    public BlackHandLedger() {
        super(ID, NAME, CharacterEnums.SHENGZHU_COLOR, RARITY, SOUND);
    }

    @Override
    public void onEquip() {
        super.onEquip();
        AbstractDungeon.player.gainGold(10);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
