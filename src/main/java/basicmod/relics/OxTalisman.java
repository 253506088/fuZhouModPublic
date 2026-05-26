package basicmod.relics;

import basicmod.helpers.TalismanInputHelper;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static basicmod.BasicMod.makeID;

public class OxTalisman extends BaseRelic {
    public static final String NAME = "OxTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.HEAVY;

    private boolean activated = false;
    private int ratResonanceTurnCounter = 0;

    public OxTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public boolean canInteractInCombat() {
        return super.canInteractInCombat() && this.counter <= 0;
    }

    @Override
    public void atPreBattle() {
        this.activated = false;
        this.counter = 0;
        this.grayscale = false;
        this.ratResonanceTurnCounter = 0;
        stopPulse();
    }

    @Override
    public void atTurnStart() {
        this.activated = false;
        stopPulse();

        if (this.counter > 0) {
            this.counter--;
            if (this.counter == 0) {
                this.grayscale = false;
            }
        }

        if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(RatTalisman.ID)) {
            this.ratResonanceTurnCounter++;
            boolean trigger = (this.ratResonanceTurnCounter % 2 == 0);
            if (trigger) {
                this.flash();
                addToBot(new com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction(AbstractDungeon.player, this));
                addToBot(new com.megacrit.cardcrawl.actions.common.ApplyPowerAction(
                        AbstractDungeon.player,
                        AbstractDungeon.player,
                        new com.megacrit.cardcrawl.powers.StrengthPower(AbstractDungeon.player, 1),
                        1
                ));
            }
        }

        if (TalismanLocator.isSemiAuto() && this.counter <= 0) {
            setActivated(true);
        }
    }

    public static boolean isActivated() {
        if (AbstractDungeon.player != null) {
            AbstractRelic relic = AbstractDungeon.player.getRelic(ID);
            if (relic instanceof OxTalisman) {
                return ((OxTalisman) relic).activated;
            }
        }
        return false;
    }

    @Override
    public void update() {
        super.update();
        if (canInteractInCombat() && this.counter <= 0) {
            if (TalismanInputHelper.isRelicRightClickTriggered(this)) {
                this.activated = !this.activated;
                if (this.activated) {
                    CardCrawlGame.sound.play("UI_CLICK_1");
                    this.beginLongPulse();
                } else {
                    CardCrawlGame.sound.play("UI_CLICK_2");
                    this.stopPulse();
                }
            }
        }
    }

    @Override
    public void onUseCard(AbstractCard targetCard, UseCardAction useCardAction) {
        if (this.activated
                && targetCard.type == AbstractCard.CardType.ATTACK
                && (targetCard.target == AbstractCard.CardTarget.ENEMY
                || targetCard.target == AbstractCard.CardTarget.SELF_AND_ENEMY)) {
            this.activated = false;
            this.counter = 2;
            this.grayscale = true;
            this.flash();
            this.stopPulse();
        }
    }

    @Override
    public void onVictory() {
        this.activated = false;
        this.counter = -1;
        this.grayscale = false;
        this.ratResonanceTurnCounter = 0;
        stopPulse();
    }

    public void resetUsedThisTurn() {
        this.counter = 0;
        this.grayscale = false;
        this.activated = false;
        this.stopPulse();
        this.flash();
    }

    public void setActivated(boolean activate) {
        if (this.counter > 0) return;
        this.activated = activate;
        if (activate) {
            beginLongPulse();
        } else {
            stopPulse();
        }
    }

    @Override
    public String getUpdatedDescription() {
        return com.megacrit.cardcrawl.dungeons.AbstractDungeon.player != null
                && com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(RatTalisman.ID)
                ? (DESCRIPTIONS.length > 1 ? DESCRIPTIONS[1] : DESCRIPTIONS[0])
                : DESCRIPTIONS[0];
    }
}
