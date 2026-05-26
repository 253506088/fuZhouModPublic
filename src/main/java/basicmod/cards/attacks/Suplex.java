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
        setDamage(6, 3); // 基础 6 点，升级 +3 = 9 点
        setMagic(6, 3);  // 用 magicNumber 展示基础伤害数值
        tags.add(CustomTags.TEAM_JACKIE);
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        // 计算时临时注入目标最大生命值 1/8 的固定伤害
        int realBase = this.baseDamage;
        if (m != null) {
            this.baseDamage += (int)(m.maxHealth * 0.125);
        }
        super.calculateCardDamage(m);
        // 恢复原始 baseDamage 保证 applyPowers 时数值不被污染，UI 上的 damage 已经是计算后的了
        this.baseDamage = realBase;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 计算 1/8 最大生命值部分
        int hpBonus = (int)(m.maxHealth * 0.125);
        // 打印中文日志，列出计算式
        basicmod.BasicMod.logger.debug(">>> [抱摔] 伤害计算式: [基础({}) + 1/8最大生命值({})] * 力量/虚弱补正 = {}",
                this.upgraded ? 9 : 6, hpBonus, this.damage);

        // calculateCardDamage 在被打出时已由引擎调用，直接使用 this.damage
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SMASH));
    }
}
