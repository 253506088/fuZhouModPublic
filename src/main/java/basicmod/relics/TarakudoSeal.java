package basicmod.relics;

import basicmod.enums.CharacterEnums;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;

import static basicmod.BasicMod.makeID;

public class TarakudoSeal extends BaseRelic {
    public static final String NAME = "TarakudoSeal";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    public TarakudoSeal() {
        super(ID, NAME, CharacterEnums.SHENGZHU_COLOR, RARITY, SOUND);
    }

    @Override
    public void atBattleStart() {
        flash();
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                new DexterityPower(AbstractDungeon.player, 2), 2));
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
