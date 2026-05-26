package basicmod.powers;

import basicmod.BasicMod;
import basicmod.helpers.DemonQiDamageHelper;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.PowerBuffEffect;

public class SteamPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("SteamPower");
    private static final int MAX_SINGLE_DAMAGE = 40;
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private boolean skippedThisTurn = false;

    public SteamPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(POWER_ID, PowerType.DEBUFF, false, owner, source, amount);
        this.name = NAME;
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        if (!(this.owner instanceof AbstractMonster) || this.owner.isDeadOrEscaped()) {
            return;
        }

        AbstractMonster m = (AbstractMonster) this.owner;
        if (GrandMageBlessingPower.shouldConvertSteamToDamage(this.owner)) {
            flash();
            int dmg = Math.max(5, this.owner.currentHealth / 5);
            // 高温蒸汽被老爹改判为直接伤害后，仍需统一走“自身封顶 + 老爹封顶”链路。
            dmg = DemonQiDamageHelper.applyDemonQiDamageCaps(this.source, this.owner, POWER_ID, dmg, MAX_SINGLE_DAMAGE, "高温蒸汽回合开始");
            BasicMod.logger.debug("【高温蒸汽判定】目标拥有【大法师的庇佑】，不再干扰意图，改为造成伤害={}。目标={}", dmg, m.name);
            addToBot(new LoseHPAction(this.owner, this.source, dmg));
            return;
        }

        int roll = AbstractDungeon.aiRng.random(1, 8);
        BasicMod.logger.debug("【高温蒸汽判定】当前敌人: {} | 蒸汽层数: {} | 摇点结果: {}", m.name, this.amount, roll);

        if (roll <= this.amount) {
            BasicMod.logger.debug("【高温蒸汽判定】判定成功！拦截 {} 的本回合行动。", m.name);
            this.skippedThisTurn = true;

            flash();
            AbstractDungeon.effectList.add(new PowerBuffEffect(m.hb.cX, m.hb.cY, "蒸汽干扰！"));

            int baseDmg = basemod.ReflectionHacks.getPrivate(m, AbstractMonster.class, "intentBaseDmg");
            int multiAmt = basemod.ReflectionHacks.getPrivate(m, AbstractMonster.class, "intentMultiAmt");
            boolean isMulti = basemod.ReflectionHacks.getPrivate(m, AbstractMonster.class, "isMultiDmg");

            m.setMove((byte) -101, m.intent, baseDmg, multiAmt, isMulti);
            m.createIntent();
        } else {
            BasicMod.logger.debug("【高温蒸汽判定】判定失败。怪物将正常行动。");
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (!isPlayer) {
            if (this.skippedThisTurn && this.owner instanceof AbstractMonster && !this.owner.isDeadOrEscaped()) {
                AbstractMonster m = (AbstractMonster) this.owner;
                m.rollMove();
                m.createIntent();
                this.skippedThisTurn = false;
            }
            addToBot(new ReducePowerAction(this.owner, this.owner, this, 1));
        }
    }

    @Override
    public void updateDescription() {
        if (GrandMageBlessingPower.shouldConvertSteamToDamage(this.owner) && DESCRIPTIONS.length >= 5) {
            this.description = DESCRIPTIONS[3] + GrandMageBlessingPower.DEMON_QI_SINGLE_DAMAGE_CAP + DESCRIPTIONS[4];
            return;
        }
        int displayDeno = 8;
        int displayNum = this.amount;
        this.description = DESCRIPTIONS[0] + displayNum + DESCRIPTIONS[1] + displayDeno + DESCRIPTIONS[2];
    }
}
