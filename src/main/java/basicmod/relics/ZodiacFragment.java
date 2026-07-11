package basicmod.relics;

import basicmod.enums.CharacterEnums;
import basicmod.helpers.FusionHelper;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

/**
 * 生肖碎片
 * 稀有遗物（圣主专属）
 * 战斗开始时，每持有 2 个动物符咒，抽 1 张牌（最多 3 张）。
 */
public class ZodiacFragment extends BaseRelic {
    public static final String NAME = "ZodiacFragment";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    public ZodiacFragment() {
        super(ID, NAME, CharacterEnums.SHENGZHU_COLOR, RARITY, SOUND);
    }

    @Override
    public void atBattleStart() {
        int talismans = FusionHelper.countTalismanRelics();
        int draw = Math.min(talismans / 2, 3);
        if (draw > 0) {
            flash();
            addToBot(new DrawCardAction(AbstractDungeon.player, draw));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
