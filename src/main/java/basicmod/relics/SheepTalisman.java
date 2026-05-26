package basicmod.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.OnApplyPowerRelic;
import basicmod.helpers.TalismanInputHelper;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import static basicmod.BasicMod.makeID;

public class SheepTalisman extends BaseRelic implements OnApplyPowerRelic {
    public static final String NAME = "SheepTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;
    private static final float DEBUFF_MULTIPLIER = 2.0f;

    private boolean activated = false;
    private boolean openingHandUpgradedThisCombat = false;

    public SheepTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public boolean canInteractInCombat() {
        return super.canInteractInCombat() && this.counter <= 0;
    }

    @Override
    public void atPreBattle() {
        this.activated = false;
        this.openingHandUpgradedThisCombat = false;
        this.counter = 0;
        this.grayscale = false;
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

        if (TalismanLocator.isSemiAuto() && this.counter <= 0) {
            setActivated(true);
        }
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
    public void atTurnStartPostDraw() {
        // 鼠+羊觉醒削弱：仅在本场战斗首回合起手摸牌完成后，统一升级当前手牌一次
        if (this.openingHandUpgradedThisCombat) return;
        if (AbstractDungeon.player == null || !AbstractDungeon.player.hasRelic(RatTalisman.ID)) return;
        if (com.megacrit.cardcrawl.actions.GameActionManager.turn > 1) return;

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (SheepTalisman.this.openingHandUpgradedThisCombat
                        || AbstractDungeon.player == null
                        || AbstractDungeon.player.hand == null
                        || com.megacrit.cardcrawl.actions.GameActionManager.turn > 1) {
                    this.isDone = true;
                    return;
                }

                SheepTalisman.this.openingHandUpgradedThisCombat = true;
                SheepTalisman.this.flash();
                for (com.megacrit.cardcrawl.cards.AbstractCard c : AbstractDungeon.player.hand.group) {
                    if (c.cardID.equals(basicmod.cards.NothingLackingCard.ID)) continue;
                    if (c.canUpgrade()) {
                        c.upgrade();
                        c.superFlash();
                    }
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public boolean onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        return true;
    }

    @Override
    public int onApplyPowerStacks(AbstractPower power, AbstractCreature target, AbstractCreature source, int stackAmount) {
        if (this.activated
                && this.counter <= 0
                && power.type == AbstractPower.PowerType.DEBUFF
                && target != source
                && source == AbstractDungeon.player
                && !basicmod.util.MechanicsContext.isProcessingDoubling
                && !basicmod.util.MechanicsContext.isApplyingMoonInversion) {
            this.flash();
            this.activated = false;
            this.stopPulse();
            this.counter = 2;
            this.grayscale = true;

            int boostedStack = applyDebuffMultiplier(stackAmount);
            if (power != null) {
                power.amount = applyDebuffMultiplier(power.amount);
                power.updateDescription();
            }
            return boostedStack;
        }
        return stackAmount;
    }

    private int applyDebuffMultiplier(int amount) {
        if (amount == 0) return 0;
        int scaled = Math.round(amount * DEBUFF_MULTIPLIER);
        if (amount > 0) {
            return Math.max(1, scaled);
        }
        return Math.min(-1, scaled);
    }

    public void resetCooldown() {
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
    public void onVictory() {
        this.counter = -1;
        this.activated = false;
        this.grayscale = false;
        this.stopPulse();
    }

    @Override
    public String getUpdatedDescription() {
        return com.megacrit.cardcrawl.dungeons.AbstractDungeon.player != null
                && com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(RatTalisman.ID)
                ? (DESCRIPTIONS.length > 1 ? DESCRIPTIONS[1] : DESCRIPTIONS[0])
                : DESCRIPTIONS[0];
    }
}
