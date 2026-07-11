package basicmod.cards.fusion;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.helpers.FusionHelper;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

/**
 * 生肖大阵 - 3费能力
 * 每回合开始时，若持有动物符咒遗物，获得1点能量与1层力量（呼应符咒曾助龙小组败敌）。
 */
public class ZodiacArray extends BaseCard {
    public static final String ID = makeID(ZodiacArray.class.getSimpleName());
    private static final CardStats stats = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.POWER,
            CardRarity.RARE,
            CardTarget.SELF,
            3
    );

    public ZodiacArray() {
        super(ID, stats);
        setMagic(1, 1);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int talismanCount = FusionHelper.countTalismanRelics();
        // 每回合+1能量（用EnergizedPower近似"召唤兵助战"）
        addToBot(new com.megacrit.cardcrawl.actions.common.ApplyPowerAction(
                p, p, new com.megacrit.cardcrawl.powers.EnergizedPower(p, this.magicNumber), this.magicNumber));
        // 持有符咒遗物时额外+1力量
        if (talismanCount > 0) {
            addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(2);
            initializeDescription();
        }
    }
}
