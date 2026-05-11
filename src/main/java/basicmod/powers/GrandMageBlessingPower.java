package basicmod.powers;

import basicmod.BasicMod;
import basicmod.helpers.DemonQiDamageHelper;
import basicmod.monsters.GrandMageDad;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GrandMageBlessingPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("GrandMageBlessingPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static final int BLOCKS_PER_TURN = 2;
    public static final int DEMON_QI_SINGLE_DAMAGE_CAP = 30;

    private static final Set<String> CAPPED_POWER_IDS = new HashSet<>(Arrays.asList(
            BurningPower.POWER_ID,
            BurningHeartPower.POWER_ID,
            FrostbitePower.POWER_ID,
            FrostHellPower.POWER_ID,
            HeavyMirePower.POWER_ID,
            SteamPower.POWER_ID
    ));

    public GrandMageBlessingPower(AbstractCreature owner) {
        super(POWER_ID, PowerType.BUFF, false, owner, BLOCKS_PER_TURN);
        this.name = NAME;
        this.amount = BLOCKS_PER_TURN;
        this.canGoNegative = false;
        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        this.amount = BLOCKS_PER_TURN;
        updateDescription();
        GrandMageDad.logDetail("【大法师的庇佑】在回合开始时重置，本回合重新可抵消前两次异常状态。");
    }

    public boolean canBlockDebuff() {
        return this.amount > 0;
    }

    public void consumeBlock() {
        if (this.amount <= 0) {
            return;
        }
        this.amount -= 1;
        flash();
        updateDescription();
        GrandMageDad.logDetail("【大法师的庇佑】已触发：本回合异常状态抵消次数剩余=" + this.amount + "。");
    }

    public static boolean hasBlessing(AbstractCreature target) {
        return target != null && target.hasPower(POWER_ID);
    }

    public static boolean shouldCapDemonQiDamage(AbstractCreature target, String powerId) {
        return hasBlessing(target) && CAPPED_POWER_IDS.contains(powerId);
    }

    public static int capDemonQiDamage(AbstractCreature target, String powerId, int damageAmount, String context) {
        return capDemonQiDamage(target, null, powerId, damageAmount, context);
    }

    /**
     * 对命中的恶魔异常伤害施加【大法师的庇佑】上限。
     * 若来源处于【解除限制】状态，则这一层上限也会被一并跳过。
     *
     * @param target 伤害目标
     * @param source 伤害来源
     * @param powerId 异常能力 ID
     * @param damageAmount 当前待处理伤害
     * @param context 日志场景
     * @return 处理后的伤害值
     */
    public static int capDemonQiDamage(AbstractCreature target, AbstractCreature source, String powerId, int damageAmount, String context) {
        if (damageAmount <= 0 || !shouldCapDemonQiDamage(target, powerId)) {
            return damageAmount;
        }
        if (DemonQiDamageHelper.shouldIgnoreDamageCap(source)) {
            GrandMageDad.logDetail("【大法师的庇佑】检测到【解除限制】，本次不再压制魔气伤害：来源="
                    + powerId + "，场景=" + context + "，伤害=" + damageAmount + "。");
            return damageAmount;
        }
        int capped = Math.min(damageAmount, DEMON_QI_SINGLE_DAMAGE_CAP);
        GrandMageDad.logDetail("【大法师的庇佑】压制魔气伤害：来源=" + powerId
                + "，场景=" + context
                + "，原始伤害=" + damageAmount
                + "，封顶后=" + capped + "。");
        return capped;
    }

    public static boolean shouldConvertSteamToDamage(AbstractCreature target) {
        return hasBlessing(target);
    }

    @Override
    public void updateDescription() {
        if (this.amount > 0) {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + DEMON_QI_SINGLE_DAMAGE_CAP + DESCRIPTIONS[2];
        } else {
            this.description = DESCRIPTIONS[3] + DEMON_QI_SINGLE_DAMAGE_CAP + DESCRIPTIONS[4];
        }
    }
}
