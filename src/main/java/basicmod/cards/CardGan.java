package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.powers.GanPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class CardGan extends BaseCard {
    public static final String ID = makeID("Gan");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.SPECIAL,
            CardTarget.ENEMY,
            1);

    public CardGan() {
        super(ID, info);
        setDamage(8, 4); // 8 -> 12
        setMagic(upgraded ? 40 : 30); // 记录暴击率
        this.misc = upgraded ? 40 : 30;
        tags.add(basicmod.enums.CustomTags.DAOLONG_DARK_ASSASSIN);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int finalDamage = this.damage;
        
        // 暴击判定：基础暴击率 + 全局增长次数*10 (通过 GanPower 记录)
        // 老大爷建议初始暴击率改为 30%
        int baseCrit = upgraded ? 40 : 30;
        int growthCount = 0;
        if (p.hasPower(GanPower.POWER_ID)) {
            growthCount = p.getPower(GanPower.POWER_ID).amount;
        }
        int currentCrit = baseCrit + growthCount * 10;
        // 老大爷说最高就 100% 暴击，再高也没意义了，咱们给它封个顶
        if (currentCrit > 100) currentCrit = 100;
        
        // 老大爷，我在这里改成了基于 Power 的全局计数，确保同局战斗内所有【甘】共享成长
        basicmod.BasicMod.logger.debug("【甘】卡牌使用 - 当前暴击率: {}% (基础: {}, 全局增长次数: {})", currentCrit, baseCrit, growthCount);
        
        int rng = AbstractDungeon.cardRandomRng.random(1, 100);
        boolean critted = rng <= currentCrit;
        
        if (critted) {
            finalDamage = (int)(finalDamage * 1.5f);
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, AbstractGameAction.AttackEffect.FIRE, false));
        }
        
        basicmod.BasicMod.logger.debug("【甘】决策判定 - 随机数: {}, 是否暴击: {}, 最终伤害: {}", rng, critted, finalDamage);
        
        addToBot(new DamageAction(m, new DamageInfo(p, finalDamage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HEAVY));
        
        // 增加全局暴击成长
        addToBot(new ApplyPowerAction(p, p, new GanPower(p, 1), 1));
        
        // 日志记录下一次（也封个顶显示）
        int nextCrit = Math.min(100, baseCrit + (growthCount + 1) * 10);
        basicmod.BasicMod.logger.debug("【甘】魔力增长 - 期待下次暴击率: {}%", nextCrit);
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        // 动态更新描述中的 magicNumber 仅用于显示
        int baseCrit = upgraded ? 40 : 30;
        int growthCount = 0;
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasPower(GanPower.POWER_ID)) {
            growthCount = AbstractDungeon.player.getPower(GanPower.POWER_ID).amount;
        }
        int totalCrit = baseCrit + growthCount * 10;
        if (totalCrit > 100) totalCrit = 100; // 描述里也封顶显示 100
        
        this.baseMagicNumber = totalCrit;
        this.magicNumber = this.baseMagicNumber;
        this.isMagicNumberModified = (growthCount > 0);
    }
}
