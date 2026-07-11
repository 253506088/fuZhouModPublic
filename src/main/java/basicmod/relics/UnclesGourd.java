package basicmod.relics;

import basicmod.enums.CharacterEnums;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import static basicmod.BasicMod.makeID;

public class UnclesGourd extends BaseRelic {
    public static final String NAME = "UnclesGourd";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.UNCOMMON;
    private static final LandingSound SOUND = LandingSound.FLAT;
    public UnclesGourd() { super(ID, NAME, CharacterEnums.SHENGZHU_COLOR, RARITY, SOUND); }
    @Override public void atTurnStart() { flash(); AbstractDungeon.actionManager.addToBottom(new HealAction(AbstractDungeon.player, AbstractDungeon.player, 2)); }
    @Override public String getUpdatedDescription() { return DESCRIPTIONS[0]; }
}
