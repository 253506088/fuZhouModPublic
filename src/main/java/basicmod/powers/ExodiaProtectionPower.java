package basicmod.powers;

import basicmod.BasicMod;
import basicmod.actions.ExodiaKillAllAction;
import basicmod.cards.colorless.ExodiaLeftArm;
import basicmod.cards.colorless.ExodiaLeftLeg;
import basicmod.cards.colorless.ExodiaRightArm;
import basicmod.cards.colorless.ExodiaRightLeg;
import basicmod.cards.colorless.ExodiaSealed;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;

/**
 * 艾克佐迪亚的庇护 Power。
 *
 * 触发时机：玩家在战斗中集齐 5 张艾克佐迪亚部件并触发胜利后，作为兜底保护施加。
 * 设计目的：兼容觉醒者复活、腐化心脏伤害上限、未知 mod 怪物等所有"杀不死"场景，
 *          确保玩家在 Exodia 触发后无论战斗持续多久都不会因怪物攻击落败。
 *
 * 三重防御：
 * 1) 表层视觉：每回合开始 + 集齐瞬间各加 999 格挡 + 1 层无实体，
 *    给玩家明确的"我无敌了"反馈。
 * 2) 兜底拦截：atDamageFinalReceive 与 onLoseHp 都返回 0，
 *    这是真正的护身符，绕开任何"格挡清零""无实体衰减"等机制。
 * 3) 持续清场：每回合开始锁定手牌中的 Exodia 部件，并再排一次 ExodiaKillAllAction
 *    继续尝试杀光场上残留怪物。
 *
 * 战斗结束时由 STS 自动清除，不会留尾巴。
 */
public class ExodiaProtectionPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("ExodiaProtectionPower");

    /** Exodia 5 件套的卡 ID 数组，便于循环匹配 */
    private static final String[] EXODIA_PART_IDS = new String[]{
            ExodiaSealed.ID,
            ExodiaRightArm.ID,
            ExodiaLeftArm.ID,
            ExodiaRightLeg.ID,
            ExodiaLeftLeg.ID
    };

    public ExodiaProtectionPower(AbstractCreature owner) {
        super(POWER_ID, PowerType.BUFF, false, owner, null, 1);
    }

    @Override
    public void updateDescription() {
        if (this.DESCRIPTIONS != null && this.DESCRIPTIONS.length >= 1) {
            this.description = DESCRIPTIONS[0];
        }
    }

    /**
     * 集齐艾克佐迪亚的瞬间：立即获得 999 格挡 + 1 层无实体 + 锁定手牌部件。
     * 这一段保护当前敌人回合的攻击。
     */
    @Override
    public void onInitialApplication() {
        BasicMod.logger.info("【艾克佐迪亚的庇护】Power 应用瞬间，立即上 999 格挡 + 1 层无实体。");
        applyDefensiveBuffs();
    }

    /**
     * 玩家回合开始时（抽牌前）：
     * - 续 999 格挡 + 1 层无实体
     * - 锁定手牌中所有 Exodia 部件
     * - 再次排队循环补刀，尝试结束战斗
     */
    @Override
    public void atStartOfTurn() {
        if (this.owner == null || this.owner.isDead) {
            return;
        }
        BasicMod.logger.info("【艾克佐迪亚的庇护】回合开始，续 999 格挡 + 1 层无实体，再尝试一次斩杀。");
        this.flash();
        applyDefensiveBuffs();
        // 再来一轮循环补刀，覆盖未杀死的复活类怪物
        this.addToBot(new ExodiaKillAllAction());
    }

    /**
     * 真正的护身符：把所有最终伤害归零。
     * 这一层不依赖 block 或 Intangible 衰减，是绝对稳的兜底。
     */
    @Override
    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
        return 0.0F;
    }

    /**
     * 拦截"失去生命"类伤害（HP_LOSS、丢失生命遗物等）。
     */
    @Override
    public int onLoseHp(int damageAmount) {
        return 0;
    }

    /**
     * 共用方法：施加 999 格挡 + 1 层无实体 + 锁手牌部件。
     */
    private void applyDefensiveBuffs() {
        if (this.owner == null) return;

        // 999 格挡（视觉反馈）
        this.addToBot(new GainBlockAction(this.owner, this.owner, 999));

        // 1 层无实体（视觉反馈，叠层不闪烁）
        this.addToBot(new ApplyPowerAction(
                this.owner, this.owner,
                new IntangiblePlayerPower(this.owner, 1), 1,
                true, AbstractGameAction.AttackEffect.NONE));

        // 锁定手牌中所有 Exodia 部件
        lockExodiaPartsInHand();
    }

    /**
     * 把当前手牌中的所有 Exodia 部件标记为保留（仅 false→true 方向，避免覆盖卡牌自带 retain）。
     */
    private void lockExodiaPartsInHand() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.hand == null) {
            return;
        }
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (card == null || card.cardID == null) continue;
            if (isExodiaPart(card.cardID)) {
                card.selfRetain = true;
                card.retain = true;
            }
        }
    }

    /** 判断给定 ID 是否为 Exodia 5 件套之一 */
    private static boolean isExodiaPart(String cardID) {
        for (String id : EXODIA_PART_IDS) {
            if (id.equals(cardID)) return true;
        }
        return false;
    }
}
