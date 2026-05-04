package basicmod.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

public class RoosterTalisman extends BaseRelic {
    public static final String NAME = "RoosterTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    public RoosterTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void onEquip() {
        basemod.BaseMod.MAX_HAND_SIZE += 3; // 获得遗物时仅提高手牌上限，不改回合开始摸牌量
    }

    @Override
    public void onUnequip() {
        basemod.BaseMod.MAX_HAND_SIZE -= 3; // 失去遗物时回退手牌上限
    }

    @Override
    public void onPlayerEndTurn() {
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(RatTalisman.ID)) {
            if (!AbstractDungeon.player.hand.isEmpty() && !AbstractDungeon.player.hasRelic("Runic Pyramid") && !AbstractDungeon.player.hasPower("Equilibrium")) {
                this.flash();
                addToBot(new com.megacrit.cardcrawl.actions.unique.RetainCardsAction(AbstractDungeon.player, AbstractDungeon.player.hand.size()));
            }
        }
    }

    @Override
    public String getUpdatedDescription() {
        return com.megacrit.cardcrawl.dungeons.AbstractDungeon.player != null && com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(RatTalisman.ID) ? (DESCRIPTIONS.length > 1 ? DESCRIPTIONS[1] : DESCRIPTIONS[0]) : DESCRIPTIONS[0];
    }
}
