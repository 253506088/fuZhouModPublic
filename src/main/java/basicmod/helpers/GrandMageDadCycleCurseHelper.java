package basicmod.helpers;

import basicmod.BasicMod;
import basicmod.actions.GrandMageDadSelectiveExhaustHandAction;
import basicmod.cards.BaseCard;
import basicmod.monsters.GrandMageDad;
import basicmod.powers.GrandMageDadCycleCursePower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ThornsPower;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GrandMageDadCycleCurseHelper {
    public static final int STAGE_COLLAPSE_UPGRADES = 1;
    public static final int STAGE_EXHAUST_HAND = 2;
    public static final int STAGE_CARD_DAMAGE = 3;
    public static final int STAGE_CARD_DECAY = 4;
    public static final int STAGE_TEMP_THORNS = 5;

    private static final int CARD_DAMAGE_AMOUNT = 2;
    private static final int UNEXHAUSTED_HAND_CARD_DAMAGE_AMOUNT = 12;
    private static final int TEMP_THORNS_AMOUNT = 5;

    private static final IdentityHashMap<AbstractCard, CardSnapshot> collapsedCards = new IdentityHashMap<>();
    private static int temporaryThorns = 0;

    private static final String[] CARD_FIELDS_TO_COPY = new String[]{
            "cost", "costForTurn", "isCostModified", "isCostModifiedForTurn",
            "selfRetain", "isInnate", "upgraded", "timesUpgraded",
            "upgradedCost", "upgradedDamage", "upgradedBlock", "upgradedMagicNumber",
            "exhaust", "isEthereal", "multiDamage",
            "baseDamage", "baseBlock", "baseMagicNumber", "baseHeal", "baseDraw", "baseDiscard",
            "damage", "block", "magicNumber", "heal", "draw", "discard",
            "isDamageModified", "isBlockModified", "isMagicNumberModified",
            "damageTypeForTurn", "target", "cardsToPreview", "name", "rawDescription", "cantUseMessage"
    };

    private static final LinkedHashMap<String, Field> CARD_FIELDS = buildCardFields();
    private static final Field BASE_CARD_VARIABLES_FIELD = findField(BaseCard.class, "cardVariables");
    private static final Constructor<?> LOCAL_VAR_INFO_CONSTRUCTOR = findLocalVarInfoConstructor();
    private static final LinkedHashMap<String, Field> LOCAL_VAR_FIELDS = buildLocalVarFields();

    private GrandMageDadCycleCurseHelper() {
    }

    public static void applyStage(GrandMageDad dad, int stage) {
        if (dad == null || stage <= 0) {
            return;
        }
        endPlayerCurseTurn();

        GrandMageDadCycleCursePower power = new GrandMageDadCycleCursePower(dad, stage);
        dad.powers.add(power);
        power.onInitialApplication();
        power.updateDescription();
        AbstractDungeon.onModifyPower();
        GrandMageDad.logDetail("【五行压制】展示阶段生效：stage=" + stage + "。");
    }

    public static void onCyclePowerApplied(GrandMageDad dad, int stage) {
        if (stage == STAGE_COLLAPSE_UPGRADES) {
            collapseUpgradedCardsForCurrentTurn();
        } else if (stage == STAGE_TEMP_THORNS) {
            grantTemporaryThorns(dad);
        }
    }

    public static void onPlayerTurnStartPostDraw() {
        GrandMageDadCycleCursePower decayPower = getActivePower(STAGE_CARD_DECAY);
        if (decayPower != null) {
            decayPower.resetCardsPlayed();
        }
        if (isStageActive(STAGE_COLLAPSE_UPGRADES)) {
            collapseUpgradedCardsForCurrentTurn();
        }
    }

    public static void onPlayerCardUsed(AbstractCard card) {
        ensureCollapsed(card);
        if (isStageActive(STAGE_CARD_DAMAGE)) {
            damagePlayerForUsingCard();
        }
    }

    public static void onPlayerCardUseFinished(AbstractCard card) {
        GrandMageDadCycleCursePower decayPower = getActivePower(STAGE_CARD_DECAY);
        if (decayPower != null) {
            decayPower.recordCardPlayed();
            refreshHand();
        }
    }

    public static void ensureCollapsed(AbstractCard card) {
        if (card == null || !isStageActive(STAGE_COLLAPSE_UPGRADES) || !card.upgraded) {
            return;
        }
        collapseCard(card);
    }

    public static int getCurrentDecayAmount() {
        GrandMageDadCycleCursePower power = getActivePower(STAGE_CARD_DECAY);
        return power == null ? 0 : Math.max(0, power.amount2);
    }

    public static boolean isStageExhaustHandActive() {
        return isStageActive(STAGE_EXHAUST_HAND);
    }

    public static void exhaustRemainingHandAtEndOfTurn() {
        AbstractPlayer player = AbstractDungeon.player;
        if (player == null || player.hand == null) {
            endPlayerCurseTurn();
            return;
        }

        ArrayList<AbstractCard> triggerCards = new ArrayList<>(player.hand.group);
        Collections.shuffle(triggerCards);
        for (AbstractCard card : triggerCards) {
            card.triggerOnEndOfPlayerTurn();
        }

        AbstractDungeon.actionManager.addToTop(new GrandMageDadSelectiveExhaustHandAction(
                getActiveDadSource(STAGE_EXHAUST_HAND),
                UNEXHAUSTED_HAND_CARD_DAMAGE_AMOUNT));
        GrandMageDad.logDetail("【五行压制】回合结束进入不消耗手牌选择：手牌数量=" + player.hand.size()
                + "，每选择1张伤害=" + UNEXHAUSTED_HAND_CARD_DAMAGE_AMOUNT + "。");
        endPlayerCurseTurn();
    }

    public static void endPlayerCurseTurn() {
        restoreCollapsedCards();
        removeTemporaryThorns();
        removeActiveCyclePower();
    }

    public static void clearAll() {
        restoreCollapsedCards();
        removeTemporaryThorns();
        removeActiveCyclePower();
    }

    public static boolean hasActiveCyclePower() {
        return getActivePower(0) != null;
    }

    public static AbstractCreature getActiveDadSource(int stage) {
        GrandMageDadCycleCursePower power = getActivePower(stage);
        return power == null ? null : power.owner;
    }

    private static boolean isStageActive(int stage) {
        return getActivePower(stage) != null;
    }

    private static GrandMageDadCycleCursePower getActivePower(int stage) {
        GrandMageDad dad = findGrandMageDad();
        if (dad == null || !dad.hasPower(GrandMageDadCycleCursePower.POWER_ID)) {
            return null;
        }
        AbstractPower power = dad.getPower(GrandMageDadCycleCursePower.POWER_ID);
        if (!(power instanceof GrandMageDadCycleCursePower)) {
            return null;
        }
        GrandMageDadCycleCursePower cyclePower = (GrandMageDadCycleCursePower) power;
        if (stage > 0 && cyclePower.amount != stage) {
            return null;
        }
        return cyclePower;
    }

    private static GrandMageDad findGrandMageDad() {
        com.megacrit.cardcrawl.rooms.AbstractRoom room = AbstractDungeon.getCurrRoom();
        if (room == null || room.monsters == null || room.monsters.monsters == null) {
            return null;
        }
        for (AbstractMonster monster : room.monsters.monsters) {
            if (monster instanceof GrandMageDad && !monster.isDeadOrEscaped()) {
                return (GrandMageDad) monster;
            }
        }
        return null;
    }

    private static void damagePlayerForUsingCard() {
        if (AbstractDungeon.player == null) {
            return;
        }
        AbstractCreature source = getActiveDadSource(STAGE_CARD_DAMAGE);
        AbstractDungeon.actionManager.addToTop(new com.megacrit.cardcrawl.actions.common.DamageAction(
                AbstractDungeon.player,
                new DamageInfo(source, CARD_DAMAGE_AMOUNT, DamageInfo.DamageType.NORMAL),
                com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect.FIRE));
        GrandMageDad.logDetail("【五行压制】出牌反震：玩家受到可被格挡抵挡的2点伤害。");
    }

    private static void grantTemporaryThorns(GrandMageDad dad) {
        if (dad == null) {
            return;
        }
        temporaryThorns += TEMP_THORNS_AMOUNT;
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(dad, dad, new ThornsPower(dad, TEMP_THORNS_AMOUNT), TEMP_THORNS_AMOUNT));
        GrandMageDad.logDetail("【五行压制】临时荆棘入队：+" + TEMP_THORNS_AMOUNT + "。");
    }

    private static void removeTemporaryThorns() {
        if (temporaryThorns <= 0) {
            return;
        }
        GrandMageDad dad = findGrandMageDad();
        if (dad != null && dad.hasPower(ThornsPower.POWER_ID)) {
            AbstractPower thorns = dad.getPower(ThornsPower.POWER_ID);
            int removeAmount = Math.min(temporaryThorns, thorns.amount);
            thorns.amount -= removeAmount;
            if (thorns.amount <= 0) {
                dad.powers.remove(thorns);
                thorns.onRemove();
            } else {
                thorns.updateDescription();
            }
            AbstractDungeon.onModifyPower();
            GrandMageDad.logDetail("【五行压制】移除临时荆棘：" + removeAmount + "。");
        }
        temporaryThorns = 0;
    }

    private static void removeActiveCyclePower() {
        GrandMageDad dad = findGrandMageDad();
        if (dad == null || !dad.hasPower(GrandMageDadCycleCursePower.POWER_ID)) {
            return;
        }
        AbstractPower power = dad.getPower(GrandMageDadCycleCursePower.POWER_ID);
        dad.powers.remove(power);
        power.onRemove();
        AbstractDungeon.onModifyPower();
        GrandMageDad.logDetail("【五行压制】移除当前压制Power。");
    }

    private static void collapseUpgradedCardsForCurrentTurn() {
        AbstractPlayer player = AbstractDungeon.player;
        if (player == null) {
            return;
        }
        collapseGroup(player.hand);
        collapseGroup(player.drawPile);
        collapseGroup(player.discardPile);
        collapseGroup(player.exhaustPile);
        collapseGroup(player.limbo);
        refreshHand();
    }

    private static void collapseGroup(com.megacrit.cardcrawl.cards.CardGroup group) {
        if (group == null || group.group == null) {
            return;
        }
        for (AbstractCard card : group.group) {
            ensureCollapsed(card);
        }
    }

    private static void collapseCard(AbstractCard card) {
        if (collapsedCards.containsKey(card)) {
            return;
        }

        AbstractCard base = card.makeCopy();
        if (base == null) {
            return;
        }
        base.misc = card.misc;

        collapsedCards.put(card, new CardSnapshot(card));
        copyCoreFields(card, base);
        copyBaseCardVariables(card, base);
        card.initializeDescription();
        GrandMageDad.logDetail("【五行压制】升级牌塌落为基础版：" + card.name + "/" + card.cardID + "。");
    }

    private static void restoreCollapsedCards() {
        if (collapsedCards.isEmpty()) {
            return;
        }
        for (Map.Entry<AbstractCard, CardSnapshot> entry : new ArrayList<>(collapsedCards.entrySet())) {
            entry.getValue().restore(entry.getKey());
            entry.getKey().initializeDescription();
        }
        int restored = collapsedCards.size();
        collapsedCards.clear();
        refreshAllCombatCards();
        GrandMageDad.logDetail("【五行压制】恢复临时塌落升级牌：数量=" + restored + "。");
    }

    private static void refreshHand() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.hand == null) {
            return;
        }
        AbstractDungeon.player.hand.applyPowers();
        AbstractDungeon.player.hand.glowCheck();
    }

    private static void refreshAllCombatCards() {
        AbstractPlayer player = AbstractDungeon.player;
        if (player == null) {
            return;
        }
        refreshGroup(player.hand);
        refreshGroup(player.drawPile);
        refreshGroup(player.discardPile);
        refreshGroup(player.exhaustPile);
        refreshGroup(player.limbo);
        if (player.hand != null) {
            player.hand.glowCheck();
        }
    }

    private static void refreshGroup(com.megacrit.cardcrawl.cards.CardGroup group) {
        if (group == null || group.group == null) {
            return;
        }
        for (AbstractCard card : group.group) {
            card.applyPowers();
        }
    }

    private static LinkedHashMap<String, Field> buildCardFields() {
        LinkedHashMap<String, Field> fields = new LinkedHashMap<>();
        for (String name : CARD_FIELDS_TO_COPY) {
            Field field = findField(AbstractCard.class, name);
            if (field != null) {
                fields.put(name, field);
            }
        }
        return fields;
    }

    private static Field findField(Class<?> type, String name) {
        Class<?> current = type;
        while (current != null) {
            try {
                Field field = current.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private static Constructor<?> findLocalVarInfoConstructor() {
        try {
            Class<?> type = Class.forName("basicmod.cards.BaseCard$LocalVarInfo");
            Constructor<?> constructor = type.getDeclaredConstructor(int.class, int.class);
            constructor.setAccessible(true);
            return constructor;
        } catch (Exception e) {
            BasicMod.logger.warn("【五行压制】无法初始化自定义变量快照构造器。", e);
            return null;
        }
    }

    private static LinkedHashMap<String, Field> buildLocalVarFields() {
        LinkedHashMap<String, Field> fields = new LinkedHashMap<>();
        try {
            Class<?> type = Class.forName("basicmod.cards.BaseCard$LocalVarInfo");
            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                fields.put(field.getName(), field);
            }
        } catch (Exception e) {
            BasicMod.logger.warn("【五行压制】无法初始化自定义变量字段快照。", e);
        }
        return fields;
    }

    private static void copyCoreFields(AbstractCard target, AbstractCard source) {
        for (Field field : CARD_FIELDS.values()) {
            try {
                field.set(target, field.get(source));
            } catch (Exception e) {
                BasicMod.logger.warn("【五行压制】复制卡牌基础字段失败：" + field.getName(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void copyBaseCardVariables(AbstractCard target, AbstractCard source) {
        if (!(target instanceof BaseCard) || !(source instanceof BaseCard) || BASE_CARD_VARIABLES_FIELD == null) {
            return;
        }
        try {
            Map<String, Object> targetVars = (Map<String, Object>) BASE_CARD_VARIABLES_FIELD.get(target);
            Map<String, Object> sourceVars = (Map<String, Object>) BASE_CARD_VARIABLES_FIELD.get(source);
            targetVars.clear();
            targetVars.putAll(cloneLocalVarMap(sourceVars));
        } catch (Exception e) {
            BasicMod.logger.warn("【五行压制】复制卡牌自定义变量失败：" + target.cardID, e);
        }
    }

    private static Map<String, Object> cloneLocalVarMap(Map<String, Object> source) {
        Map<String, Object> result = new HashMap<>();
        if (source == null) {
            return result;
        }
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            result.put(entry.getKey(), cloneLocalVar(entry.getValue()));
        }
        return result;
    }

    private static Object cloneLocalVar(Object source) {
        if (source == null || LOCAL_VAR_INFO_CONSTRUCTOR == null) {
            return source;
        }
        try {
            Object clone = LOCAL_VAR_INFO_CONSTRUCTOR.newInstance(0, 0);
            for (Field field : LOCAL_VAR_FIELDS.values()) {
                Object value = field.get(source);
                if (value instanceof int[]) {
                    value = ((int[]) value).clone();
                }
                field.set(clone, value);
            }
            return clone;
        } catch (Exception e) {
            BasicMod.logger.warn("【五行压制】复制自定义变量失败。", e);
            return source;
        }
    }

    private static class CardSnapshot {
        private final HashMap<Field, Object> values = new HashMap<>();
        private final Map<String, Object> cardVariables;

        private CardSnapshot(AbstractCard card) {
            for (Field field : CARD_FIELDS.values()) {
                try {
                    Object value = field.get(card);
                    if (value instanceof int[]) {
                        value = ((int[]) value).clone();
                    }
                    values.put(field, value);
                } catch (Exception e) {
                    BasicMod.logger.warn("【五行压制】保存卡牌字段失败：" + field.getName(), e);
                }
            }
            cardVariables = snapshotCardVariables(card);
        }

        private void restore(AbstractCard card) {
            for (Map.Entry<Field, Object> entry : values.entrySet()) {
                try {
                    Object value = entry.getValue();
                    if (value instanceof int[]) {
                        value = ((int[]) value).clone();
                    }
                    entry.getKey().set(card, value);
                } catch (Exception e) {
                    BasicMod.logger.warn("【五行压制】恢复卡牌字段失败：" + entry.getKey().getName(), e);
                }
            }
            restoreCardVariables(card, cardVariables);
        }

        @SuppressWarnings("unchecked")
        private static Map<String, Object> snapshotCardVariables(AbstractCard card) {
            if (!(card instanceof BaseCard) || BASE_CARD_VARIABLES_FIELD == null) {
                return null;
            }
            try {
                return cloneLocalVarMap((Map<String, Object>) BASE_CARD_VARIABLES_FIELD.get(card));
            } catch (Exception e) {
                BasicMod.logger.warn("【五行压制】保存自定义变量失败：" + card.cardID, e);
                return null;
            }
        }

        @SuppressWarnings("unchecked")
        private static void restoreCardVariables(AbstractCard card, Map<String, Object> snapshot) {
            if (!(card instanceof BaseCard) || snapshot == null || BASE_CARD_VARIABLES_FIELD == null) {
                return;
            }
            try {
                Map<String, Object> targetVars = (Map<String, Object>) BASE_CARD_VARIABLES_FIELD.get(card);
                targetVars.clear();
                targetVars.putAll(cloneLocalVarMap(snapshot));
            } catch (Exception e) {
                BasicMod.logger.warn("【五行压制】恢复自定义变量失败：" + card.cardID, e);
            }
        }
    }
}
