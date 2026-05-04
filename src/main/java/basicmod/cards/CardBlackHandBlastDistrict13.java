package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.BlackHandPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class CardBlackHandBlastDistrict13 extends BaseCard {
    public static final String ID = makeID("BlackHandBlastDistrict13");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.RARE,
            CardTarget.ALL_ENEMY,
            1
    );

    public CardBlackHandBlastDistrict13() {
        super(ID, info);
        setMagic(2, 1);
        tags.add(CustomTags.blackhand);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                int totalConsumed = 0;
                for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (mo.isDeadOrEscaped()) {
                        continue;
                    }
                    int consumed = BlackHandPower.consumeAll(mo, p);
                    totalConsumed += consumed;
                    if (consumed > 0) {
                        addToBot(new DamageAction(
                                mo,
                                new DamageInfo(p, consumed * magicNumber, DamageInfo.DamageType.NORMAL),
                                AbstractGameAction.AttackEffect.FIRE
                        ));
                    }
                }

                if (totalConsumed >= 8) {
                    int debuffAmount = upgraded ? 2 : 1;
                    for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                        if (mo.isDeadOrEscaped()) {
                            continue;
                        }
                        addToBot(new ApplyPowerAction(mo, p, new VulnerablePower(mo, debuffAmount, false), debuffAmount));
                        addToBot(new ApplyPowerAction(mo, p, new WeakPower(mo, debuffAmount, false), debuffAmount));
                    }
                }
                this.isDone = true;
            }
        });
    }
}
