package basicmod.relics;

import basicmod.enums.CharacterEnums;
import basicmod.helpers.FusionHelper;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static basicmod.BasicMod.makeID;

/**
 * 十二生肖·圆满 - 稀有遗物（圣主专属）
 * 每场战斗开始，若拥有6个以上不同符咒遗物，获得2层力量与1点能量。
 */
public class TwelveZodiacPerfection extends BaseRelic {
    public static final String NAME = "TwelveZodiacPerfection";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    public TwelveZodiacPerfection() {
        super(ID, NAME, CharacterEnums.SHENGZHU_COLOR, RARITY, SOUND);
    }

    @Override
    public void atBattleStart() {
        int count = FusionHelper.countTalismanRelics();
        if (count >= 6) {
            flash();
            AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                            new StrengthPower(AbstractDungeon.player, 2), 2));
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
