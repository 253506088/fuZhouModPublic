package basicmod.relics;

import basicmod.enums.CharacterEnums;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import static basicmod.BasicMod.makeID;

public class ChronicleQuill extends BaseRelic {
    public static final String NAME = "ChronicleQuill";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.CLINK;
    public ChronicleQuill() { super(ID, NAME, CharacterEnums.SHENGZHU_COLOR, RARITY, SOUND); }
    @Override public void atTurnStart() { flash(); AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1)); }
    @Override public String getUpdatedDescription() { return DESCRIPTIONS[0]; }
}
