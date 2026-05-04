package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.BlackHandPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardBlackHandSuppressiveFire extends BaseCard {
    public static final String ID = makeID("BlackHandSuppressiveFire");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.COMMON,
            CardTarget.ALL_ENEMY,
            1
    );

    public CardBlackHandSuppressiveFire() {
        super(ID, info);
        setDamage(4, 3);
        this.isMultiDamage = true;
        tags.add(CustomTags.blackhand);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAllEnemiesAction(
                p,
                this.multiDamage,
                this.damageTypeForTurn,
                AbstractGameAction.AttackEffect.SLASH_HORIZONTAL
        ));

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!mo.isDeadOrEscaped()) {
                        int extraDamage = BlackHandPower.getAmount(mo) / 2;
                        if (extraDamage <= 0) {
                            continue;
                        }
                        addToTop(new DamageAction(
                                mo,
                                new DamageInfo(p, extraDamage, DamageInfo.DamageType.NORMAL),
                                AbstractGameAction.AttackEffect.SLASH_HORIZONTAL
                        ));
                    }
                }
                this.isDone = true;
            }
        });
    }
}
