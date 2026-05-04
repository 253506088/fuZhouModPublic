package basicmod.cards.shadowkhan;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BaTeBat extends BaseShadowKhanCard {
    public static final String ID = BasicMod.makeID(BaTeBat.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.ATTACK,
            AbstractCard.CardRarity.SPECIAL,
            AbstractCard.CardTarget.ENEMY,
            0
    );

    public BaTeBat() {
        super(ID, info);
        setDamage(3, 2); // 3 damage, +2 on upgrade -> 5
        setMagic(2, 0); // Draw 2
        tags.add(CustomTags.SHADOW_KHAN);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        addToBot(new DrawCardAction(p, magicNumber));
        
        // 增加诅咒联动：吸血
        if (m != null && m.hasPower(basicmod.powers.LeiSuCursePower.POWER_ID)) {
            addToBot(new com.megacrit.cardcrawl.actions.common.HealAction(p, p, 1));
        }
    }
}
