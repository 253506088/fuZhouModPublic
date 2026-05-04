package basicmod.cards.shadowkhan;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import com.megacrit.cardcrawl.powers.WeakPower;


public class ManNiMantis extends BaseShadowKhanCard {
    public static final String ID = BasicMod.makeID(ManNiMantis.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.ATTACK,
            AbstractCard.CardRarity.SPECIAL,
            AbstractCard.CardTarget.ENEMY,
            0
    );

    public ManNiMantis() {
        super(ID, info);
        setDamage(3, 2); // 3 damage, +2 -> 5
        tags.add(CustomTags.SHADOW_KHAN);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        
        int extraHits = 0;
        if (m != null) {
            if (m.hasPower(com.megacrit.cardcrawl.powers.VulnerablePower.POWER_ID)) {
                extraHits += m.getPower(com.megacrit.cardcrawl.powers.VulnerablePower.POWER_ID).amount;
            }
            if (m.hasPower(WeakPower.POWER_ID)) {
                extraHits += m.getPower(WeakPower.POWER_ID).amount;
            }
            if (m.hasPower(basicmod.powers.LeiSuCursePower.POWER_ID)) {
                extraHits += m.getPower(basicmod.powers.LeiSuCursePower.POWER_ID).amount;
            }
        }
        
        // 封顶最多重复3次，即自带1次打击+3=4最高总打击次数
        extraHits = Math.min(extraHits, 3);
        
        for (int i = 0; i < extraHits; i++) {
            addToBot(new DamageAction(m, new DamageInfo(p, damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        }
    }
}
