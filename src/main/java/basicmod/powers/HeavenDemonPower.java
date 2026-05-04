package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;

/**
 * 天空恶魔能力：提供高额伤害数值限制，并赋予攻击时施加恐惧的概率。
 */
public class HeavenDemonPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("HeavenDemonPower");
    private static final int MAX_BASE_STATUS_DAMAGE = 20;
    private static final int MAX_EVOLVED_STATUS_DAMAGE = 40;
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final Logger logger = LogManager.getLogger(HeavenDemonPower.class.getName());

    public HeavenDemonPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, source, amount);
        this.canGoNegative = false;
        updateDescription();
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.type == DamageInfo.DamageType.NORMAL && target != this.owner && !target.isDeadOrEscaped()) {
            int statusCount = 0;

            // 检查四种异常并结算伤害与消耗
            // 灼烧
            if (target.hasPower(BurningPower.POWER_ID)) {
                statusCount++;
                int dmg = Math.min(MAX_BASE_STATUS_DAMAGE, Math.max(3, target.currentHealth / 8));
                dmg = GrandMageBlessingPower.capDemonQiDamage(target, BurningPower.POWER_ID, dmg, "天空恶魔额外触发灼烧");
                addToBot(new LoseHPAction(target, this.owner, dmg));
                addToBot(new ReducePowerAction(target, this.owner, BurningPower.POWER_ID, 1));
            }
            // 灼心
            if (target.hasPower(BurningHeartPower.POWER_ID)) {
                statusCount++;
                int dmg = Math.min(MAX_EVOLVED_STATUS_DAMAGE, Math.max(5, target.currentHealth / 5));
                dmg = GrandMageBlessingPower.capDemonQiDamage(target, BurningHeartPower.POWER_ID, dmg, "天空恶魔额外触发灼心");
                addToBot(new LoseHPAction(target, this.owner, dmg));
                addToBot(new ReducePowerAction(target, this.owner, BurningHeartPower.POWER_ID, 1));
            }
            // 冻伤
            if (target.hasPower(FrostbitePower.POWER_ID)) {
                statusCount++;
                int dmg = Math.min(MAX_BASE_STATUS_DAMAGE, Math.max(3, target.currentHealth / 8));
                dmg = GrandMageBlessingPower.capDemonQiDamage(target, FrostbitePower.POWER_ID, dmg, "天空恶魔额外触发冻伤");
                addToBot(new LoseHPAction(target, this.owner, dmg));
                addToBot(new ReducePowerAction(target, this.owner, FrostbitePower.POWER_ID, 1));
            }
            // 冰狱 (FrostHellPower)
            if (target.hasPower(FrostHellPower.POWER_ID)) {
                statusCount++;
                int dmg = Math.min(MAX_EVOLVED_STATUS_DAMAGE, Math.max(5, target.currentHealth / 5));
                dmg = GrandMageBlessingPower.capDemonQiDamage(target, FrostHellPower.POWER_ID, dmg, "天空恶魔额外触发冰狱");
                addToBot(new LoseHPAction(target, this.owner, dmg));
                addToBot(new ReducePowerAction(target, this.owner, FrostHellPower.POWER_ID, 1));
            }

            // 恐惧判定：基础 1/8，每有一种相关异常 +1/8
            int probNumerator = 1 + statusCount; 
            int roll = AbstractDungeon.cardRandomRng.random(1, 8);
            
            if (roll <= probNumerator) {
                flash();
                logger.info("【天空恶魔】判定成功！概率：" + probNumerator + "/8，目标：" + target.name);
                addToBot(new ApplyPowerAction(target, this.owner, new FearPower(target, this.owner, 1), 1));
            }
        }
    }


    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
