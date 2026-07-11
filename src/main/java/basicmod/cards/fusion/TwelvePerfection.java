package basicmod.cards.fusion;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.helpers.FusionHelper;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

/**
 * 十二圆满 - 0费技能
 * 若已集齐12符咒遗物，本场战斗获得2能量与2力量（圣主恢复全盛）。否则抽1张牌。
 */
public class TwelvePerfection extends BaseCard {
    public static final String ID = makeID(TwelvePerfection.class.getSimpleName());
    private static final CardStats stats = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.SELF,
            0
    );

    public TwelvePerfection() {
        super(ID, stats);
        setMagic(2, 1);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int talismanCount = FusionHelper.countTalismanRelics();
        if (talismanCount >= 12) {
            addToBot(new GainEnergyAction(this.magicNumber));
            addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));
        } else {
            addToBot(new DrawCardAction(p, 1));
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
            initializeDescription();
        }
    }
}
