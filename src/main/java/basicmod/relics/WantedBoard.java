package basicmod.relics;

import basicmod.enums.CharacterEnums;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import static basicmod.BasicMod.makeID;

public class WantedBoard extends BaseRelic {
    public static final String NAME = "WantedBoard";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.COMMON;
    private static final LandingSound SOUND = LandingSound.FLAT;
    public WantedBoard() { super(ID, NAME, CharacterEnums.SHENGZHU_COLOR, RARITY, SOUND); }
    @Override public void atBattleStart() {
        flash();
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(mo, AbstractDungeon.player, new VulnerablePower(mo, 1, false), 1));
        }
    }
    @Override public String getUpdatedDescription() { return DESCRIPTIONS[0]; }
}
