package basicmod.powers;

import basicmod.BasicMod;
import basicmod.helpers.DemonQiDamageHelper;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class FrostHellPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("FrostHellPower");
    private static final int MAX_SINGLE_DAMAGE = 40;
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int lastHp = -1;


    public FrostHellPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(POWER_ID, PowerType.DEBUFF, false, owner, source, amount);
        this.name = NAME;
        this.canGoNegative = false;
        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        if (!this.owner.isPlayer) {
            flash();
            int dmg = Math.max(5, this.owner.currentHealth / 5);
            // 统一处理冰狱自己的伤害上限，以及大法师老爹的额外封顶。
            dmg = DemonQiDamageHelper.applyDemonQiDamageCaps(this.source, this.owner, POWER_ID, dmg, MAX_SINGLE_DAMAGE, "冰狱回合开始");
            addToBot(new LoseHPAction(this.owner, this.source, dmg));
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
    public void atEndOfTurn(boolean isPlayer) {
        if (!isPlayer) {
            addToBot(new ReducePowerAction(this.owner, this.owner, this, 1));
        }
    }


    @Override
    public void updateDescription() {
        int dmg = Math.min(MAX_SINGLE_DAMAGE, Math.max(5, this.owner.currentHealth / 5));
        this.description = DESCRIPTIONS[0] + dmg + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
    }

}
