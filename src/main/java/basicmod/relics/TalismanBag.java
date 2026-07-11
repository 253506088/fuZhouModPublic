package basicmod.relics;

import basicmod.enums.CharacterEnums;
import basicmod.helpers.FusionHelper;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

import static basicmod.BasicMod.makeID;

/**
 * 符咒袋
 * 普通遗物（圣主专属）
 * 战斗开始时获得 1 点能量。若持有 3 个及以上动物符咒，改为获得 2 点能量。
 */
public class TalismanBag extends BaseRelic {
    public static final String NAME = "TalismanBag";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.COMMON;
    private static final LandingSound SOUND = LandingSound.CLINK;

    public TalismanBag() {
        super(ID, NAME, CharacterEnums.SHENGZHU_COLOR, RARITY, SOUND);
    }

    @Override
    public void atBattleStart() {
        flash();
        addToBot(new GainEnergyAction(1));
        if (FusionHelper.countTalismanRelics() >= 3) {
            addToBot(new GainEnergyAction(1));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
