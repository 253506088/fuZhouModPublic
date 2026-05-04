package basicmod.cards.shadowkhan;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class LaZuoBlade extends BaseShadowKhanCard {
    public static final String ID = BasicMod.makeID(LaZuoBlade.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.ATTACK,
            AbstractCard.CardRarity.SPECIAL,
            AbstractCard.CardTarget.ENEMY,
            0
    );

    public LaZuoBlade() {
        super(ID, info);
        setDamage(5, 3); // 5 damage, augments by +3 on upgrade -> 8
        setMagic(1, 0); // 1 Vulnerable
        tags.add(CustomTags.SHADOW_KHAN);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 计算最终伤害（含诅咒加成）
        int finalDamage = this.damage;
        addToBot(new DamageAction(m, new DamageInfo(p, finalDamage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HEAVY));
        
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (m != null && m.lastDamageTaken > 0) {
                    addToTop(new ApplyPowerAction(m, p, new VulnerablePower(m, magicNumber, false), magicNumber));
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        super.calculateCardDamage(m);
        if (m != null && m.hasPower(basicmod.powers.LeiSuCursePower.POWER_ID)) {
            this.damage = (int)(this.damage * 1.25F);
        }
        this.isDamageModified = this.damage != this.baseDamage;
    }
}
