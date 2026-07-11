package basicmod.relics;

import basicmod.enums.CharacterEnums;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static basicmod.BasicMod.makeID;

/**
 * 黑影军旗 - 普通遗物（圣主专属）
 * 每回合开始获得1层力量。
 */
public class ShadowBanner extends BaseRelic {
    public static final String NAME = "ShadowBanner";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.COMMON;
    private static final LandingSound SOUND = LandingSound.HEAVY;

    public ShadowBanner() {
        super(ID, NAME, CharacterEnums.SHENGZHU_COLOR, RARITY, SOUND);
    }

    @Override
    public void atTurnStart() {
        flash();
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                        new StrengthPower(AbstractDungeon.player, 1), 1));
    }

    @Override
    public String getUpdatedDescription() { return DESCRIPTIONS[0]; }
}
