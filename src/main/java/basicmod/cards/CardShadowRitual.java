package basicmod.cards;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.DominionPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardShadowRitual extends BaseCard {
    public static final String ID = makeID("ShadowRitual");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            1
    );

    public CardShadowRitual() {
        super(ID, info);
        tags.add(CustomTags.MASK_SUPPORT);
        // 2(3) 层影蚀。
        setMagic(2, 1);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        BasicMod.logger.info("【潜影仪式】打出：获得影蚀={}，失去生命=5", this.magicNumber);
        addToBot(new ApplyPowerAction(p, p, new DominionPower(p, this.magicNumber), this.magicNumber));
        addToBot(new LoseHPAction(p, p, 5));
    }
}
