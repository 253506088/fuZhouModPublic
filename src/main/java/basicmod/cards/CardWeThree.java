package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

@basemod.AutoAdd.Ignore
public class CardWeThree extends BaseCard {
    public static final String ID = makeID("WeThree");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.SPECIAL,
            CardTarget.ENEMY,
            1);

    public CardWeThree() {
        super(ID, info);
        // 造成 6 点伤害，升级变为 9 点 (+3)
        setDamage(6, 3);
        // 葱姜蒜 是基础攻击牌，所以打上 STARTER_STRIKE 和 STRIKE 标签
        tags.add(CardTags.STARTER_STRIKE);
        tags.add(CardTags.STRIKE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_LIGHT));
    }
}
