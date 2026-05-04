package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.BlackHandPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardBlackHandFirstStrike extends BaseCard {
    public static final String ID = makeID("BlackHandFirstStrike");
    private static final int APPLY_IF_NO_BLACKHAND = 2;
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.COMMON,
            CardTarget.ENEMY,
            1
    );

    public CardBlackHandFirstStrike() {
        super(ID, info);
        setDamage(8, 3);
        setMagic(6);
        tags.add(CustomTags.blackhand);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int totalDamage = this.damage;
        if (BlackHandPower.hasAny(m)) {
            totalDamage += this.magicNumber;
        } else {
            BlackHandPower.apply(m, p, APPLY_IF_NO_BLACKHAND);
        }
        addToBot(new DamageAction(
                m,
                new DamageInfo(p, totalDamage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL
        ));
    }
}
