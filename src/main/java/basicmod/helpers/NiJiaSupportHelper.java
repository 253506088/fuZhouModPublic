package basicmod.helpers;

import basicmod.cards.shadowkhan.NiJiaNinja;
import basicmod.powers.DemonCodexPower;
import basicmod.powers.DominionPower;
import basicmod.powers.DoubleSticksPower;
import basicmod.powers.KatanaPower;
import basicmod.powers.NinjaCooperationPower;
import basicmod.powers.ShadowGuardPower;
import basicmod.powers.ShurikenPower;
import basicmod.powers.SmokeBombPower;
import basicmod.powers.WingsuitFlightPower;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;

/**
 * 尼嘉辅助工具。
 * 集中处理尼嘉-忍者团与面具辅助能力之间的联动，避免把判断散落在卡牌动作里。
 */
public class NiJiaSupportHelper {
    private static final int DOUBLE_STICKS_DOMINION_MULTIPLIER = 3;
    private static final int SHURIKEN_NIJIA_BASE_DAMAGE = 3;
    private static final int SHURIKEN_UPGRADED_NIJIA_BASE_DAMAGE = 5;

    /** 消耗堆尼嘉计数缓存：上次统计时的消耗堆对象 */
    private static CardGroup cachedExhaustPile = null;
    /** 消耗堆尼嘉计数缓存：上次统计时的消耗堆张数 */
    private static int cachedExhaustSize = -1;
    /** 消耗堆尼嘉计数缓存：上次统计出的尼嘉数量 */
    private static int cachedNiJiaCountInExhaust = 0;

    /**
     * 黑影兵团结算用的玩家能力快照。
     * 一次遍历玩家能力列表拿齐结算相关的全部能力，避免高频结算路径上反复线性查找。
     */
    public static class ShadowKhanPowerSnapshot {
        /** 影噬 */
        public AbstractPower dominion;
        /** 力量 */
        public AbstractPower strength;
        /** 敏捷 */
        public AbstractPower dexterity;
        /** 虚弱 */
        public AbstractPower weak;
        /** 脆弱 */
        public AbstractPower frail;
        /** 是否拥有手里剑 */
        public boolean hasShuriken;
        /** 忍者协作 */
        public AbstractPower ninjaCooperation;
        /** 是否拥有恶魔法典 */
        public boolean hasDemonCodex;
        /** 是否拥有双棍 */
        public boolean hasDoubleSticks;

        /**
         * 获取影噬层数。
         *
         * @return 影噬层数，没有时为 0
         */
        public int getDominionAmount() {
            return this.dominion == null ? 0 : Math.max(0, this.dominion.amount);
        }
    }

    /**
     * 一次遍历玩家能力列表，收集黑影兵团结算需要的全部能力。
     *
     * @return 能力快照；玩家不存在时返回空快照
     */
    public static ShadowKhanPowerSnapshot collectShadowKhanPowers() {
        ShadowKhanPowerSnapshot snapshot = new ShadowKhanPowerSnapshot();
        if (AbstractDungeon.player == null) {
            return snapshot;
        }
        // 关键点：这一趟遍历替代原本 9 次独立的 getPower/hasPower 线性查找。
        for (AbstractPower power : AbstractDungeon.player.powers) {
            if (power == null) {
                continue;
            }
            String id = power.ID;
            if (DominionPower.POWER_ID.equals(id)) {
                snapshot.dominion = power;
            } else if (StrengthPower.POWER_ID.equals(id)) {
                snapshot.strength = power;
            } else if (DexterityPower.POWER_ID.equals(id)) {
                snapshot.dexterity = power;
            } else if (WeakPower.POWER_ID.equals(id)) {
                snapshot.weak = power;
            } else if (FrailPower.POWER_ID.equals(id)) {
                snapshot.frail = power;
            } else if (DemonCodexPower.POWER_ID.equals(id)) {
                snapshot.hasDemonCodex = true;
            } else if (ShurikenPower.POWER_ID.equals(id)) {
                snapshot.hasShuriken = true;
            } else if (NinjaCooperationPower.POWER_ID.equals(id)) {
                snapshot.ninjaCooperation = power;
            } else if (DoubleSticksPower.POWER_ID.equals(id)) {
                snapshot.hasDoubleSticks = true;
            }
        }
        return snapshot;
    }

    /**
     * 判断卡牌是否为尼嘉-忍者团。
     *
     * @param card 待判断卡牌
     * @return 是尼嘉-忍者团时返回 true
     */
    public static boolean isNiJiaNinja(AbstractCard card) {
        return card != null && NiJiaNinja.ID.equals(card.cardID);
    }

    /**
     * 为生成到手牌的尼嘉牌补上翼装飞行的保留效果。
     *
     * @param card 生成的卡牌
     */
    public static void prepareGeneratedNiJiaCard(AbstractCard card) {
        if (AbstractDungeon.player == null || card == null) {
            return;
        }
        if (isNiJiaNinja(card) && AbstractDungeon.player.hasPower(WingsuitFlightPower.POWER_ID)) {
            card.selfRetain = true;
            card.retain = true;
        }
    }

