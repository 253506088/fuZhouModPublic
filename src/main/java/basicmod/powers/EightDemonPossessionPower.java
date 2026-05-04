package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ChokePower;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import com.megacrit.cardcrawl.powers.CorpseExplosionPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.powers.watcher.BlockReturnPower;
import com.megacrit.cardcrawl.powers.watcher.MarkPower;

import java.util.ArrayList;

public class EightDemonPossessionPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("EightDemonPossessionPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final int TRIGGERS_PER_TURN = 2;
    private static final int DEBUFF_AMOUNT = 2;
    private static final int DEBUFF_TYPES = 18;

    public EightDemonPossessionPower(AbstractCreature owner) {
        super(POWER_ID, PowerType.BUFF, false, owner, owner, TRIGGERS_PER_TURN);
        this.name = NAME;
        this.canGoNegative = false;
        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        this.amount = TRIGGERS_PER_TURN;
        updateDescription();
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (this.amount <= 0 || card == null || card.type != AbstractCard.CardType.ATTACK) {
            return;
        }

        ArrayList<AbstractMonster> targets = getTargets(card, action);
        if (targets.isEmpty()) {
            return;
        }

        int debuffIndex = AbstractDungeon.cardRandomRng.random(DEBUFF_TYPES - 1);
        for (AbstractMonster target : targets) {
            AbstractPower power = createDebuff(target, debuffIndex);
            if (power != null) {
                addToBot(new ApplyPowerAction(target, this.owner, power, DEBUFF_AMOUNT));
            }
        }

        this.amount--;
        flash();
        updateDescription();
    }

    private ArrayList<AbstractMonster> getTargets(AbstractCard card, UseCardAction action) {
        ArrayList<AbstractMonster> targets = new ArrayList<>();
        if (card.target == AbstractCard.CardTarget.ALL_ENEMY || card.target == AbstractCard.CardTarget.ALL) {
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                if (!monster.isDeadOrEscaped()) {
                    targets.add(monster);
                }
            }
        } else if (action != null && action.target instanceof AbstractMonster) {
            AbstractMonster monster = (AbstractMonster) action.target;
            if (!monster.isDeadOrEscaped()) {
                targets.add(monster);
            }
        }
        return targets;
    }

    private AbstractPower createDebuff(AbstractMonster target, int index) {
        switch (index) {
            case 0:
                return new BurningPower(target, this.owner, DEBUFF_AMOUNT);
            case 1:
                return new SoakedPower(target, this.owner, DEBUFF_AMOUNT);
            case 2:
                return new WindCatalystPower(target, this.owner, DEBUFF_AMOUNT);
            case 3:
                return new EarthBindPower(target, this.owner, DEBUFF_AMOUNT);
            case 4:
                return new FearPower(target, this.owner, DEBUFF_AMOUNT);
            case 5:
                return new SteamPower(target, this.owner, DEBUFF_AMOUNT);
            case 6:
                return new FrostbitePower(target, this.owner, DEBUFF_AMOUNT);
            case 7:
                return new BurningHeartPower(target, this.owner, DEBUFF_AMOUNT);
            case 8:
                return new FrostHellPower(target, this.owner, DEBUFF_AMOUNT);
            case 9:
                return new HeavyMirePower(target, this.owner, DEBUFF_AMOUNT);
            case 10:
                return new WeakPower(target, DEBUFF_AMOUNT, false);
            case 11:
                return new VulnerablePower(target, DEBUFF_AMOUNT, false);
            case 12:
                return new ConstrictedPower(target, this.owner, DEBUFF_AMOUNT);
            case 13:
                return new MarkPower(target, DEBUFF_AMOUNT);
            case 14:
                return new BlockReturnPower(target, DEBUFF_AMOUNT);
            case 15:
                return new ChokePower(target, DEBUFF_AMOUNT);
            case 16:
                CorpseExplosionPower corpseExplosionPower = new CorpseExplosionPower(target);
                corpseExplosionPower.amount = DEBUFF_AMOUNT;
                corpseExplosionPower.updateDescription();
                return corpseExplosionPower;
            case 17:
                return new PoisonPower(target, this.owner, DEBUFF_AMOUNT);
            default:
                return new EarthBindPower(target, this.owner, DEBUFF_AMOUNT);
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}
