package basicmod.cards.jteam;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ThornsPower;

/**
 * 牛战士·摔角王 - 1费技能
 * 获得3格挡与1层反伤（牛战士=墨西哥摔跤手，从不摘下面具）。
 */
public class BullWarrior extends BaseCard {
    public static final String ID = makeID(BullWarrior.class.getSimpleName());
    private static final CardStats stats = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            1
    );

    public BullWarrior() {
        super(ID, stats);
        setBlock(3, 2);
        setMagic(1, 1);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, this.block));
        addToBot(new ApplyPowerAction(p, p, new ThornsPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBlock(2);
            upgradeMagicNumber(1);
            initializeDescription();
        }
    }
}
