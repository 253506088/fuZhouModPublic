package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.BlackHandPower;
import basicmod.powers.BlackHandStrengthReturnPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class CardBlackHandSiegeRam extends BaseCard {
    public static final String ID = makeID("BlackHandSiegeRam");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            3
    );

    public CardBlackHandSiegeRam() {
        super(ID, info);
        setDamage(32, 5);
        tags.add(CustomTags.blackhand);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(
                m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY
        ));

        int currentBlackHand = BlackHandPower.getAmount(m);
        if (currentBlackHand < 3) {
            return;
        }

        int consumedBlackHand = BlackHandPower.consumeAll(m, p);
        int strengthLoss = consumedBlackHand / 3;
        if (strengthLoss > 0) {
            addToBot(new ApplyPowerAction(m, p, new StrengthPower(m, -strengthLoss), -strengthLoss));
            if (!m.hasPower(ArtifactPower.POWER_ID)) {
                addToBot(new ApplyPowerAction(m, p, new BlackHandStrengthReturnPower(m, 2, strengthLoss), strengthLoss));
            }
        }
    }
}
