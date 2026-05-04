package basicmod.cards.demons;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardThunderDemonQi extends BaseCard {
    public static final String ID = makeID("CardThunderDemonQi");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.SPECIAL,
            CardTarget.ENEMY,
            3
    );

    private int growthThisCombat = 0; // 记录该卡在当前战斗中已经成长的次数
    private int timesUsedInCombat = 0; // 记录该卡在当前战斗中打出的次数 (用于减费)

    public CardThunderDemonQi() {
        super(ID, info);
        this.misc = 0;
        setDamage(5, 3); // 基础5，升级+3变8
        setMagic(3);     // 成长上限
        this.exhaust = false; 
        tags.add(CustomTags.EIGHT_DEMONS);
    }

    @Override
    public void applyPowers() {
        // 根据misc更新基础伤害 (跨战局永久成长)
        this.baseDamage = (this.upgraded ? 8 : 5) + this.misc;
        // 根据本场战斗的使用次数更新耗能，最低 0 (仅限本场战斗)
        int newCost = Math.max(0, 3 - this.timesUsedInCombat);
        if (this.cost != newCost) {
            this.cost = newCost;
            if (!this.isCostModifiedForTurn) {
                this.costForTurn = this.cost;
            }
            this.upgradedCost = true;
        }
        super.applyPowers();
        this.initializeDescription();
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        this.baseDamage = (this.upgraded ? 8 : 5) + this.misc;
        super.calculateCardDamage(m);
        // 如果目标有潮湿或高温蒸汽，伤害额外提升100% (不叠加)
        if (m != null && (m.hasPower(basicmod.powers.SoakedPower.POWER_ID) || m.hasPower(basicmod.powers.SteamPower.POWER_ID))) {
            this.damage = (int)(this.damage * 2.0F);
        }
        this.isDamageModified = this.damage != this.baseDamage;
        this.initializeDescription();
    }

    @Override
    public void update() {
        super.update();
        // 核心修复：Slay the Spire 在 SL 时虽然会读取 misc，但并不会主动触发更新 baseDamage。
        // 通过监听 update()，确保任何时候（包含查看牌库时）基础伤害都能正确反映永久成长的数值。
        int expectedBaseDamage = (this.upgraded ? 8 : 5) + this.misc;
        if (this.baseDamage != expectedBaseDamage) {
            this.baseDamage = expectedBaseDamage;
            this.initializeDescription();
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 先锁定涨点之前的伤害威力，确保这一发不吃到即时加成
        int damageBeforeGrowth = this.damage;
        
        // 增加本场战斗的使用计数
        this.timesUsedInCombat++;
        
        // 永久增加伤害 (上限检测) - 直接在 use 中处理，确保斩杀最后一名敌人时也能稳健成长
        if (growthThisCombat < 3) {
            growthThisCombat++; // 本实例内累加
            this.misc += 1;
            
            // 同步主卡组中对应的单张卡牌，确保永久成长被保存 (仅同步伤害增益 misc)
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c.uuid.equals(uuid)) {
                    c.misc += 1;
                    c.applyPowers();
                    c.baseDamage = (c.upgraded ? 8 : 5) + c.misc;
                    c.isDamageModified = false;
                }
            }
            // 注意：这里不再调用 this.applyPowers()，这样本次 DamageAction 就会维持锁定前的威力
        }

        // 造成伤害 (使用锁定好的旧威力)
        addToBot(new DamageAction(m, new DamageInfo(p, damageBeforeGrowth, this.damageTypeForTurn), AbstractGameAction.AttackEffect.LIGHTNING));
    }
}
