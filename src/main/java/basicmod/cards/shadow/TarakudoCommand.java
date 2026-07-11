package basicmod.cards.shadow;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.EnergizedPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

/**
 * 塔拉库多之令 - 2费能力
 * 每回合获得1点能量与1层力量。
 */
public class TarakudoCommand extends BaseCard {
    public static final String ID = makeID(TarakudoCommand.class.getSimpleName());
    private static final CardStats stats = new CardStats(
            CharacterEnums.SHENGZHU_COLOR, CardType.POWER, CardRarity.RARE, CardTarget.SELF, 2);

    public TarakudoCommand() { super(ID, stats); setMagic(1, 1); }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new EnergizedPower(p, this.magicNumber), this.magicNumber));
        addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!upgraded) { upgradeName(); upgradeBaseCost(1); initializeDescription(); }
    }
}
