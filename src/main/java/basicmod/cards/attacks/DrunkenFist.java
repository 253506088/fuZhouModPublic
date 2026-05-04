package basicmod.cards.attacks;

import basicmod.actions.DrunkenFistAction;
import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DrunkenFist extends BaseCard {
    public static final String ID = makeID(DrunkenFist.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.SPECIAL,
            CardTarget.ALL_ENEMY,
            -1
    );

    public DrunkenFist() {
        super(ID, info);
        tags.add(CustomTags.TEAM_JACKIE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DrunkenFistAction(p, this.energyOnUse, this.upgraded, this.freeToPlayOnce));
    }
}
