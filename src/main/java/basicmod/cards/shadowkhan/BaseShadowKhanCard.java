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

public abstract class BaseShadowKhanCard extends BaseCard {
    public BaseShadowKhanCard(String id, CardStats info) {
        super(id, info);
        tags.add(CustomTags.SHADOW_KHAN);
        // 黑影兵团牌默认消耗，避免在牌堆里堆积。
        this.exhaust = true;
    }

    private int getDominionAmount() {
        if (AbstractDungeon.player == null) {
            return 0;
        }
        // 统一从玩家身上的【影蚀】读取层数，供伤害/格挡结算复用。
        AbstractPower dominion = AbstractDungeon.player.getPower(DominionPower.POWER_ID);
        return dominion == null ? 0 : Math.max(0, dominion.amount);
    }

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

    private static void restorePower(AbstractCreature creature, AbstractPower power, int index) {
        if (creature == null || power == null || index < 0) {
            return;
        }
        if (!creature.powers.contains(power)) {
            int insertIndex = Math.min(index, creature.powers.size());
            creature.powers.add(insertIndex, power);
        }
    }

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

    private static void restorePlayerPowers(ArrayList<AbstractPower> originalPowers) {
        if (AbstractDungeon.player != null && originalPowers != null) {
            AbstractDungeon.player.powers = originalPowers;
        }
    }

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
        boolean needDebugLog = dominion > 0 || originalStr != 0 || originalDex != 0;
        ArrayList<AbstractPower> originalPowers = copyPlayerPowersIfNeeded(ignoreWeak, weakPower, ignoreFrail, frailPower);

        if (needDebugLog) {
            BasicMod.logger.info("【影蚀结算-预览】卡牌={}，基础伤害={}，基础格挡={}，影蚀={}，力量={}，敏捷={}",
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

            // 关键点：把【影蚀】层数直接加到基础伤害/格挡上，再走原版结算链路。
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
            BasicMod.logger.info("【影蚀结算-预览结果】卡牌={}，最终伤害={}，最终格挡={}，伤害已改={}，格挡已改={}",
                    this.cardID, this.damage, this.block, this.isDamageModified, this.isBlockModified);
        }
    }

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
        boolean needDebugLog = dominion > 0 || originalStr != 0 || originalDex != 0;
        ArrayList<AbstractPower> originalPowers = copyPlayerPowersIfNeeded(ignoreWeak, weakPower, ignoreFrail, frailPower);

        if (needDebugLog) {
            String targetName = m == null ? "空目标" : m.name;
            BasicMod.logger.info("【影蚀结算-实战】卡牌={}，目标={}，基础伤害={}，影蚀={}，力量={}，敏捷={}",
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
            BasicMod.logger.info("【影蚀结算-实战结果】卡牌={}，目标={}，最终伤害={}，伤害已改={}",
                    this.cardID, targetName, this.damage, this.isDamageModified);
        }
    }
}
