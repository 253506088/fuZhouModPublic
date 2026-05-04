package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class EarthDemonPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("EarthDemonPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public EarthDemonPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, source, amount);
        this.canGoNegative = false;
        updateDescription();
    }
    @Override
    public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
        // 如果回合结束时没有格挡，失去生命值并额外获得 最大生命值 1/8 的格挡
        if (isPlayer && this.owner.currentBlock <= 0) {
            flash();
            addToBot(new LoseHPAction(this.owner, this.owner, this.amount));
            // 获得最大生命值 1/8 的格挡
            int blockAmount = this.owner.maxHealth / 8;
            if (blockAmount > 0) {
                addToBot(new GainBlockAction(this.owner, this.owner, blockAmount));
            }
        }
    }

    @Override
    public void updateDescription() {
        int blockAmount = 0;
        if (this.owner != null) {
            blockAmount = this.owner.maxHealth / 8;
        }
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + blockAmount + DESCRIPTIONS[2];
    }
}
