package basicmod.ui;

import basemod.helpers.CardModifierManager;
import basicmod.modifiers.demons.AbstractDemonQiModifier;
import basicmod.relics.PanKuBox;
import basicmod.relics.PanKuDemonQiHelper;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PanKuCampfireEnchantEffect extends AbstractGameEffect {
    private enum Phase {
        CHOOSE_QI,
        CHOOSE_TARGET
    }
    private static final String CANCEL_TOKEN = "__PANKU_CANCEL__";
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:PanKuUI");

    private final PanKuBox panKuBox;
    private final Map<UUID, String> selectedQiMap = new HashMap<>();
    private Phase phase = Phase.CHOOSE_QI;
    private String selectedQiCardID;
    private AbstractDemonQiModifier selectedModifier;

    public PanKuCampfireEnchantEffect(PanKuBox panKuBox) {
        this.panKuBox = panKuBox;
        this.duration = 0.1f;
        openStoredQiSelection();
    }

    @Override
    public void update() {
        if (phase == Phase.CHOOSE_QI) {
            updateStoredQiSelection();
        } else {
            updateTargetSelection();
        }
    }

    private void openStoredQiSelection() {
        selectedQiMap.clear();
        AbstractDungeon.gridSelectScreen.selectedCards.clear();

        CardGroup storedQiChoices = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        AbstractCard cancelSourceDemon = null;

        for (String storedCardID : panKuBox.getStoredQiCardIDs()) {
            AbstractCard demonCard = PanKuDemonQiHelper.getDemonCardCopyByID(storedCardID);
            if (demonCard == null) {
                continue;
            }

            if (cancelSourceDemon == null) {
                cancelSourceDemon = demonCard;
            }

            AbstractDemonQiModifier modifier = PanKuDemonQiHelper.createModifierByDemonCard(demonCard);
            if (modifier == null) {
                continue;
            }

            boolean canEnchantNow = PanKuDemonQiHelper.hasEnchantTarget(modifier);
            AbstractCard preview = PanKuDemonQiHelper.createCampfireStoredQiPreviewCard(demonCard, canEnchantNow);
            selectedQiMap.put(preview.uuid, storedCardID);
            storedQiChoices.addToBottom(preview);
        }

        // 明确提供“返回营火”按钮，避免只有不可附魔魔气时被困住
        AbstractCard cancelCard = PanKuDemonQiHelper.createCampfireCancelPreviewCard(cancelSourceDemon);
        selectedQiMap.put(cancelCard.uuid, CANCEL_TOKEN);
        storedQiChoices.addToBottom(cancelCard);

        if (storedQiChoices.isEmpty()) {
            cancelAndReturnToCampfire();
            return;
        }

        AbstractDungeon.gridSelectScreen.open(storedQiChoices, 1, uiStrings.TEXT[18], false, false, true, false);
        phase = Phase.CHOOSE_QI;
    }

    private void updateStoredQiSelection() {
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard selectedQi = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();

            String qiCardID = selectedQiMap.get(selectedQi.uuid);
            if (qiCardID == null || CANCEL_TOKEN.equals(qiCardID)) {
                cancelAndReturnToCampfire();
                return;
            }

            AbstractDemonQiModifier modifier = PanKuDemonQiHelper.createModifierByCardID(qiCardID);
            CardGroup targets = PanKuDemonQiHelper.getEnchantTargets(modifier);
            if (modifier == null || targets.isEmpty()) {
                AbstractDungeon.effectsQueue.add(new com.megacrit.cardcrawl.vfx.ThoughtBubble(
                        AbstractDungeon.player.dialogX,
                        AbstractDungeon.player.dialogY,
                        2.5f,
                        uiStrings.TEXT[21],
                        true));
                openStoredQiSelection();
                return;
            }

            selectedQiCardID = qiCardID;
            selectedModifier = modifier;
            AbstractDungeon.gridSelectScreen.open(targets, 1, uiStrings.TEXT[20] + selectedQi.name, false, false, true, false);
            phase = Phase.CHOOSE_TARGET;
            return;
        }

        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.GRID) {
            // 在选择魔气阶段取消：返回营火，不消耗休息机会
            cancelAndReturnToCampfire();
        }
    }

    private void updateTargetSelection() {
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard target = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();

            CardModifierManager.addModifier(target, selectedModifier);
            target.superFlash();
            panKuBox.removeStoredQi(selectedQiCardID);
            panKuBox.flash();

            CardCrawlGame.metricData.addCampfireChoiceData("PANKU_ENCHANT", target.getMetricID());
            consumeRestAndFinish();
            return;
        }

        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.GRID) {
            // 在选择附魔目标阶段取消：返回营火，不消耗休息机会
            cancelAndReturnToCampfire();
        }
    }

    private void cancelAndReturnToCampfire() {
        AbstractDungeon.gridSelectScreen.selectedCards.clear();
        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID) {
            AbstractDungeon.closeCurrentScreen();
        }

        AbstractRoom room = AbstractDungeon.getCurrRoom();
        room.phase = AbstractRoom.RoomPhase.INCOMPLETE;
        if (room instanceof RestRoom) {
            RestRoom restRoom = (RestRoom) room;
            if (restRoom.campfireUI != null) {
                restRoom.campfireUI.reopen();
            }
        }
        this.isDone = true;
    }

    private void consumeRestAndFinish() {
        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID) {
            AbstractDungeon.closeCurrentScreen();
        }

        AbstractRoom.waitTimer = 0.0f;
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
        if (AbstractDungeon.getCurrRoom() instanceof RestRoom) {
            ((RestRoom) AbstractDungeon.getCurrRoom()).fadeIn();
        }
        this.isDone = true;
    }

    @Override
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch sb) {
    }

    @Override
    public void dispose() {
    }
}
