package basicmod.powers;

import basicmod.BasicMod;
import basicmod.monsters.GrandMageDad;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class GrandMageDamageCapPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("GrandMageDamageCapPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final int capPerTurn;
    private int takenThisTurn;
    private int perHitCap = -1;

    public GrandMageDamageCapPower(AbstractCreature owner, int capPerTurn) {
        super(POWER_ID, PowerType.BUFF, false, owner, capPerTurn);
        this.name = NAME;
        this.capPerTurn = capPerTurn;
        this.takenThisTurn = 0;
        updateAmount();
        updateDescription();
    }

    public void setPerHitCap(int cap) {
        this.perHitCap = cap;
        updateDescription();
        GrandMageDad.logDetail("【正气不灭】更新单次受伤上限：perHitCap=" + cap + "。");
    }

    @Override
    public void atStartOfTurn() {
        this.takenThisTurn = 0;
        updateAmount();
        updateDescription();
        GrandMageDad.logDetail("【正气不灭】在回合开始时重置累计承伤。当前本回合总承伤上限="
                + capPerTurn + "，单次受伤上限=" + perHitCap + "。");
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if (damageAmount <= 0) {
            return damageAmount;
        }
        int remainBefore = Math.max(0, capPerTurn - takenThisTurn);
        int adjusted = damageAmount;
        if (perHitCap > 0) {
            adjusted = Math.min(adjusted, perHitCap);
        }
        adjusted = Math.min(adjusted, remainBefore);
        takenThisTurn += Math.max(0, adjusted);
        boolean capJustFilled = remainBefore > 0 && adjusted >= remainBefore;
        if (capJustFilled && this.owner instanceof GrandMageDad) {
            GrandMageDad.logDetail("【正气不灭】本回合承伤上限已被打满，准备返还1个已封存符咒。");
            ((GrandMageDad) this.owner).onDamageCapFilled();
        }
        updateAmount();
        updateDescription();
        GrandMageDad.logDetail("【正气不灭】拦截伤害：原始伤害=" + damageAmount
                + "，来源=" + (info == null || info.owner == null ? "未知" : info.owner.name + "/" + info.owner.id)
                + "，回合剩余上限=" + remainBefore
                + "，单次上限=" + perHitCap
                + "，最终放行=" + adjusted
                + "，本回合累计已承伤=" + takenThisTurn + "。");
        return adjusted;
    }

    private void updateAmount() {
        this.amount = Math.max(0, capPerTurn - takenThisTurn);
    }

    @Override
    public void renderAmount(SpriteBatch sb, float x, float y, Color c) {
        if (this.amount == 0) {
            FontHelper.renderFontRightTopAligned(sb, FontHelper.powerAmountFont, "0", x, y, this.fontScale, c);
            return;
        }
        super.renderAmount(sb, x, y, c);
    }

    @Override
    public void updateDescription() {
        int remain = Math.max(0, capPerTurn - takenThisTurn);
        StringBuilder builder = new StringBuilder();
        if (perHitCap > 0) {
            builder.append(DESCRIPTIONS[0]).append(capPerTurn).append(DESCRIPTIONS[1]).append(remain).append(DESCRIPTIONS[2])
                    .append(DESCRIPTIONS[3]).append(perHitCap);
        } else {
            builder.append(DESCRIPTIONS[0]).append(capPerTurn).append(DESCRIPTIONS[1]).append(remain).append(DESCRIPTIONS[2]);
        }
        builder.append(DESCRIPTIONS[4]);
        String sealedSummary = this.owner instanceof GrandMageDad
                ? ((GrandMageDad) this.owner).getSealedTalismansSummary(DESCRIPTIONS[6])
                : DESCRIPTIONS[6];
        builder.append(DESCRIPTIONS[5]).append(sealedSummary);
        this.description = builder.toString();
    }
}
