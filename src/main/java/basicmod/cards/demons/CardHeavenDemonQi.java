package basicmod.cards.demons;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.HeavenDemonPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardHeavenDemonQi extends BaseCard {
    public static final String ID = makeID("CardHeavenDemonQi");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.POWER,
            CardRarity.SPECIAL,
            CardTarget.SELF,
            2
    );

    public CardHeavenDemonQi() {
        super(ID, info);
        setCostUpgrade(1); // 升级后费用降为1
        tags.add(CustomTags.EIGHT_DEMONS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new HeavenDemonPower(p, p, 1), 1));
    }
}
