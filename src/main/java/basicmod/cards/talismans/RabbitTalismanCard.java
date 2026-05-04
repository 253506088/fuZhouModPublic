package basicmod.cards.talismans;

import basicmod.cards.BaseCard;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import basicmod.actions.RabbitAction;
import com.megacrit.cardcrawl.actions.common.EndTurnAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import basicmod.enums.CharacterEnums;

public class RabbitTalismanCard extends BaseCard {
    public static final String ID = makeID("RabbitTalismanCard");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.NONE,
            3
    );

    public RabbitTalismanCard() {
        super(ID, info);
        tags.add(CustomTags.TALISMAN_CARD);
        this.baseMagicNumber = this.magicNumber =  2;
        this.exhaust = true;
        setCostUpgrade(2);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new RabbitAction());
        addToBot(new EndTurnAction());
    }
}
