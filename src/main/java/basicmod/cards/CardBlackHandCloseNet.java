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

public class CardBlackHandCloseNet extends BaseCard {
    public static final String ID = makeID("BlackHandCloseNet");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            2
    );

    public CardBlackHandCloseNet() {
        super(ID, info);
        setDamage(12, 4);
        tags.add(CustomTags.blackhand);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        final int hitDamage = this.damage;
        final DamageInfo.DamageType hitType = this.damageTypeForTurn;

        addToBot(new DamageAction(
                m,
                new DamageInfo(p, hitDamage, hitType),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY
        ));

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (m == null || m.isDeadOrEscaped()) {
                    this.isDone = true;
                    return;
                }

                int stacks = BlackHandPower.getAmount(m);
                int extraHits = stacks / 4;
                if (extraHits > 0) {
                    BlackHandPower.consume(m, p, extraHits * 4);
                    for (int i = 0; i < extraHits; i++) {
                        addToBot(new DamageAction(
                                m,
                                new DamageInfo(p, hitDamage, hitType),
                                AbstractGameAction.AttackEffect.BLUNT_LIGHT
                        ));
                    }
                }
                this.isDone = true;
            }
        });
    }
}
