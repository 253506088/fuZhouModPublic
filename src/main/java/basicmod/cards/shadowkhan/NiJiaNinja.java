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

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    }
}
