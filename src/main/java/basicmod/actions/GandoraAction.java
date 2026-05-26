package basicmod.actions;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

/**
 * 破坏龙甘多拉Action
 * 1. 收集玩家和所有怪物的力量绝对值之和
 * 2. 清零所有力量
 * 3. 对所有怪物造成该数值的伤害
 */
public class GandoraAction extends AbstractGameAction {
    private AbstractPlayer player;

    public GandoraAction(AbstractPlayer player) {
        this.player = player;
        this.duration = Settings.ACTION_DUR_FAST;
        this.actionType = ActionType.DAMAGE;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            int totalStrength = 0;

            // 收集玩家力量绝对值
            AbstractPower playerStr = player.getPower(StrengthPower.POWER_ID);
            if (playerStr != null) {
                totalStrength += Math.abs(playerStr.amount);
                BasicMod.logger.info("甘多拉：玩家力量=" + playerStr.amount + "，绝对值=" + Math.abs(playerStr.amount));
                // 清零玩家力量
                addToBot(new RemoveSpecificPowerAction(player, player, StrengthPower.POWER_ID));
            }

            // 收集所有怪物力量绝对值
            for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                if (!mo.isDeadOrEscaped()) {
                    AbstractPower monsterStr = mo.getPower(StrengthPower.POWER_ID);
                    if (monsterStr != null) {
                        totalStrength += Math.abs(monsterStr.amount);
                        BasicMod.logger.info("甘多拉：怪物[" + mo.name + "]力量=" + monsterStr.amount + "，绝对值=" + Math.abs(monsterStr.amount));
                        // 清零怪物力量
                        addToBot(new RemoveSpecificPowerAction(mo, player, StrengthPower.POWER_ID));
                    }
                }
            }

            BasicMod.logger.info("甘多拉：力量绝对值总和=" + totalStrength + "，对所有怪物造成该伤害");

            // 对所有怪物造成力量绝对值之和的伤害
            if (totalStrength > 0) {
                addToBot(new DamageAllEnemiesAction(player,
                        DamageInfo.createDamageMatrix(totalStrength, true),
                        DamageInfo.DamageType.THORNS,
                        AttackEffect.FIRE));
            }
        }
        tickDuration();
    }
}
