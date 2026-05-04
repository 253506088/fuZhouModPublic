package basicmod.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static basicmod.BasicMod.makeID;

public class MonkeyTalismanPower extends BasePower {
    public static final String POWER_ID = makeID("MonkeyTalismanPower");
    private boolean upgraded;

    public MonkeyTalismanPower(AbstractCreature owner, int amount, boolean upgraded) {
        super(POWER_ID, PowerType.BUFF, true, owner, amount);
        this.upgraded = upgraded;
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType damageType) {
        if (damageType == DamageInfo.DamageType.NORMAL) {
            float multiplier = upgraded ? 1.50f : 1.25f;
            return damage * multiplier;
        }
        return damage;
    }

    @Override
    public void atEndOfRound() {
        if (this.amount <= 1) {
            addToBot(new RemoveSpecificPowerAction(owner, owner, this));
        } else {
            this.amount--;
            updateDescription();
        }
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null && info.owner != this.owner && info.output > 0) {
            if (info.owner instanceof AbstractMonster) {
                AbstractMonster m = (AbstractMonster) info.owner;
                if (m.intent == AbstractMonster.Intent.ATTACK || m.intent == AbstractMonster.Intent.ATTACK_BUFF || 
                    m.intent == AbstractMonster.Intent.ATTACK_DEBUFF || m.intent == AbstractMonster.Intent.ATTACK_DEFEND) {
                    
                    this.flash();
                    addToTop(new DamageAction(m, new DamageInfo(this.owner, info.output * 2, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                }
            }
        }
        return damageAmount;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
