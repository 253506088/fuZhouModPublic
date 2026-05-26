package basicmod.powers;

import basicmod.actions.AddMaskCardAction;
import basicmod.cards.masks.BaTeMask;
import basicmod.cards.masks.KaBoMask;
import basicmod.cards.masks.LaZuoMask;
import basicmod.cards.masks.LeiSuMask;
import basicmod.cards.masks.ManNiMask;
import basicmod.cards.masks.MingTaMask;
import basicmod.cards.masks.NiJiaMask;
import basicmod.cards.masks.SaMoMask;
import basicmod.cards.masks.YiKaMask;
import basicmod.helpers.MaskManager;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

import java.util.ArrayList;
import java.util.List;

/**
 * 面具能力基类。
 * 提供面具能力的基础功能，包括回合开始生成影子卡、挤出返还等机制。
 */
public abstract class BaseMaskPower extends BasePower {

    /** 升级层数 */
    public int upgradedAmount = 0;
    /** 面具堆叠记录 */
    private final ArrayList<MaskStackRecord> maskStackRecords = new ArrayList<>();
    /** 防止同一个面具能力实例在挤出流程中被重复返还 */
    private boolean evictionRefundHandled = false;
    /** 每层面具在被挤出时返还的兵团卡数量 */
    private static final int EVICT_SOLDIER_REFUND_PER_STACK = 2;

    public BaseMaskPower(String id, PowerType powerType, boolean isTurnBased, AbstractCreature owner, int amount) {
        super(id, powerType, isTurnBased, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(this.ID);
        if (powerStrings != null && powerStrings.DESCRIPTIONS != null && powerStrings.DESCRIPTIONS.length > 0) {
            this.name = powerStrings.NAME;
            this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1];
        }
        MaskManager.updateMaskOrbs();
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        MaskManager.onMaskApplied(this.owner, this.ID, this);
        MaskManager.updateMaskOrbs();
    }

    @Override
    public void onRemove() {
        super.onRemove();
        MaskManager.onMaskRemoved(this.ID);
        MaskManager.updateMaskOrbs();
    }

    /**
     * 获取关联的黑影兵团卡牌。
     *
     * @return 黑影兵团卡牌实例
     */
    public abstract AbstractCard getLinkedShadowKhanCard();

    /**
     * 挤出回调，返还面具卡和兵团卡。
     */
    public void onEvict() {
        onEvict(0);
    }

    /**
     * 记录应用的面具堆叠信息。
     *
     * @param stackCount 堆叠数量
     * @param upgraded 是否升级
     * @param permanentCostReductionCount 永久费用减免次数
     */
    public void recordAppliedMaskStacks(int stackCount, boolean upgraded, int permanentCostReductionCount) {
        if (stackCount <= 0) {
            return;
        }
        int reduction = Math.max(0, permanentCostReductionCount);
        for (int i = 0; i < stackCount; i++) {
            this.maskStackRecords.add(new MaskStackRecord(upgraded, reduction));
        }
    }

    /**
     * 挤出回调，返还面具卡和兵团卡，并应用费用减免。
     *
     * @param returnedMaskCostReduction 返还的面具费用减免次数
     */
    public void onEvict(int returnedMaskCostReduction) {
        if (evictionRefundHandled) {
            return;
        }
        evictionRefundHandled = true;

        this.flash();

        int reduction = Math.max(0, returnedMaskCostReduction);
        List<MaskStackRecord> snapshot = getMaskStackSnapshot();

        for (MaskStackRecord record : snapshot) {
            for (int i = 0; i < EVICT_SOLDIER_REFUND_PER_STACK; i++) {
                AbstractCard card = getLinkedShadowKhanCard();
                if (card != null) {
                    if (record.upgraded) {
                        card.upgrade();
                    }
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(card, 1));
                }
            }
        }

        for (MaskStackRecord record : snapshot) {
            AbstractCard maskCard = getLinkedMaskCard();
            if (maskCard != null) {
                if (record.upgraded) {
                    maskCard.upgrade();
                }
                int totalReduction = record.permanentCostReductionCount + reduction;
                maskCard.misc = totalReduction;
                if (totalReduction > 0) {
                    maskCard.modifyCostForCombat(-totalReduction);
                }
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(maskCard, 1));
            }
        }
        this.maskStackRecords.clear();
    }

    /**
     * 回合开始（抽牌后）触发，生成黑影兵团卡牌。
     */
    @Override
    public void atStartOfTurnPostDraw() {
        super.atStartOfTurnPostDraw();
        this.flash();
        // 鍔犲叆涓€涓煭鏆傜瓑寰咃紝閬垮厤鍥炲悎寮€濮嬫娊鐗屼笌濉炵墝鐨勬椂搴忓啿绐?
        AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.utility.WaitAction(0.1F));

        int regular = this.amount - this.upgradedAmount;
        if (regular > 0) {
            for (int i = 0; i < regular; i++) {
                AbstractCard card = getLinkedShadowKhanCard();
                if (card != null) {
                    AbstractDungeon.actionManager.addToBottom(new AddMaskCardAction(card));
                }
            }
        }
        if (this.upgradedAmount > 0) {
            for (int i = 0; i < this.upgradedAmount; i++) {
                AbstractCard card = getLinkedShadowKhanCard();
                if (card != null) {
                    card.upgrade();
                    AbstractDungeon.actionManager.addToBottom(new AddMaskCardAction(card));
                }
            }
        }
    }

    private AbstractCard getLinkedMaskCard() {
        switch (this.ID) {
            case "fuZhouMod:NiJiaPower":
                return new NiJiaMask();
            case "fuZhouMod:LaZuoPower":
                return new LaZuoMask();
            case "fuZhouMod:SaMoPower":
                return new SaMoMask();
            case "fuZhouMod:BaTePower":
                return new BaTeMask();
            case "fuZhouMod:KaBoPower":
                return new KaBoMask();
            case "fuZhouMod:LeiSuPower":
                return new LeiSuMask();
            case "fuZhouMod:ManNiPower":
                return new ManNiMask();
            case "fuZhouMod:MingTaPower":
                return new MingTaMask();
            case "fuZhouMod:YiKaPower":
                return new YiKaMask();
            default:
                return null;
        }
    }

    private ArrayList<MaskStackRecord> getMaskStackSnapshot() {
        int total = Math.max(0, this.amount);
        ArrayList<MaskStackRecord> snapshot = new ArrayList<>(this.maskStackRecords);

        while (snapshot.size() > total) {
            snapshot.remove(snapshot.size() - 1);
        }
        if (snapshot.size() == total) {
            return snapshot;
        }

        int upgradedTotal = Math.max(0, Math.min(this.upgradedAmount, total));
        int upgradedRecorded = 0;
        for (MaskStackRecord record : snapshot) {
            if (record.upgraded) {
                upgradedRecorded++;
            }
        }

        int missing = total - snapshot.size();
        int missingUpgraded = Math.max(0, Math.min(missing, upgradedTotal - upgradedRecorded));
        int missingRegular = missing - missingUpgraded;
        for (int i = 0; i < missingRegular; i++) {
            snapshot.add(new MaskStackRecord(false, 0));
        }
        for (int i = 0; i < missingUpgraded; i++) {
            snapshot.add(new MaskStackRecord(true, 0));
        }
        return snapshot;
    }

    private static class MaskStackRecord {
        private final boolean upgraded;
        private final int permanentCostReductionCount;

        private MaskStackRecord(boolean upgraded, int permanentCostReductionCount) {
            this.upgraded = upgraded;
            this.permanentCostReductionCount = Math.max(0, permanentCostReductionCount);
        }
    }
}
