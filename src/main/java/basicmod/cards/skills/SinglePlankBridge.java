package basicmod.cards.skills;

import basicmod.actions.SinglePlankBridgeAction;
import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class SinglePlankBridge extends BaseCard {
    public static final String ID = makeID(SinglePlankBridge.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.NONE,
            2
    );

    public SinglePlankBridge() {
        super(ID, info);
        tags.add(CustomTags.TEAM_JACKIE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new SinglePlankBridgeAction(upgraded));
    }
}
