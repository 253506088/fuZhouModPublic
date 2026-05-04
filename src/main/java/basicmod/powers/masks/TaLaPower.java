package basicmod.powers.masks;

import basicmod.BasicMod;
import basicmod.actions.AddMaskCardAction;
import basicmod.powers.BasePower;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

import java.util.ArrayList;

public class TaLaPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("TaLaPower"); // ID Must match MaskManager constant
    private static final PowerType TYPE = PowerType.BUFF;
    private static final boolean TURN_BASED = false;
    private int randomCardsPerTurn = 1;
    private int baseMaskCastCount = 0;
    private int upgradedMaskCastCount = 0;
    private final ArrayList<TaLaMaskRecord> peelMaskRecords = new ArrayList<>();

    public TaLaPower(AbstractCreature owner, int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(this.ID);
        if (powerStrings != null && powerStrings.DESCRIPTIONS != null && powerStrings.DESCRIPTIONS.length > 0) {
            this.name = powerStrings.NAME;
            this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1];
        }
        basicmod.helpers.MaskManager.updateMaskOrbs();
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        basicmod.helpers.MaskManager.updateMaskOrbs();
    }

    @Override
    public void onRemove() {
        super.onRemove();
        basicmod.helpers.MaskManager.updateMaskOrbs();
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        if (stackAmount > 0) {
            this.randomCardsPerTurn++;
        }
    }

    @Override
    public void atStartOfTurnPostDraw() {
        super.atStartOfTurnPostDraw();
        this.flash();

        AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.utility.WaitAction(0.1F));
        for (int i = 0; i < this.randomCardsPerTurn; i++) {
            queueRandomShadowKhanCard();
        }
    }

    public void recordTalaMaskCast(boolean upgraded) {
        recordTalaMaskCast(upgraded, 0);
    }

    public void recordTalaMaskCast(boolean upgraded, int permanentCostReductionCount) {
        if (upgraded) {
            this.upgradedMaskCastCount++;
        } else {
            this.baseMaskCastCount++;
        }
        this.peelMaskRecords.add(new TaLaMaskRecord(upgraded, Math.max(0, permanentCostReductionCount)));
    }

    public int getTalaCastCountForPeel() {
        return getPeelMaskRecordsSnapshot().size();
    }

    public int getPeelBaseMaskCount() {
        int count = 0;
        for (TaLaMaskRecord record : getPeelMaskRecordsSnapshot()) {
            if (!record.upgraded) {
                count++;
            }
        }
        return count;
    }

    public int getPeelUpgradedMaskCount() {
        int count = 0;
        for (TaLaMaskRecord record : getPeelMaskRecordsSnapshot()) {
            if (record.upgraded) {
                count++;
            }
        }
        return count;
    }

    public ArrayList<TaLaMaskRecord> getPeelMaskRecordsSnapshot() {
        int tracked = this.baseMaskCastCount + this.upgradedMaskCastCount;
        int total = Math.max(tracked, Math.max(1, this.randomCardsPerTurn));
        ArrayList<TaLaMaskRecord> snapshot = new ArrayList<>(this.peelMaskRecords);

        while (snapshot.size() > total) {
            snapshot.remove(snapshot.size() - 1);
        }

        int upgradedRecorded = 0;
        for (TaLaMaskRecord record : snapshot) {
            if (record.upgraded) {
                upgradedRecorded++;
            }
        }

        int missingTracked = Math.max(0, tracked - snapshot.size());
        int missingUpgraded = Math.max(0, Math.min(missingTracked, this.upgradedMaskCastCount - upgradedRecorded));
        int missingBase = missingTracked - missingUpgraded;
        for (int i = 0; i < missingBase; i++) {
            snapshot.add(new TaLaMaskRecord(false, 0));
        }
        for (int i = 0; i < missingUpgraded; i++) {
            snapshot.add(new TaLaMaskRecord(true, 0));
        }
        while (snapshot.size() < total) {
            snapshot.add(new TaLaMaskRecord(false, 0));
        }
        return snapshot;
    }

    public static void queueRandomShadowKhanCard() {
        ArrayList<AbstractCard> cards = new ArrayList<>();
        cards.add(new basicmod.cards.shadowkhan.NiJiaNinja());
        cards.add(new basicmod.cards.shadowkhan.LaZuoBlade());
        cards.add(new basicmod.cards.shadowkhan.SaMoTroll());
        cards.add(new basicmod.cards.shadowkhan.BaTeBat());
        cards.add(new basicmod.cards.shadowkhan.KaBoPincer());
        cards.add(new basicmod.cards.shadowkhan.LeiSuAlien());
        cards.add(new basicmod.cards.shadowkhan.ManNiMantis());
        cards.add(new basicmod.cards.shadowkhan.MingTaShadow());
        cards.add(new basicmod.cards.shadowkhan.YiKaSamurai());

        AbstractCard rand = cards.get(AbstractDungeon.cardRandomRng.random(cards.size() - 1));
        AbstractDungeon.actionManager.addToBottom(new AddMaskCardAction(rand));
    }

    public static class TaLaMaskRecord {
        private final boolean upgraded;
        private final int permanentCostReductionCount;

        private TaLaMaskRecord(boolean upgraded, int permanentCostReductionCount) {
            this.upgraded = upgraded;
            this.permanentCostReductionCount = Math.max(0, permanentCostReductionCount);
        }

        public boolean isUpgraded() {
            return upgraded;
        }

        public int getPermanentCostReductionCount() {
            return permanentCostReductionCount;
        }
    }
}
