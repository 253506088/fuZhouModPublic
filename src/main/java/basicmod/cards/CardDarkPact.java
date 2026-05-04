package basicmod.cards;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.DominionPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class CardDarkPact extends BaseCard {
    public static final String ID = makeID("DarkPact");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.SELF,
            1
    );

    public CardDarkPact() {
        super(ID, info);
        tags.add(CustomTags.MASK_SUPPORT);
        // 1(2) 层影蚀。
        setMagic(1, 1);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        BasicMod.logger.info("【黑暗契约】打出：获得影蚀={}，失去力量=2", this.magicNumber);
        addToBot(new ApplyPowerAction(p, p, new DominionPower(p, this.magicNumber), this.magicNumber));
        addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, -2), -2));
    }
}
