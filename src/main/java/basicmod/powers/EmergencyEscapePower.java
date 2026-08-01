package basicmod.powers;

import basicmod.BasicMod;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import com.megacrit.cardcrawl.powers.CorpseExplosionPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

import java.util.ArrayList;

import static basicmod.BasicMod.makeID;

/**
 * 紧急逃生能力。
 * 在剩余回合内免疫怪物施加的减益，并随机反弹两种塔一可用负面状态。
 */
public class EmergencyEscapePower extends BasePower implements OnReceivePowerPower {
    public static final String POWER_ID = makeID("EmergencyEscapePower");
    private static final int REFLECT_AMOUNT = 2;
    private static final int REFLECT_TYPES = 5;

    /**
     * 构造函数。
     *
     * @param owner 持有者
     * @param amount 持续回合数
     */
    public EmergencyEscapePower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, true, owner, owner, Math.max(1, amount));
        updateDescription();
    }

    /**
     * 堆叠持续回合。
     *
     * @param stackAmount 增加回合
     */
    @Override
    public void stackPower(int stackAmount) {
        if (stackAmount <= 0) {
            return;
        }
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        updateDescription();
    }

    /**
     * 玩家回合结束时减少持续回合。
     *
     * @param isPlayer 是否为玩家回合结束
     */
    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (!isPlayer) {
            return;
        }
        if (this.amount <= 1) {
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        } else {
            this.amount--;
            updateDescription();
        }
    }

    /**
     * 拦截来自怪物的减益。
     */
    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (shouldReflect(power, target, source)) {
            reflectDebuffs((AbstractMonster) source);
            return false;
        }
        return true;
    }

    /**
     * 拦截已有减益的叠层。
     */
    @Override
    public int onReceivePowerStacks(AbstractPower power, AbstractCreature target, AbstractCreature source, int stackAmount) {
        if (stackAmount > 0 && shouldReflect(power, target, source)) {
            reflectDebuffs((AbstractMonster) source);
            return 0;
        }
        return stackAmount;
    }

    private boolean shouldReflect(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        return target == this.owner
                && source instanceof AbstractMonster
                && power != null
                && (power.type == PowerType.DEBUFF || (StrengthPower.POWER_ID.equals(power.ID) && power.amount < 0));
    }

    private void reflectDebuffs(AbstractMonster monster) {
        if (monster == null || monster.isDeadOrEscaped()) {
            return;
        }
        this.flash();
        BasicMod.logger.info("【紧急逃生】拦截怪物减益并反弹，来源={}", monster.name);
        ArrayList<Integer> pool = new ArrayList<>();
        for (int i = 0; i < REFLECT_TYPES; i++) {
            pool.add(i);
        }
        for (int i = 0; i < 2 && !pool.isEmpty(); i++) {
            int index = AbstractDungeon.cardRandomRng.random(pool.size() - 1);
            applyReflectedDebuff(monster, pool.remove(index));
        }
    }

    private void applyReflectedDebuff(AbstractMonster monster, int index) {
        switch (index) {
            case 0:
                addToBot(new ApplyPowerAction(monster, this.owner, new VulnerablePower(monster, REFLECT_AMOUNT, false), REFLECT_AMOUNT));
                break;
            case 1:
                addToBot(new ApplyPowerAction(monster, this.owner, new ConstrictedPower(monster, this.owner, REFLECT_AMOUNT), REFLECT_AMOUNT));
                break;
            case 2:
                addToBot(new ApplyPowerAction(monster, this.owner, new WeakPower(monster, REFLECT_AMOUNT, false), REFLECT_AMOUNT));
                break;
            case 3:
                CorpseExplosionPower corpseExplosionPower = new CorpseExplosionPower(monster);
                corpseExplosionPower.amount = REFLECT_AMOUNT;
                corpseExplosionPower.updateDescription();
                addToBot(new ApplyPowerAction(monster, this.owner, corpseExplosionPower, REFLECT_AMOUNT));
                break;
            default:
                addToBot(new ApplyPowerAction(monster, this.owner, new StrengthPower(monster, -REFLECT_AMOUNT), -REFLECT_AMOUNT));
                addToBot(new ApplyPowerAction(monster, this.owner, new GainStrengthPower(monster, REFLECT_AMOUNT), REFLECT_AMOUNT));
                break;
        }
    }

    /**
     * 更新描述。
     */
    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}
