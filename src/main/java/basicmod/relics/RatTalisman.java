package basicmod.relics;

import basicmod.actions.RatTalismanReplaceAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;

import static basicmod.BasicMod.makeID;

/**
 * 鼠符咒
 * 一回合一次：摸牌后，若手牌中有 状态 或 诅咒 牌，则选择其中1张，将其替换为1张0费且消耗的攻击牌。
 * 若回合开始摸牌未触发，后续本回合内因抽牌或效果加入手牌时仍可触发。
 */
public class RatTalisman extends BaseRelic {
    public static final String NAME = "RatTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:RatTalismanUI");
    public static final String SELECT_PROMPT = uiStrings.TEXT[0];
    private static final int COOLDOWN_TURNS = 2;

    // 每回合只允许成功替换一次
    private boolean usedThisTurn = false;
    // 防止同一时刻重复加入检测动作
    private boolean checkQueued = false;

    public RatTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void onEquip() {
        super.onEquip();

        if (AbstractDungeon.player instanceof basicmod.character.ShengZhuCustomPlayer) {
            basicmod.character.ShengZhuCustomPlayer shengZhu = (basicmod.character.ShengZhuCustomPlayer) AbstractDungeon.player;
            shengZhu.enableIdleAnimation();
            shengZhu.img = basicmod.util.TextureLoader
                    .getTexture(basicmod.BasicMod.imagePath("character/shengzhu/main.png"));
        }

        // 如果在战斗中获得（例如事件或指令），并且玩家是圣主且处于石像状态，解除石像惩罚并给予奖励
        if (AbstractDungeon.player != null && canInteractInCombat()) {
            if (AbstractDungeon.player.hasPower(basicmod.powers.ShenZhuStatuePower.POWER_ID)) {
                // 移除石像状态的 Power 及对应的特效
                addToBot(new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(
                        AbstractDungeon.player, AbstractDungeon.player, basicmod.powers.ShenZhuStatuePower.POWER_ID));

                // 给予复苏能力
                addToBot(new com.megacrit.cardcrawl.actions.common.ApplyPowerAction(
                        AbstractDungeon.player, AbstractDungeon.player,
                        new basicmod.powers.ShenZhuRevivedPower(AbstractDungeon.player)));
            }
        }

        // 刷新所有符咒的描述以显示共鸣状态
        if (AbstractDungeon.player != null && AbstractDungeon.player.relics != null) {
            for (com.megacrit.cardcrawl.relics.AbstractRelic r : AbstractDungeon.player.relics) {
                if (r instanceof BaseRelic) {
                    ((BaseRelic) r).refreshDescription();
                }
            }
        }
    }

    @Override
    public void onUnequip() {
        super.onUnequip();
        if (AbstractDungeon.player instanceof basicmod.character.ShengZhuCustomPlayer) {
            basicmod.character.ShengZhuCustomPlayer shengZhu = (basicmod.character.ShengZhuCustomPlayer) AbstractDungeon.player;
            shengZhu.disableIdleAnimation();
            shengZhu.img = basicmod.util.TextureLoader
                    .getTexture(basicmod.BasicMod.imagePath("character/shengzhu/statue.png"));
        }

        // 如果在战斗中失去，移除复苏能力，变回石像
        if (AbstractDungeon.player != null && canInteractInCombat()) {
            if (AbstractDungeon.player.hasPower(basicmod.powers.ShenZhuRevivedPower.POWER_ID)) {
                addToBot(new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(
                        AbstractDungeon.player, AbstractDungeon.player, basicmod.powers.ShenZhuRevivedPower.POWER_ID));

                addToBot(new com.megacrit.cardcrawl.actions.common.ApplyPowerAction(
                        AbstractDungeon.player, AbstractDungeon.player,
                        new basicmod.powers.ShenZhuStatuePower(AbstractDungeon.player)));
            }
        }

        // 刷新所有符咒的描述以移除共鸣状态
        if (AbstractDungeon.player != null && AbstractDungeon.player.relics != null) {
            // 由于 relicId 是 final 无法修改，改用列表脱离方案
            int index = AbstractDungeon.player.relics.indexOf(this);
            if (index != -1) {
                AbstractDungeon.player.relics.remove(index);
                for (com.megacrit.cardcrawl.relics.AbstractRelic r : AbstractDungeon.player.relics) {
                    if (r instanceof BaseRelic) {
                        ((BaseRelic) r).refreshDescription();
                    }
                }
                // 刷新完后原地放回，保证后续正常删除流程
                AbstractDungeon.player.relics.add(index, this);
            }
        }
    }

    @Override
    public void atPreBattle() {
        // 每场战斗开始时重置状态
        usedThisTurn = false;
        checkQueued = false;
        this.counter = 0;
        this.grayscale = false;
    }

    @Override
    public void atTurnStart() {
        // 每回合开始时重置触发次数
        usedThisTurn = false;
        checkQueued = false;
        if (this.counter > 0) {
            this.counter--;
            if (this.counter == 0) {
                this.grayscale = false;
            }
        }
    }

    @Override
    public void atTurnStartPostDraw() {
        // 起手摸牌后立刻检测一次
        queueReplaceCheck();
    }

    @Override
    public void update() {
        super.update();
        // 覆盖后续抽牌和直接加入手牌的情况
        if (!checkQueued && canTriggerNow() && hasReplaceableCardInHand()) {
            queueReplaceCheck();
        }
    }

    @Override
    public void onVictory() {
        usedThisTurn = false;
        checkQueued = false;
        this.counter = -1;
        this.grayscale = false;
    }

    // 返回当前手牌中可被替换的诅咒牌/状态牌
    public ArrayList<AbstractCard> getReplaceCandidates() {
        ArrayList<AbstractCard> candidates = new ArrayList<>();
        if (AbstractDungeon.player == null || AbstractDungeon.player.hand == null) {
            return candidates;
        }

        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (isReplaceable(card)) {
                candidates.add(card);
            }
        }
        return candidates;
    }

    // 执行真正的替换逻辑，成功返回true
    public boolean tryReplaceCard(AbstractCard toReplace) {
        if (usedThisTurn || this.counter > 0 || toReplace == null || AbstractDungeon.player == null
                || AbstractDungeon.player.hand == null) {
            return false;
        }
        if (!AbstractDungeon.player.hand.group.contains(toReplace) || !isReplaceable(toReplace)) {
            return false;
        }

        usedThisTurn = true;
        checkQueued = false;
        this.counter = COOLDOWN_TURNS;
        this.grayscale = true;
        this.flash();

        AbstractDungeon.player.hand.removeCard(toReplace);
        AbstractDungeon.player.hand.refreshHandLayout();
        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));

        // 直接把配置好的卡牌实例加入手牌，不使用 MakeTempCardInHandAction
        // 原因：MakeTempCardInHandAction 内部调用 makeStatEquivalentCopy()，该方法不会复制 exhaust 字段
        AbstractCard replacement = createReplacementAttack();
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (AbstractDungeon.player.hand.size() < 10) {
                    AbstractDungeon.player.hand.addToTop(replacement);
                    AbstractDungeon.player.hand.refreshHandLayout();
                } else {
                    // 手牌满了则放入弃牌堆
                    AbstractDungeon.player.discardPile.addToTop(replacement);
                }
                this.isDone = true;
            }
        });
        return true;
    }

    // 检测动作结束时回调，释放“已入队”标记
    public void markCheckResolved() {
        checkQueued = false;
    }

    /**
     * 重置鼠符咒冷却与触发标记（供兔符咒刷新时调用）。
     */
    public void resetUsedThisTurn() {
        this.usedThisTurn = false;
        this.checkQueued = false;
        this.counter = 0;
        this.grayscale = false;
        this.flash();
    }

    // 将替换检测加入行动队列
    private void queueReplaceCheck() {
        if (usedThisTurn || this.counter > 0 || checkQueued || !canTriggerNow() || !hasReplaceableCardInHand()) {
            return;
        }
        checkQueued = true;
        addToBot(new RatTalismanReplaceAction(this));
    }

    // 仅在玩家回合且战斗中允许触发
    private boolean canTriggerNow() {
        if (AbstractDungeon.actionManager == null || !canInteractInCombat()) {
            return false;
        }
        return !AbstractDungeon.actionManager.turnHasEnded;
    }

    private boolean hasReplaceableCardInHand() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.hand == null) {
            return false;
        }
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (isReplaceable(card)) {
                return true;
            }
        }
        return false;
    }

    private boolean isReplaceable(AbstractCard card) {
        return card.type == AbstractCard.CardType.CURSE || card.type == AbstractCard.CardType.STATUS;
    }

    // 生成替换用攻击牌：0费，且使用后消耗
    // 注意：返回的卡牌实例必须直接加入手牌，不可经过 MakeTempCardInHandAction，
    //       因为其内部 makeStatEquivalentCopy() 不复制 exhaust 字段
    private AbstractCard createReplacementAttack() {
        AbstractCard randomAttack = null;
        for (int i = 0; i < 200; i++) {
            AbstractCard candidate = AbstractDungeon.returnTrulyRandomCardInCombat();
            if (candidate != null && candidate.type == AbstractCard.CardType.ATTACK) {
                randomAttack = candidate.makeCopy();
                break;
            }
        }

        if (randomAttack == null) {
            // 极端情况下兜底，避免空牌导致异常
            randomAttack = new Strike_Red();
        }

        randomAttack.cost = 0;
        randomAttack.costForTurn = 0;
        randomAttack.isCostModified = true;
        randomAttack.isCostModifiedForTurn = true;
        randomAttack.freeToPlayOnce = true;
        randomAttack.exhaust = true;

        // 修复：确保卡牌描述中包含“消耗”关键字，否则 UI 不会显示，且可能由于某些卡牌的 initializeDescription 逻辑导致属性失效
        if (!randomAttack.rawDescription.contains("消耗") && !randomAttack.rawDescription.toLowerCase().contains("exhaust")) {
            if (com.megacrit.cardcrawl.core.Settings.language == com.megacrit.cardcrawl.core.Settings.GameLanguage.ZHS) {
                randomAttack.rawDescription += " NL 消耗";
            } else {
                randomAttack.rawDescription += " NL Exhaust.";
            }
        }

        randomAttack.initializeDescription();
        return randomAttack;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void justEnteredRoom(com.megacrit.cardcrawl.rooms.AbstractRoom room) {
        super.justEnteredRoom(room);
        if (AbstractDungeon.player != null && AbstractDungeon.player.relics != null) {
            for (com.megacrit.cardcrawl.relics.AbstractRelic r : AbstractDungeon.player.relics) {
                if (r instanceof BaseRelic) {
                    ((BaseRelic) r).refreshDescription();
                }
            }
        }
    }
}
