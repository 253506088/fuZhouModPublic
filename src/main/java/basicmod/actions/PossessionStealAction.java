package basicmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.RitualPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

/**
 * 夺舍偷取动作。
 * 从目标身上夺走力量、人工制品、仪式、金属化与当前格挡。
 */
public class PossessionStealAction extends AbstractGameAction {
    private final AbstractPlayer player;
    private final AbstractMonster monster;

    /**
     * 构造函数。
     *
     * @param player 玩家
     * @param monster 被夺舍的怪物
     */
    public PossessionStealAction(AbstractPlayer player, AbstractMonster monster) {
        this.player = player;
        this.monster = monster;
        this.actionType = ActionType.POWER;
    }

    /**
     * 执行偷取逻辑。
     */
    @Override
    public void update() {
        if (this.player == null || this.monster == null || this.monster.isDeadOrEscaped()) {
            this.isDone = true;
            return;
        }
        stealStrength();
        stealArtifact();
        stealRitual();
        stealMetallicize();
        stealBlock();
        this.isDone = true;
    }

    private void stealStrength() {
        if (this.monster.hasPower(StrengthPower.POWER_ID)) {
            int amount = Math.max(0, this.monster.getPower(StrengthPower.POWER_ID).amount);
            if (amount > 0) {
                addToTop(new ReducePowerAction(this.monster, this.player, StrengthPower.POWER_ID, amount));
                addToTop(new ApplyPowerAction(this.player, this.player, new StrengthPower(this.player, amount), amount));
            }
        }
    }

    private void stealArtifact() {
        if (this.monster.hasPower(ArtifactPower.POWER_ID)) {
            int amount = Math.max(0, this.monster.getPower(ArtifactPower.POWER_ID).amount);
            if (amount > 0) {
                addToTop(new ReducePowerAction(this.monster, this.player, ArtifactPower.POWER_ID, amount));
                addToTop(new ApplyPowerAction(this.player, this.player, new ArtifactPower(this.player, amount), amount));
            }
        }
    }

    private void stealRitual() {
        if (this.monster.hasPower(RitualPower.POWER_ID)) {
            int amount = Math.max(0, this.monster.getPower(RitualPower.POWER_ID).amount);
            if (amount > 0) {
                addToTop(new ReducePowerAction(this.monster, this.player, RitualPower.POWER_ID, amount));
                addToTop(new ApplyPowerAction(this.player, this.player, new RitualPower(this.player, amount, true), amount));
            }
        }
    }

    private void stealMetallicize() {
        if (this.monster.hasPower(MetallicizePower.POWER_ID)) {
            int amount = Math.max(0, this.monster.getPower(MetallicizePower.POWER_ID).amount);
            if (amount > 0) {
                addToTop(new ReducePowerAction(this.monster, this.player, MetallicizePower.POWER_ID, amount));
                addToTop(new ApplyPowerAction(this.player, this.player, new MetallicizePower(this.player, amount), amount));
            }
        }
    }

    private void stealBlock() {
        int block = Math.max(0, this.monster.currentBlock);
        if (block > 0) {
            this.monster.loseBlock(block);
            AbstractDungeon.actionManager.addToTop(new GainBlockAction(this.player, this.player, block));
        }
    }
}
