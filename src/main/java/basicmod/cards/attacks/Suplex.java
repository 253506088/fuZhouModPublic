package basicmod.cards.attacks;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Suplex extends BaseCard {
    public static final String ID = makeID(Suplex.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.SPECIAL,
            CardTarget.ENEMY,
            2
    );

    public Suplex() {
        super(ID, info);
        setDamage(9, 3); // 基础 6 点，升级 +3 = 9 点
        tags.add(CustomTags.TEAM_JACKIE);
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        // 计算时临时注入目标 10% 最大生命值的固定伤害
        int realBase = this.baseDamage;
        if (m != null) {
            this.baseDamage += (int)(m.maxHealth * 0.06);
        }
        super.calculateCardDamage(m);
        // 恢复原始 baseDamage 保证 applyPowers 时数值不被污染，UI 上的 damage 已经是计算后的了
        this.baseDamage = realBase;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 计算 6% 最大生命值部分
        int hpBonus = (int)(m.maxHealth * 0.06);
        // 打印中文日志，列出计算式
        basicmod.BasicMod.logger.info(">>> [抱摔] 伤害计算式: [基础(" + (this.upgraded ? 9 : 6) + ") + 6%最大生命值(" + hpBonus + ")] * 力量/虚弱补正 = " + this.damage);

        // calculateCardDamage 在被打出时已由引擎调用，直接使用 this.damage
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SMASH));
    }
}
