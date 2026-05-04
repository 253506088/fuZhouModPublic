package basicmod.cards.demons;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.powers.EightDemonPossessionPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

@basemod.AutoAdd.Ignore
public class CardEightDemonPossession extends BaseCard {
    public static final String ID = makeID("CardEightDemonPossession");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.POWER,
            CardRarity.SPECIAL,
            CardTarget.SELF,
            2
    );

    public CardEightDemonPossession() {
        super(ID, info);
        setInnate(false, true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new EightDemonPossessionPower(p), 1));
    }
}
