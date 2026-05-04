package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.BlackHandPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardBlackHandStrike extends BaseCard {
    public static final String ID = makeID("BlackHandStrike");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            1
    );

    public CardBlackHandStrike() {
        super(ID, info);
        setDamage(6, 2);
        setMagic(2);
        tags.add(CustomTags.blackhand);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(
                m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_HORIZONTAL
        ));

        if (!BlackHandPower.hasAny(m)) {
            BlackHandPower.apply(m, p, this.magicNumber);
        } else {
            addToBot(new DrawCardAction(p, 1));
            BlackHandPower.apply(m, p, 1);
        }
    }
}
