package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 破坏轮标记Power
 * 当持有此Power的怪物死亡时，对场上其他怪物造成该怪物最大生命值的伤害
 */
public class RingOfDestructionPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("RingOfDestructionPower");

    public RingOfDestructionPower(AbstractCreature owner, AbstractCreature source) {
        super(POWER_ID, PowerType.DEBUFF, false, owner, source, -1);
    }

    @Override
    public void updateDescription() {
        if (this.DESCRIPTIONS != null && this.DESCRIPTIONS.length >= 2) {
            this.description = DESCRIPTIONS[0] + this.owner.maxHealth + DESCRIPTIONS[1];
        }
    }

    @Override
    public void onDeath() {
        // 怪物死亡时，对场上其他怪物造成该怪物最大生命值的伤害
        int damage = this.owner.maxHealth;
        BasicMod.logger.info("破坏轮触发：" + this.owner.name + " 死亡，对其他怪物造成 " + damage + " 点伤害");

        for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
            if (!mo.isDeadOrEscaped() && mo != this.owner) {
                addToBot(new DamageAction(mo,
                        new DamageInfo(source, damage, DamageInfo.DamageType.THORNS),
                        AbstractGameAction.AttackEffect.FIRE));
            }
        }
    }
}
