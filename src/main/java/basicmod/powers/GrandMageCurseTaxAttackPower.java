package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class GrandMageCurseTaxAttackPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("GrandMageCurseTaxAttackPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public GrandMageCurseTaxAttackPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.DEBUFF, true, owner, amount);
        this.name = NAME;
        this.amount = Math.max(1, amount);
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        BasicMod.logger.info("【诅咒税Power】施加攻击税，amount={}。", this.amount);
        refreshHandCosts();
    }

    @Override
    public void onRemove() {
        BasicMod.logger.info("【诅咒税Power】移除攻击税，开始刷新手牌费用显示。");
        refreshHandCosts();
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    private void refreshHandCosts() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.hand == null) {
            return;
        }
        BasicMod.logger.info("【诅咒税Power】攻击税刷新手牌，当前手牌数={}。", AbstractDungeon.player.hand.size());
        AbstractDungeon.player.hand.applyPowers();
        AbstractDungeon.player.hand.glowCheck();
    }
}
