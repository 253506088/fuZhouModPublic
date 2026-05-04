package basicmod.relics;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

public class PanKuRewardEffect extends AbstractGameEffect {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:PanKuUI");
    private enum Phase {
        CHOOSE_REWARD,
        CHOOSE_TARGET
    }

    private final PanKuRewardItem rewardItem;
    private Phase phase = Phase.CHOOSE_REWARD;

    public PanKuRewardEffect(PanKuRewardItem rewardItem) {
        this.rewardItem = rewardItem;
        this.duration = 0.1f;
    }

    @Override
    public void update() {
        if (phase == Phase.CHOOSE_REWARD) {
            updateRewardChoice();
        } else {
            updateEnchantTargetChoice();
        }
    }

    private void updateRewardChoice() {
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard chosen = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();

            if (chosen == rewardItem.demonCard) {
                AbstractDungeon.effectsQueue.add(new ShowCardAndObtainEffect(chosen.makeCopy(), Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f));
                completeReward(rewardItem.demonCard);
                return;
            }

            if (chosen == rewardItem.storageDummyCard) {
                PanKuBox panKuBox = (PanKuBox) AbstractDungeon.player.getRelic(PanKuBox.ID);
                if (panKuBox != null) {
                    panKuBox.addStoredQi(rewardItem.demonCard.cardID);
                    panKuBox.flash();
                    completeReward(null);
                } else {
                    // 兜底：若异常丢失宝盒，则直接给恶魔卡，避免卡住奖励流程
                    AbstractDungeon.effectsQueue.add(new ShowCardAndObtainEffect(rewardItem.demonCard.makeCopy(), Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f));
                    completeReward(null);
                }
                return;
            }

            if (chosen == rewardItem.enchantDummyCard) {
                CardGroup targets = PanKuDemonQiHelper.getEnchantTargets(rewardItem.qiModifier);
                if (targets.isEmpty()) {
                    AbstractDungeon.effectsQueue.add(new com.megacrit.cardcrawl.vfx.ThoughtBubble(
                            AbstractDungeon.player.dialogX,
                            AbstractDungeon.player.dialogY,
                            2.5f,
                            uiStrings.TEXT[19],
                            true));
                    rewardItem.openChoiceScreen();
                } else {
                    AbstractDungeon.dynamicBanner.hide();
                    AbstractDungeon.gridSelectScreen.open(targets, 1, uiStrings.TEXT[20] + rewardItem.enchantDummyCard.name, false, false, true, false);
                    phase = Phase.CHOOSE_TARGET;
                }
            }
        } else if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.GRID) {
            // 取消选择，恢复奖励点击状态
            this.isDone = true;
        }
    }

    private void updateEnchantTargetChoice() {
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard target = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();

            CardModifierManager.addModifier(target, rewardItem.qiModifier);
            target.superFlash();
            completeReward(null);
        } else if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.GRID) {
            // 取消了附魔目标选择，返回奖励分支选择
            phase = Phase.CHOOSE_REWARD;
            rewardItem.openChoiceScreen();
        }
    }

    private void completeReward(AbstractCard pendingDemonCard) {
        PanKuBox panKuBox = (PanKuBox) AbstractDungeon.player.getRelic(PanKuBox.ID);
        if (panKuBox != null) {
            panKuBox.clearPendingReward();
            if (pendingDemonCard == null) {
                panKuBox.updateCounter();
            } else {
                panKuBox.updateCounterWithPendingDemon(pendingDemonCard);
            }
        }
        rewardItem.isDone = true;
        rewardItem.ignoreReward = true;
        AbstractDungeon.combatRewardScreen.rewards.remove(rewardItem);
        AbstractDungeon.combatRewardScreen.positionRewards();
        this.isDone = true;
    }

    @Override
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch sb) {
    }

    @Override
    public void dispose() {
    }
}
