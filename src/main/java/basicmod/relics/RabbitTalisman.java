package basicmod.relics;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import basicmod.helpers.TalismanInputHelper;

import static basicmod.BasicMod.makeID;

public class RabbitTalisman extends BaseRelic {
    public static final String NAME = "RabbitTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;
    private static final int REFUND_ENERGY_COOLDOWN = 2;

    private boolean activated = false;
    private boolean usedThisTurn = false;

    public RabbitTalisman() {
        super(ID, NAME, RARITY, SOUND);
        this.counter = 0;
    }

    @Override
    public void atPreBattle() {
        this.activated = false;
        this.usedThisTurn = false;
        this.counter = 0;
        this.grayscale = false;
        stopPulse();
    }

    @Override
    public void atTurnStart() {
        this.usedThisTurn = false;
        if (this.counter > 0) {
            this.counter--;
            if (this.counter == 0) {
                this.grayscale = false;
            }
        }
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(RatTalisman.ID)) {
            this.flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            addToBot(new GainEnergyAction(1));
        }

        if (TalismanLocator.isSemiAuto() && this.counter <= 0) {
            this.activated = true;
            this.beginLongPulse();
        }
    }

    @Override
    public void update() {
        super.update();
        if (canInteractInCombat() && !this.usedThisTurn && this.counter <= 0) {
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

            if (this.activated) {
                for (com.megacrit.cardcrawl.relics.AbstractRelic r : AbstractDungeon.player.relics) {
                    if (r != this && isWhitelistedTalisman(r) && TalismanInputHelper.isRelicRightClickTriggered(r)) {
                        if (tryRefreshRelic(r)) {
                            triggerEffect2();
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean tryRefreshRelic(com.megacrit.cardcrawl.relics.AbstractRelic r) {
        if (!isWhitelistedTalisman(r)) {
            return false;
        }
        if (r instanceof MonkeyTalisman || r instanceof TalismanLocator) {
            return false;
        }

        if (isCooldownTypeRelic(r) && r.counter > 0) {
            r.counter = 0;
            r.grayscale = false;

            if (TalismanLocator.isSemiAuto()) {
                if (r instanceof SnakeTalisman) ((SnakeTalisman) r).setActivated(true);
                if (r instanceof RabbitTalisman) ((RabbitTalisman) r).setActivated(true);
                if (r instanceof OxTalisman) ((OxTalisman) r).setActivated(true);
                if (r instanceof PigTalisman) ((PigTalisman) r).setActivated(true);
                if (r instanceof SheepTalisman) ((SheepTalisman) r).setActivated(true);
            }
            r.flash();
            return true;
        }

        if (r instanceof RatTalisman && r.grayscale) {
            ((RatTalisman) r).resetUsedThisTurn();
            return true;
        }
        if (r instanceof HorseTalisman && r.grayscale) {
            ((HorseTalisman) r).resetUsedThisTurn();
            if (TalismanLocator.isSemiAuto()) ((HorseTalisman) r).setActivated(true);
            return true;
        }
        if (r instanceof OxTalisman && r.grayscale) {
            ((OxTalisman) r).resetUsedThisTurn();
            if (TalismanLocator.isSemiAuto()) ((OxTalisman) r).setActivated(true);
            return true;
        }
        if (r instanceof PigTalisman && r.grayscale) {
            ((PigTalisman) r).resetUsedThisTurn();
            if (TalismanLocator.isSemiAuto()) ((PigTalisman) r).setActivated(true);
            return true;
        }
        if (r instanceof SheepTalisman && r.grayscale) {
            ((SheepTalisman) r).resetCooldown();
            if (TalismanLocator.isSemiAuto()) ((SheepTalisman) r).setActivated(true);
            return true;
        }

        return false;
    }

    /**
     * 兔符咒右键刷新采用白名单：只允许点击十二符咒遗物，避免误点其他遗物浪费冷却。
     */
    private boolean isWhitelistedTalisman(com.megacrit.cardcrawl.relics.AbstractRelic relic) {
        return basicmod.helpers.TalismanHelper.ALL_TALISMAN_IDS.contains(relic.relicId);
    }

    private boolean isCooldownTypeRelic(com.megacrit.cardcrawl.relics.AbstractRelic r) {
        return r instanceof SnakeTalisman
                || r instanceof RabbitTalisman
                || r instanceof OxTalisman
                || r instanceof PigTalisman
                || r instanceof SheepTalisman;
    }

    private void triggerEffect1() {
        this.flash();
        this.activated = false;
        this.usedThisTurn = true;
        this.stopPulse();
        this.counter = REFUND_ENERGY_COOLDOWN;
        this.grayscale = true;
    }

    private void triggerEffect2() {
        this.flash();
        this.activated = false;
        this.usedThisTurn = true;
        this.stopPulse();
        this.counter = 4;
        this.grayscale = true;
        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
    }

    @Override
    public void onUseCard(AbstractCard targetCard, UseCardAction useCardAction) {
        if (this.activated && !this.usedThisTurn && this.counter <= 0) {
            boolean hasCost = (targetCard.costForTurn > 0 && !targetCard.freeToPlayOnce)
                    || (targetCard.cost == -1 && targetCard.energyOnUse > 0);

            if (hasCost) {
                triggerEffect1();
                addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                if (targetCard.costForTurn > 0) {
                    addToBot(new GainEnergyAction(targetCard.costForTurn));
                } else if (targetCard.cost == -1) {
                    addToBot(new GainEnergyAction(targetCard.energyOnUse));
                }
            }
        }
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
    public void onVictory() {
        this.activated = false;
        this.usedThisTurn = false;
        this.grayscale = false;
        this.counter = 0;
        stopPulse();
    }

    @Override
    public String getUpdatedDescription() {
        return com.megacrit.cardcrawl.dungeons.AbstractDungeon.player != null
                && com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(RatTalisman.ID)
                ? (DESCRIPTIONS.length > 1 ? DESCRIPTIONS[1] : DESCRIPTIONS[0])
                : DESCRIPTIONS[0];
    }
}