    /**
     * 让当前手牌中的尼嘉-忍者团获得保留。
     */
    public static void applyRetainToHand() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.hand == null) {
            return;
        }
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            prepareGeneratedNiJiaCard(card);
        }
    }

    /**
     * 获取尼嘉-忍者团的影噬伤害倍率。
     *
     * @param snapshot 玩家能力快照
     * @return 有双棍时返回 3，否则返回 1
     */
    public static int getNiJiaDominionDamageMultiplier(ShadowKhanPowerSnapshot snapshot) {
        return snapshot.hasDoubleSticks ? DOUBLE_STICKS_DOMINION_MULTIPLIER : 1;
    }

    /**
     * 获取尼嘉-忍者团的攻击段数。
     *
     * @return 手里剑提供的最高段数，没有手里剑时为 1
     */
    public static int getNiJiaHitCount() {
        AbstractPower power = AbstractDungeon.player == null ? null : AbstractDungeon.player.getPower(ShurikenPower.POWER_ID);
        if (power == null) {
            return 1;
        }
        return Math.max(1, power.amount);
    }

    /**
     * 获取尼嘉-忍者团当前每段基础伤害。
     *
     * @param originalBaseDamage 原本基础伤害
     * @param ninjaUpgraded 尼嘉-忍者团是否已升级
     * @param snapshot 玩家能力快照
     * @return 受手里剑与忍者协作修正后的每段基础伤害
     */
    public static int getNiJiaBaseDamage(int originalBaseDamage, boolean ninjaUpgraded, ShadowKhanPowerSnapshot snapshot) {
        int baseDamage = originalBaseDamage;
        if (snapshot.hasShuriken) {
            baseDamage = ninjaUpgraded ? SHURIKEN_UPGRADED_NIJIA_BASE_DAMAGE : SHURIKEN_NIJIA_BASE_DAMAGE;
        }
        return Math.max(0, baseDamage + getNinjaCooperationBonus(snapshot));
    }

    /**
     * 计算忍者协作提供的每段伤害加成。
     *
     * @param snapshot 玩家能力快照
     * @return 消耗堆尼嘉数量乘以忍者协作层数
     */
    public static int getNinjaCooperationBonus(ShadowKhanPowerSnapshot snapshot) {
        AbstractPower power = snapshot.ninjaCooperation;
        if (power == null || power.amount <= 0) {
            return 0;
        }
        return getNiJiaCountInExhaustPile() * power.amount;
    }

    /**
     * 获取消耗堆中尼嘉-忍者团的数量（带缓存）。
     *
     * @return 消耗堆中的尼嘉-忍者团数量，玩家或消耗堆不存在时为 0
     */
    public static int getNiJiaCountInExhaustPile() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.exhaustPile == null) {
            return 0;
        }
        return countNiJiaInExhaustPile(AbstractDungeon.player.exhaustPile);
    }

    /**
     * 统计消耗堆里尼嘉-忍者团的数量（带缓存）。
     *
     * 消耗堆对象和张数都与上次一致时直接返回缓存；
     * 张数变化（进牌、离场、换战斗清空）时才重新统计一遍。
     *
     * @param exhaustPile 玩家消耗堆
     * @return 消耗堆中的尼嘉-忍者团数量
     */
    private static int countNiJiaInExhaustPile(CardGroup exhaustPile) {
        if (exhaustPile != cachedExhaustPile || exhaustPile.group.size() != cachedExhaustSize) {
            int count = 0;
            for (AbstractCard card : exhaustPile.group) {
                if (isNiJiaNinja(card)) {
                    count++;
                }
            }
            cachedExhaustPile = exhaustPile;
            cachedExhaustSize = exhaustPile.group.size();
            cachedNiJiaCountInExhaust = count;
        }
        return cachedNiJiaCountInExhaust;
    }

    /**
     * 判断尼嘉-忍者团是否需要攻击所有敌人。
     *
     * @return 拥有武士刀能力时返回 true
     */
    public static boolean shouldNiJiaAttackAllEnemies() {
        return AbstractDungeon.player != null && AbstractDungeon.player.hasPower(KatanaPower.POWER_ID);
    }

    /**
     * 在尼嘉-忍者团伤害结算后触发相关辅助能力。
     *
     * @param target 受击目标
     * @param hpLoss 目标失去的生命
     * @param blockedDamage 被格挡吸收的伤害
     * @param segmentResolved 当前攻击段是否完成结算
     */
    public static void onNiJiaDamageResolved(AbstractMonster target, int hpLoss, int blockedDamage, boolean segmentResolved) {
        if (AbstractDungeon.player == null || target == null || !segmentResolved) {
            return;
        }
        triggerShadowGuard(hpLoss, blockedDamage);
        triggerSmokeBomb(target, hpLoss, segmentResolved);
    }

    private static void triggerShadowGuard(int hpLoss, int blockedDamage) {
        AbstractPower power = AbstractDungeon.player.getPower(ShadowGuardPower.POWER_ID);
        if (!(power instanceof ShadowGuardPower) || power.amount <= 0) {
            return;
        }
        ShadowGuardPower shadowGuard = (ShadowGuardPower) power;
        int converted = Math.max(0, hpLoss);
        if (shadowGuard.includesBlockedDamage()) {
            converted += Math.max(0, blockedDamage);
        }
        converted *= Math.max(1, shadowGuard.amount);
        if (converted > 0) {
            shadowGuard.flash();
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, converted));
        }
    }

    private static void triggerSmokeBomb(AbstractMonster target, int hpLoss, boolean segmentResolved) {
        AbstractPower power = AbstractDungeon.player.getPower(SmokeBombPower.POWER_ID);
        if (!(power instanceof SmokeBombPower) || power.amount <= 0) {
            return;
        }
        SmokeBombPower smokeBomb = (SmokeBombPower) power;
        if (!smokeBomb.shouldTrigger(hpLoss, segmentResolved)) {
            return;
        }
        int strengthLoss = Math.max(1, smokeBomb.amount);
        smokeBomb.flash();
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(target, AbstractDungeon.player, new StrengthPower(target, -strengthLoss), -strengthLoss));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(target, AbstractDungeon.player, new GainStrengthPower(target, strengthLoss), strengthLoss));
    }
}
