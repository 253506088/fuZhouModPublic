package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.BlackHandPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardBlackHandExtortion extends BaseCard {
    public static final String ID = makeID("BlackHandExtortion");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            1
    );

    public CardBlackHandExtortion() {
        super(ID, info);
        setDamage(7, 3);
        tags.add(CustomTags.blackhand);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        final int goldGain = BlackHandPower.getAmount(m) / 3;

        addToBot(new DamageAction(
                m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_HEAVY
        ));

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (goldGain > 0) {
                    p.gainGold(goldGain);
                    CardCrawlGame.sound.play("GOLD_JINGLE");
                }
                if (m != null && !m.isDeadOrEscaped()) {
                    BlackHandPower.consume(m, p, 2);
                }
                this.isDone = true;
            }
        });
    }
}
