package basicmod.cards.shadowkhan;

import basicmod.actions.NiJiaNinjaAttackAction;
import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.helpers.NiJiaSupportHelper;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

/**
 * 尼嘉-忍者团。
 * 基础黑影兵团攻击牌，会受到多种尼嘉辅助能力改造。
 */
public class NiJiaNinja extends BaseShadowKhanCard {
    public static final String ID = BasicMod.makeID(NiJiaNinja.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.ATTACK,
            AbstractCard.CardRarity.SPECIAL, // Not in normal rewards
            AbstractCard.CardTarget.ENEMY,
            0
    );

    public NiJiaNinja() {
        super(ID, info);
        setDamage(5, 3); // 5 damage, +3 -> 8
        tags.add(CustomTags.SHADOW_KHAN);
    }

    /**
     * 取得尼嘉-忍者团参与结算的基础伤害。
     *
     * @param originalBaseDamage 原本基础伤害
     * @param snapshot 玩家能力快照
     * @return 受手里剑与忍者协作影响后的每段基础伤害
     */
    @Override
    protected int getShadowKhanBaseDamage(int originalBaseDamage, NiJiaSupportHelper.ShadowKhanPowerSnapshot snapshot) {
        return NiJiaSupportHelper.getNiJiaBaseDamage(originalBaseDamage, this.upgraded, snapshot);
    }

    /**
     * 取得尼嘉-忍者团从影噬获得的伤害加成。
     *
     * @param dominion 当前影噬层数
     * @param snapshot 玩家能力快照
     * @return 受双棍倍率影响后的影噬伤害
     */
    @Override
    protected int getDominionDamageBonus(int dominion, NiJiaSupportHelper.ShadowKhanPowerSnapshot snapshot) {
        return dominion * NiJiaSupportHelper.getNiJiaDominionDamageMultiplier(snapshot);
    }

    /**
     * 复制卡牌时重新判定翼装飞行的保留效果。
     * 原版复制不携带保留标记，生成到手牌的副本要在这里补上。
     */
    @Override
    public AbstractCard makeStatEquivalentCopy() {
        AbstractCard copy = super.makeStatEquivalentCopy();
        NiJiaSupportHelper.prepareGeneratedNiJiaCard(copy);
        return copy;
    }

    /**
     * 使用尼嘉-忍者团。
     * 先把本次出牌的全部攻击段收集齐，标记最后一段免去收尾等待，再依次入队。
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int hitCount = NiJiaSupportHelper.getNiJiaHitCount();
        ArrayList<NiJiaNinjaAttackAction> segmentActions = new ArrayList<>();
        if (NiJiaSupportHelper.shouldNiJiaAttackAllEnemies()) {
            for (int i = 0; i < hitCount; i++) {
                for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                    if (monster != null && !monster.isDeadOrEscaped()) {
                        segmentActions.add(new NiJiaNinjaAttackAction(monster, new DamageInfo(p, this.damage, this.damageTypeForTurn)));
                    }
                }
            }
        } else {
            if (m == null) {
                return;
            }
            for (int i = 0; i < hitCount; i++) {
                segmentActions.add(new NiJiaNinjaAttackAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn)));
            }
        }

        if (segmentActions.isEmpty()) {
            return;
        }
        // 关键点：最后一段攻击免去收尾等待，缩短多段结算的拖沓感；段与段之间的节奏不变。
        segmentActions.get(segmentActions.size() - 1).markAsFinalSegment();
        for (NiJiaNinjaAttackAction action : segmentActions) {
            addToBot(action);
        }
    }
}
