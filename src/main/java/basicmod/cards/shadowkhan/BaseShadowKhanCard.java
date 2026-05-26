package basicmod.cards.shadowkhan;

import basicmod.BasicMod;
import basicmod.cards.BaseCard;
import basicmod.enums.CustomTags;
import basicmod.powers.DominionPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;

import java.util.ArrayList;

/**
 * 黑影兵团卡牌基类。
 *
 * 统一处理黑影兵团牌的影噬加成，以及不受力量、敏捷、虚弱、脆弱影响的结算规则。
 */
public abstract class BaseShadowKhanCard extends BaseCard {
    /**
     * 创建一张黑影兵团卡牌，并统一打上黑影兵团标签。
     *
     * @param id 卡牌 ID
     * @param info 卡牌基础配置
     */
    public BaseShadowKhanCard(String id, CardStats info) {
        super(id, info);
        tags.add(CustomTags.SHADOW_KHAN);
        // 黑影兵团牌默认消耗，避免在牌堆里堆积。
        this.exhaust = true;
    }

    /**
     * 读取玩家身上的影噬层数。
     *
     * @return 当前影噬层数，没有影噬时返回 0
     */
    private int getDominionAmount() {
        if (AbstractDungeon.player == null) {
            return 0;
        }
        // 统一从玩家身上的【影噬】读取层数，供伤害/格挡结算复用。
        AbstractPower dominion = AbstractDungeon.player.getPower(DominionPower.POWER_ID);
        return dominion == null ? 0 : Math.max(0, dominion.amount);
    }

    /**
     * 重新标记黑影兵团牌的伤害和格挡是否被修改。
     *
     * @param originalBaseDamage 计算前的基础伤害
     * @param originalBaseBlock 计算前的基础格挡
     */
    private void refreshShadowKhanModifiedFlags(int originalBaseDamage, int originalBaseBlock) {
        if (originalBaseDamage >= 0) {
            boolean modified = this.damage != originalBaseDamage;
            if (!modified && this.isMultiDamage && this.multiDamage != null) {
                for (int value : this.multiDamage) {
                    if (value != originalBaseDamage) {
                        modified = true;
                        break;
                    }
                }
            }
            this.isDamageModified = modified;
        }

        if (originalBaseBlock >= 0) {
            this.isBlockModified = this.block != originalBaseBlock;
        }
    }

    /**
     * 临时从目标身上移除指定 Power，并返回它原本所在的位置。
     *
     * @param creature 需要临时移除 Power 的目标
     * @param power 需要临时移除的 Power
     * @return 原位置；如果没有移除则返回 -1
     */
    private static int removePowerTemporarily(AbstractCreature creature, AbstractPower power) {
        if (creature == null || power == null) {
            return -1;
        }
        int index = creature.powers.indexOf(power);
        if (index >= 0) {
            creature.powers.remove(index);
        }
        return index;
    }

    /**
     * 把临时移除的 Power 放回原本位置。
     *
     * @param creature 需要恢复 Power 的目标
     * @param power 需要恢复的 Power
     * @param index 原位置
     */
    private static void restorePower(AbstractCreature creature, AbstractPower power, int index) {
        if (creature == null || power == null || index < 0) {
            return;
        }
        if (!creature.powers.contains(power)) {
            int insertIndex = Math.min(index, creature.powers.size());
            creature.powers.add(insertIndex, power);
        }
    }

    /**
     * 必要时拷贝玩家 Power 列表，避免在原版遍历 powers 时修改原列表导致闪退。
     *
     * @param ignoreWeak 本次计算是否需要忽略虚弱
     * @param weakPower 玩家身上的虚弱 Power
     * @param ignoreFrail 本次计算是否需要忽略脆弱
     * @param frailPower 玩家身上的脆弱 Power
     * @return 原始玩家 Power 列表；没有拷贝时返回 null
     */
    private static ArrayList<AbstractPower> copyPlayerPowersIfNeeded(boolean ignoreWeak, AbstractPower weakPower, boolean ignoreFrail, AbstractPower frailPower) {
        if (AbstractDungeon.player == null) {
            return null;
        }
        if (!(ignoreWeak && weakPower != null) && !(ignoreFrail && frailPower != null)) {
            return null;
        }
        ArrayList<AbstractPower> originalPowers = AbstractDungeon.player.powers;
        AbstractDungeon.player.powers = new ArrayList<>(originalPowers);
        return originalPowers;
    }

