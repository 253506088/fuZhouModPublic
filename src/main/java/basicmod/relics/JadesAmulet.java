package basicmod.relics;

import basicmod.enums.CharacterEnums;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

/**
 * 小玉的护身符 - 罕见遗物（圣主专属）
 * 每场战斗开始抽1张牌。
 */
public class JadesAmulet extends BaseRelic {
    public static final String NAME = "JadesAmulet";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.UNCOMMON;
    private static final LandingSound SOUND = LandingSound.FLAT;

    public JadesAmulet() {
        super(ID, NAME, CharacterEnums.SHENGZHU_COLOR, RARITY, SOUND);
    }

    @Override
    public void atBattleStart() {
        flash();
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, 1));
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
