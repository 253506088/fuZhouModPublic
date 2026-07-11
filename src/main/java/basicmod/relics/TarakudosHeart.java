package basicmod.relics;

import basicmod.enums.CharacterEnums;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import static basicmod.BasicMod.makeID;

public class TarakudosHeart extends BaseRelic {
    public static final String NAME = "TarakudosHeart";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.UNCOMMON;
    private static final LandingSound SOUND = LandingSound.HEAVY;
    public TarakudosHeart() { super(ID, NAME, CharacterEnums.SHENGZHU_COLOR, RARITY, SOUND); }
    @Override public void atBattleStart() { flash(); AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1)); }
    @Override public String getUpdatedDescription() { return DESCRIPTIONS[0]; }
}