    /**
     * 恢复被替换过的玩家原始 Power 列表。
     *
     * @param originalPowers 原始玩家 Power 列表
     */
    private static void restorePlayerPowers(ArrayList<AbstractPower> originalPowers) {
        if (AbstractDungeon.player != null && originalPowers != null) {
            AbstractDungeon.player.powers = originalPowers;
        }
    }

    /**
     * 计算手牌预览数值。
     *
     * 预览时临时屏蔽力量、敏捷、虚弱、脆弱，只保留影噬对黑影兵团牌的加成。
     */
    @Override
    public void applyPowers() {
        int dominion = getDominionAmount();
        int originalBaseDamage = this.baseDamage;
        int originalBaseBlock = this.baseBlock;

        AbstractPower strPower = AbstractDungeon.player == null ? null : AbstractDungeon.player.getPower(StrengthPower.POWER_ID);
        AbstractPower dexPower = AbstractDungeon.player == null ? null : AbstractDungeon.player.getPower(DexterityPower.POWER_ID);
        AbstractPower weakPower = AbstractDungeon.player == null ? null : AbstractDungeon.player.getPower(WeakPower.POWER_ID);
        AbstractPower frailPower = AbstractDungeon.player == null ? null : AbstractDungeon.player.getPower(FrailPower.POWER_ID);
        int originalStr = strPower == null ? 0 : strPower.amount;
        int originalDex = dexPower == null ? 0 : dexPower.amount;
        boolean ignoreWeak = this.type == CardType.ATTACK;
        boolean ignoreFrail = originalBaseBlock >= 0;
        int weakPowerIndex = -1;
        int frailPowerIndex = -1;
        boolean needDebugLog = BasicMod.logger.isDebugEnabled() && (dominion > 0 || originalStr != 0 || originalDex != 0);
        ArrayList<AbstractPower> originalPowers = copyPlayerPowersIfNeeded(ignoreWeak, weakPower, ignoreFrail, frailPower);

        if (needDebugLog) {
            BasicMod.logger.debug("【影噬结算-预览】卡牌={}，基础伤害={}，基础格挡={}，影噬={}，力量={}，敏捷={}",
                    this.cardID, originalBaseDamage, originalBaseBlock, dominion, originalStr, originalDex);
        }

        try {
            // 关键点：黑影兵团牌计算时临时屏蔽力量/敏捷影响。
            if (strPower != null) {
                strPower.amount = 0;
            }
            if (dexPower != null) {
                dexPower.amount = 0;
            }
            if (ignoreWeak) {
                weakPowerIndex = removePowerTemporarily(AbstractDungeon.player, weakPower);
            }
            if (ignoreFrail) {
                frailPowerIndex = removePowerTemporarily(AbstractDungeon.player, frailPower);
            }

            // 关键点：把【影噬】层数直接加到基础伤害/格挡上，再走原版结算链路。
            if (originalBaseDamage >= 0) {
                this.baseDamage = originalBaseDamage + dominion;
            }
            if (originalBaseBlock >= 0) {
                this.baseBlock = originalBaseBlock + dominion;
            }

            super.applyPowers();
        } finally {
            this.baseDamage = originalBaseDamage;
            this.baseBlock = originalBaseBlock;
            if (strPower != null) {
                strPower.amount = originalStr;
            }
            if (dexPower != null) {
                dexPower.amount = originalDex;
            }
            if (ignoreWeak) {
                restorePower(AbstractDungeon.player, weakPower, weakPowerIndex);
            }
            if (ignoreFrail) {
                restorePower(AbstractDungeon.player, frailPower, frailPowerIndex);
            }
            restorePlayerPowers(originalPowers);
        }

        refreshShadowKhanModifiedFlags(originalBaseDamage, originalBaseBlock);

        if (needDebugLog) {
            BasicMod.logger.debug("【影噬结算-预览结果】卡牌={}，最终伤害={}，最终格挡={}，伤害已改={}，格挡已改={}",
                    this.cardID, this.damage, this.block, this.isDamageModified, this.isBlockModified);
        }
    }

