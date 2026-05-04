package basicmod.powers;

import basicmod.cards.NothingLackingCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import static basicmod.BasicMod.makeID;

public class SheepResonancePower extends AbstractPower {
    public static final String POWER_ID = makeID("SheepResonancePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SheepResonancePower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        loadRegion("ai"); // Placeholder icon
        updateDescription();
    }

    @Override
    public void atStartOfTurnPostDraw() {
        upgradeCardsInGroup(AbstractDungeon.player.hand);
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        if (card.canUpgrade() && !card.cardID.equals(NothingLackingCard.ID)) {
            card.upgrade();
            card.superFlash();
        }
    }

    private void upgradeCardsInGroup(com.megacrit.cardcrawl.cards.CardGroup group) {
        for (AbstractCard c : group.group) {
            if (c.canUpgrade() && !c.cardID.equals(NothingLackingCard.ID)) {
                c.upgrade();
                c.superFlash();
            }
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
