package basicmod.actions;

import basicmod.cards.BaseCard;
import basicmod.cards.masks.TaLaMask;
import basicmod.helpers.MaskManager;
import basicmod.powers.BaseMaskPower;
import basicmod.powers.masks.TaLaPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.List;

/**
 * 剥离面具动作。
 * 选择一个面具能力剥离，移除该能力并返还对应的面具卡牌。
 */
public class PeelMaskAction extends AbstractGameAction {
    /** 玩家对象 */
    private final AbstractPlayer player;
    /** 是否升级 */
    private final boolean upgraded;

    /**
     * 构造函数。
     *
     * @param upgraded 是否升级
     */
    public PeelMaskAction(boolean upgraded) {
        this.player = AbstractDungeon.player;
        this.upgraded = upgraded;
        this.actionType = ActionType.SPECIAL;
        this.duration = Settings.ACTION_DUR_MED;
    }

    /**
     * 执行动作逻辑：选择面具能力，移除并返还对应卡牌。
     */
    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_MED) {
            CardGroup peelable = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractPower power : player.powers) {
                if (isPeelableMaskPower(power)) {
                    peelable.addToBottom(new MaskPowerPreviewCard(power));
                }
            }

            if (peelable.isEmpty()) {
                this.isDone = true;
                return;
            }

            AbstractDungeon.gridSelectScreen.open(peelable, 1, "选择1个要剥离的面具能力", false);
            tickDuration();
            return;
        }

        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard selected = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            if (selected instanceof MaskPowerPreviewCard) {
                String powerId = ((MaskPowerPreviewCard) selected).powerId;
                AbstractPower targetPower = player.getPower(powerId);
                if (targetPower != null) {
                    if (targetPower instanceof BaseMaskPower) {
                        ((BaseMaskPower) targetPower).onEvict(1);
                        addToBot(new RemoveSpecificPowerAction(player, player, targetPower));
                    } else if (TaLaPower.POWER_ID.equals(targetPower.ID)) {
                        int talaBaseCount = 1;
                        int talaUpgradedCount = 0;
                        List<TaLaPower.TaLaMaskRecord> peelRecords = null;
                        if (targetPower instanceof TaLaPower) {
                            TaLaPower taLaPower = (TaLaPower) targetPower;
                            peelRecords = taLaPower.getPeelMaskRecordsSnapshot();
                            talaBaseCount = taLaPower.getPeelBaseMaskCount();
                            talaUpgradedCount = taLaPower.getPeelUpgradedMaskCount();
                        }

                        int costReduction = 1;
                        int talaStacks = peelRecords == null
                                ? Math.max(0, talaBaseCount) + Math.max(0, talaUpgradedCount)
                                : peelRecords.size();
                        if (talaStacks <= 0) {
                            talaBaseCount = 1;
                            talaUpgradedCount = 0;
                            talaStacks = 1;
                        }

                        if (peelRecords != null) {
                            for (TaLaPower.TaLaMaskRecord record : peelRecords) {
                                AbstractCard talaMaskCard = new TaLaMask();
                                if (record.isUpgraded()) {
                                    talaMaskCard.upgrade();
                                }
                                int totalReduction = record.getPermanentCostReductionCount() + costReduction;
                                talaMaskCard.misc = totalReduction;
                                if (totalReduction > 0) {
                                    talaMaskCard.modifyCostForCombat(-totalReduction);
                                }
                                addToBot(new MakeTempCardInDiscardAction(talaMaskCard, 1));
                            }
                        } else {
                            for (int i = 0; i < talaBaseCount; i++) {
                                AbstractCard talaMaskCard = new TaLaMask();
                                if (costReduction > 0) {
                                    talaMaskCard.modifyCostForCombat(-costReduction);
                                }
                                addToBot(new MakeTempCardInDiscardAction(talaMaskCard, 1));
                            }

                            for (int i = 0; i < talaUpgradedCount; i++) {
                                AbstractCard talaMaskCard = new TaLaMask();
                                talaMaskCard.upgrade();
                                if (costReduction > 0) {
                                    talaMaskCard.modifyCostForCombat(-costReduction);
                                }
                                addToBot(new MakeTempCardInDiscardAction(talaMaskCard, 1));
                            }
                        }

                        for (int i = 0; i < talaStacks * 2; i++) {
                            TaLaPower.queueRandomShadowKhanCard();
                        }

                        addToBot(new RemoveSpecificPowerAction(player, player, targetPower));
                        addToBot(new AbstractGameAction() {
                            @Override
                            public void update() {
                                MaskManager.enforceCapacity(player);
                                this.isDone = true;
                            }
                        });
                    }
                }
            }
        }

        this.isDone = true;
    }

    /**
     * 判断能力是否可剥离（是面具能力或塔拉能力）。
     *
     * @param power 要检查的能力
     * @return 如果可剥离返回true
     */
    private boolean isPeelableMaskPower(AbstractPower power) {
        return power instanceof BaseMaskPower || TaLaPower.POWER_ID.equals(power.ID);
    }

    /**
     * 面具能力预览卡牌。
     * 用于在网格选择界面中显示面具能力的信息。
     */
    private static class MaskPowerPreviewCard extends AbstractCard {
        private final String powerId;
        private final String previewName;
        private final String previewDescription;

        private MaskPowerPreviewCard(AbstractPower power) {
            this(power.ID, power.name, BaseCard.normalizeCardDescriptionColors(buildPreviewDescription(power)));
        }

        private MaskPowerPreviewCard(String powerId, String name, String description) {
            super("MaskPowerPreview:" + powerId,
                    name,
                    null,
                    -2,
                    description,
                    CardType.SKILL,
                    CardColor.COLORLESS,
                    CardRarity.SPECIAL,
                    CardTarget.NONE);
            this.powerId = powerId;
            this.previewName = name;
            this.previewDescription = description;
            this.baseDamage = 0;
            this.baseBlock = 0;
            this.initializeDescription();
        }

        @Override
        public void upgrade() {
        }

        @Override
        public void use(com.megacrit.cardcrawl.characters.AbstractPlayer abstractPlayer,
                        com.megacrit.cardcrawl.monsters.AbstractMonster abstractMonster) {
        }

        @Override
        public AbstractCard makeCopy() {
            return new MaskPowerPreviewCard(this.powerId, this.previewName, this.previewDescription);
        }

        private static String buildPreviewDescription(AbstractPower power) {
            if (power instanceof TaLaPower) {
                return buildTaLaPeelPreviewDescription((TaLaPower) power);
            }
            return power.description == null ? "" : power.description;
        }

        private static String buildTaLaPeelPreviewDescription(TaLaPower taLaPower) {
            int baseCount = Math.max(0, taLaPower.getPeelBaseMaskCount());
            int upgradedCount = Math.max(0, taLaPower.getPeelUpgradedMaskCount());
            int totalCount = baseCount + upgradedCount;
            if (totalCount <= 0) {
                baseCount = 1;
                totalCount = 1;
                upgradedCount = 0;
            }
            int randomShadowCount = totalCount * 2;

            if (Settings.language == Settings.GameLanguage.ZHS) {
                if (baseCount > 0 && upgradedCount > 0) {
                    return "剥离时返还 #b" + baseCount + " 张基础塔拉面具与 #b" + upgradedCount + " 张升级塔拉面具。 NL 随机获得 #b" + randomShadowCount + " 张黑影兵团牌。";
                }
                if (upgradedCount > 0) {
                    return "剥离时返还 #b" + upgradedCount + " 张升级塔拉面具。 NL 随机获得 #b" + randomShadowCount + " 张黑影兵团牌。";
                }
                return "剥离时返还 #b" + totalCount + " 张基础塔拉面具。 NL 随机获得 #b" + randomShadowCount + " 张黑影兵团牌。";
            }

            if (baseCount > 0 && upgradedCount > 0) {
                return "On peel, return #b" + baseCount + " base Tarakudo Mask card(s) and #b" + upgradedCount + " upgraded Tarakudo Mask card(s). NL Gain #b" + randomShadowCount + " random Shadowkhan card(s).";
            }
            if (upgradedCount > 0) {
                return "On peel, return #b" + upgradedCount + " upgraded Tarakudo Mask card(s). NL Gain #b" + randomShadowCount + " random Shadowkhan card(s).";
            }
            return "On peel, return #b" + totalCount + " base Tarakudo Mask card(s). NL Gain #b" + randomShadowCount + " random Shadowkhan card(s).";
        }
    }
}