    /**
     * 计算指定目标下的实战伤害预览。
     *
     * 鼠标悬停怪物时会高频调用，所以这里保持逻辑集中，并确保所有临时修改都在 finally 中恢复。
     *
     * @param m 当前预览目标
     */
    @Override
    public void calculateCardDamage(AbstractMonster m) {
        int dominion = getDominionAmount();
        int originalBaseDamage = this.baseDamage;
        int originalBaseBlock = this.baseBlock;

        AbstractPower strPower = AbstractDungeon.player == null ? null : AbstractDungeon.player.getPower(StrengthPower.POWER_ID);
        AbstractPower dexPower = AbstractDungeon.player == null ? null : AbstractDungeon.player.getPower(DexterityPower.POWER_ID);
        AbstractPower weakPower = AbstractDungeon.player == null ? null : AbstractDungeon.player.getPower(WeakPower.POWER_ID);
        AbstractPower frailPower = AbstractDungeon.player == null ? null : AbstractDungeon.player.getPower(FrailPower.POWER_ID);
        int originalStr = strPower == null ? 0 : strPower.amount;
        int originalDex = dexPower == null ? 0 : dexPower.amount;
        boolean ignoreWeak = this.type == CardType.ATTACK;
        boolean ignoreFrail = originalBaseBlock >= 0;
        int weakPowerIndex = -1;
        int frailPowerIndex = -1;
        boolean needDebugLog = BasicMod.logger.isDebugEnabled() && (dominion > 0 || originalStr != 0 || originalDex != 0);
        ArrayList<AbstractPower> originalPowers = copyPlayerPowersIfNeeded(ignoreWeak, weakPower, ignoreFrail, frailPower);

        if (needDebugLog) {
            String targetName = m == null ? "空目标" : m.name;
            BasicMod.logger.debug("【影噬结算-实战】卡牌={}，目标={}，基础伤害={}，影噬={}，力量={}，敏捷={}",
                    this.cardID, targetName, originalBaseDamage, dominion, originalStr, originalDex);
        }

        try {
            if (strPower != null) {
                strPower.amount = 0;
            }
            if (dexPower != null) {
                dexPower.amount = 0;
            }
            if (ignoreWeak) {
                weakPowerIndex = removePowerTemporarily(AbstractDungeon.player, weakPower);
            }
            if (ignoreFrail) {
                frailPowerIndex = removePowerTemporarily(AbstractDungeon.player, frailPower);
            }

            if (originalBaseDamage >= 0) {
                this.baseDamage = originalBaseDamage + dominion;
            }
            if (originalBaseBlock >= 0) {
                this.baseBlock = originalBaseBlock + dominion;
            }

            super.calculateCardDamage(m);
        } finally {
            this.baseDamage = originalBaseDamage;
            this.baseBlock = originalBaseBlock;
            if (strPower != null) {
                strPower.amount = originalStr;
            }
            if (dexPower != null) {
                dexPower.amount = originalDex;
            }
            if (ignoreWeak) {
                restorePower(AbstractDungeon.player, weakPower, weakPowerIndex);
            }
            if (ignoreFrail) {
                restorePower(AbstractDungeon.player, frailPower, frailPowerIndex);
            }
            restorePlayerPowers(originalPowers);
        }

        refreshShadowKhanModifiedFlags(originalBaseDamage, originalBaseBlock);

        if (needDebugLog) {
            String targetName = m == null ? "空目标" : m.name;
            BasicMod.logger.debug("【影噬结算-实战结果】卡牌={}，目标={}，最终伤害={}，伤害已改={}",
                    this.cardID, targetName, this.damage, this.isDamageModified);
        }
    }
}
