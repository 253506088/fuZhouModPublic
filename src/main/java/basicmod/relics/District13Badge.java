package basicmod.relics;

import basicmod.enums.CharacterEnums;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import static basicmod.BasicMod.makeID;

public class District13Badge extends BaseRelic {
    public static final String NAME = "District13Badge";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.UNCOMMON;
    private static final LandingSound SOUND = LandingSound.CLINK;
    public District13Badge() { super(ID, NAME, CharacterEnums.SHENGZHU_COLOR, RARITY, SOUND); }
    @Override public void atBattleStart() { flash(); AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, 1)); }
    @Override public String getUpdatedDescription() { return DESCRIPTIONS[0]; }
}
