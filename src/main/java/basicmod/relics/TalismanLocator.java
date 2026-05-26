package basicmod.relics;

import basicmod.BasicMod;
import basicmod.helpers.TalismanInputHelper;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

public class TalismanLocator extends BaseRelic {
    public static final String NAME = "TalismanLocator";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.CLINK;

    public TalismanLocator() {
        super(ID, NAME, RARITY, SOUND);
        this.counter = BasicMod.semiAutoMode ? 1 : 0;
    }

    @Override
    public void atPreBattle() {
        this.counter = BasicMod.semiAutoMode ? 1 : 0;
    }

    @Override
    public void update() {
        super.update();
        if (TalismanInputHelper.isRelicRightClickTriggered(this)) {
            CardCrawlGame.sound.play("UI_CLICK_1");
            BasicMod.semiAutoMode = !BasicMod.semiAutoMode;
            BasicMod.saveConfig();
            this.counter = BasicMod.semiAutoMode ? 1 : 0;
            syncActivationStates();
            this.flash();
        }
    }

    private void syncActivationStates() {
        if (!CardCrawlGame.isInARun() || AbstractDungeon.player == null || AbstractDungeon.getCurrRoom() == null || AbstractDungeon.getCurrRoom().phase != com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.COMBAT) {
            return;
        }

        for (com.megacrit.cardcrawl.relics.AbstractRelic r : AbstractDungeon.player.relics) {
            if (r instanceof OxTalisman) ((OxTalisman) r).setActivated(BasicMod.semiAutoMode);
            if (r instanceof SnakeTalisman) ((SnakeTalisman) r).setActivated(BasicMod.semiAutoMode);
            if (r instanceof PigTalisman) ((PigTalisman) r).setActivated(BasicMod.semiAutoMode);
            if (r instanceof SheepTalisman) ((SheepTalisman) r).setActivated(BasicMod.semiAutoMode);
            if (r instanceof RabbitTalisman) ((RabbitTalisman) r).setActivated(BasicMod.semiAutoMode);
            if (r instanceof HorseTalisman) ((HorseTalisman) r).setActivated(BasicMod.semiAutoMode);
            if (r instanceof MonkeyTalisman) ((MonkeyTalisman) r).setActivated(BasicMod.semiAutoMode);
        }
    }

    public static boolean isSemiAuto() {
        return BasicMod.semiAutoMode;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
