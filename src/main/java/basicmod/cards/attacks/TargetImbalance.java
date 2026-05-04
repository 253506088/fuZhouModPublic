package basicmod.cards.attacks;

import basicmod.actions.ChangeIntentAction;
import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TargetImbalance extends BaseCard {
    public static final String ID = makeID(TargetImbalance.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            com.megacrit.cardcrawl.cards.AbstractCard.CardType.ATTACK,
            com.megacrit.cardcrawl.cards.AbstractCard.CardRarity.SPECIAL,
            com.megacrit.cardcrawl.cards.AbstractCard.CardTarget.ENEMY,
            1
    );

    public TargetImbalance() {
        super(ID, info);
        setDamage(6, 3);
        setExhaust(true);
        tags.add(CustomTags.TEAM_JACKIE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (m.lastDamageTaken > 0 && !m.isDeadOrEscaped()) {
                   addToTop(new ChangeIntentAction(m));
                }
                this.isDone = true;
            }
        });
    }
}
