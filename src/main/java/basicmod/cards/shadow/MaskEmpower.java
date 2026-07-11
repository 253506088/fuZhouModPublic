package basicmod.cards.shadow;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

/**
 * 面具·赋予 - 1费技能
 * 获得1层力量与1层敏捷。
 */
public class MaskEmpower extends BaseCard {
    public static final String ID = makeID(MaskEmpower.class.getSimpleName());
    private static final CardStats stats = new CardStats(
            CharacterEnums.SHENGZHU_COLOR, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, 1);

    public MaskEmpower() { super(ID, stats); setMagic(1, 1); }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));
        addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!upgraded) { upgradeName(); upgradeMagicNumber(1); initializeDescription(); }
    }
}
