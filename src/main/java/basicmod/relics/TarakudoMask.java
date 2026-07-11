package basicmod.relics;

import basicmod.enums.CharacterEnums;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

/**
 * 塔拉库多面具 - 罕见遗物（圣主专属）
 * 每场战斗开始获得1点能量。
 */
public class TarakudoMask extends BaseRelic {
    public static final String NAME = "TarakudoMask";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.UNCOMMON;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    public TarakudoMask() {
        super(ID, NAME, CharacterEnums.SHENGZHU_COLOR, RARITY, SOUND);
    }

    @Override
    public void atBattleStart() {
        flash();
        AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
    }

    @Override
    public String getUpdatedDescription() { return DESCRIPTIONS[0]; }
}
