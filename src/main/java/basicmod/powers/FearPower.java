package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 恐惧能力：让敌人变怂（叠甲）。
 * 这里的逻辑是：怪物回合开始时强制获得护甲，并跳过原始动作。
 */
public class FearPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("FearPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public FearPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(POWER_ID, PowerType.DEBUFF, false, owner, source, amount);
        this.name = NAME;
        this.canGoNegative = false;
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        if (this.owner instanceof AbstractMonster) {
            AbstractMonster m = (AbstractMonster)this.owner;
            // 通过补丁拦截 rollMove 来固定意图图标
            m.rollMove();
            m.createIntent(); 
        }
    }

    @Override
    public void atStartOfTurn() {
        if (!this.owner.isPlayer && this.owner instanceof AbstractMonster) {
            AbstractMonster m = (AbstractMonster) this.owner;
            // 为怪兽添加格挡：第一层 15，第二层 20，第三/四层 25（按层数倍增）
            int blockAmount = getBlockAmount();
            addToBot(new com.megacrit.cardcrawl.actions.common.GainBlockAction(m, m, blockAmount));
            
            // 移除了这里的 addToTop(RemoveSpecificPowerAction)，改为由补丁在执行完 takeTurn 后移除。
        }
    }

    private int getBlockAmount() {
        // 第一层(Act1)为 15，后续每层 +5，按用户要求第四层（或第三层后）封顶 25 或继续增长
        // 采用公式：15 + (min(3, actNum) - 1) * 5
        int base = 15;
        int act = 1;
        if (AbstractDungeon.id != null) {
            act = AbstractDungeon.actNum;
        }
        return (base + (Math.min(3, act) - 1) * 5) * this.amount;
    }

    @Override
    public void updateDescription() {
        int block = getBlockAmount();
        this.description = DESCRIPTIONS[0] + " NL 回合开始时获得 #b" + block + " 点 #y格挡 。";
    }
}
