package basicmod.relics;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

import static basicmod.BasicMod.makeID;

/**
 * 共鸣之环
 * 罕见遗物（圣主专属）
 * 每当你打出一张融合牌时，获得 1 点能量，每回合最多 2 次。
 */
public class ResonanceRing extends BaseRelic {
    public static final String NAME = "ResonanceRing";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.UNCOMMON;
    private static final LandingSound SOUND = LandingSound.MAGICAL;
    private static final int PER_TURN_CAP = 2;

    private int usedThisTurn = 0;

    public ResonanceRing() {
        super(ID, NAME, CharacterEnums.SHENGZHU_COLOR, RARITY, SOUND);
    }

    @Override
    public void atPreBattle() {
        usedThisTurn = 0;
    }

    @Override
    public void atTurnStart() {
        usedThisTurn = 0;
    }

    @Override
    public void onUseCard(AbstractCard targetCard, UseCardAction useCardAction) {
        if (usedThisTurn < PER_TURN_CAP && targetCard.hasTag(CustomTags.FUSION)) {
            usedThisTurn++;
            flash();
            addToBot(new GainEnergyAction(1));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
