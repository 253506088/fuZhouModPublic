package basicmod.powers;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

public class NothingLackingPower extends BasePower {
    public static final String POWER_ID = makeID("NothingLackingPower");
    private static final PowerType TYPE = PowerType.BUFF;
    private static final boolean TURN_BASED = false;

    private int energyGain;
    private int statGain;
    private boolean drawOnePerCardOnly;

    public NothingLackingPower(AbstractCreature owner, int energyGain, int statGain) {
        this(owner, energyGain, statGain, false);
    }

    public NothingLackingPower(AbstractCreature owner, int energyGain, int statGain, boolean drawOnePerCardOnly) {
        super(POWER_ID, TYPE, TURN_BASED, owner, 1);
        this.energyGain = energyGain;
        this.statGain = statGain;
        this.drawOnePerCardOnly = drawOnePerCardOnly;
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        // 刚获得 Power 时只处理持续效果，避免在原版能力遍历期间继续改能力列表
        if (!this.drawOnePerCardOnly) {
            replenishHand();
        }
    }

    @Override
    public void atStartOfTurn() {
        // 每回合开始加能量；基础版只在获得 Power 时给一次能量
        if (this.energyGain > 0 && !this.drawOnePerCardOnly) {
            addToBot(new GainEnergyAction(this.energyGain));
        }
    }

    @Override
    public void atStartOfTurnPostDraw() {
        // 回合开始抽牌后补满
        if (!this.drawOnePerCardOnly) {
            replenishHand();
        }
    }

    @Override
    public void onAfterUseCard(com.megacrit.cardcrawl.cards.AbstractCard card, com.megacrit.cardcrawl.actions.utility.UseCardAction action) {
        // 每出一张牌补满手牌
        if (this.drawOnePerCardOnly) {
            addToBot(new DrawCardAction(1));
        } else {
            replenishHand();
        }
    }

    // 核心逻辑：将手牌补到最大上限
    private void replenishHand() {
        if (AbstractDungeon.player != null) {
            int currentHandSize = AbstractDungeon.player.hand.size();
            int maxHandSize = BaseMod.MAX_HAND_SIZE;
            if (currentHandSize < maxHandSize) {
                // 使用 addToTop 确保优先执行，实现真正的“始终补满”
                addToTop(new DrawCardAction(maxHandSize - currentHandSize));
            }
        }
    }

    @Override
    public void updateDescription() {
        if (this.drawOnePerCardOnly) {
            this.description = "每打出 #b1 张牌，抽 #b1 张牌。获得 #b" + this.energyGain + " [E] 、 #b" + this.statGain + " 层 *力量 和 #b" + this.statGain + " 层 *敏捷 。";
            return;
        }
        this.description = DESCRIPTIONS[0] + this.energyGain + DESCRIPTIONS[1] + this.statGain + DESCRIPTIONS[2] + this.statGain + DESCRIPTIONS[3];
    }
}
