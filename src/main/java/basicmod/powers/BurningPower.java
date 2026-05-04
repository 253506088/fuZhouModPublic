package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class BurningPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("BurningPower");
    private static final int MAX_SINGLE_DAMAGE = 20;
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int lastHp = -1;


    public BurningPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(POWER_ID, PowerType.DEBUFF, false, owner, source, amount);
        this.name = NAME;
        updateDescription();
        checkEvolution();
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        checkEvolution();
    }

    private void checkEvolution() {
        if (this.amount >= 9) {
            int evolveCount = this.amount / 9;
            int remaining = this.amount % 9;
            
            // 转化为 3 层 灼心
            int heartAmount = evolveCount * 3;
            
            this.amount = remaining;
            com.megacrit.cardcrawl.dungeons.AbstractDungeon.actionManager.addToTop(new com.megacrit.cardcrawl.actions.common.ApplyPowerAction(this.owner, this.source, new BurningHeartPower(this.owner, this.source, heartAmount), heartAmount));
            
            if (this.amount <= 0) {
                com.megacrit.cardcrawl.dungeons.AbstractDungeon.actionManager.addToTop(new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(this.owner, this.source, this.ID));
            }
            updateDescription();
        }
    }

    @Override
    public void atStartOfTurn() {
        if (!this.owner.isPlayer) {
            flash();
            int dmg = Math.min(MAX_SINGLE_DAMAGE, Math.max(2, this.owner.currentHealth / 8));
            dmg = GrandMageBlessingPower.capDemonQiDamage(this.owner, POWER_ID, dmg, "灼烧回合开始");
            // 杀戮尖塔通常往下取整，但最好有个保底2点以防1血怪不死
            addToBot(new LoseHPAction(this.owner, this.source, dmg));
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (!isPlayer) {
            addToBot(new ReducePowerAction(this.owner, this.owner, this, 1));
        }
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        if (this.owner.currentHealth != lastHp) {
            lastHp = this.owner.currentHealth;
            updateDescription();
        }
    }


    @Override
    public void updateDescription() {
        int dmg = Math.min(MAX_SINGLE_DAMAGE, Math.max(2, this.owner.currentHealth / 8));
        this.description = DESCRIPTIONS[0] + dmg + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
    }
}
