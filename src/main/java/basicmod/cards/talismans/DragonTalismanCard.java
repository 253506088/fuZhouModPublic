package basicmod.cards.talismans;

import basicmod.cards.BaseCard;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import basicmod.actions.DragonTalismanAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import basicmod.enums.CharacterEnums;

public class DragonTalismanCard extends BaseCard {
    public static final String ID = makeID("DragonTalismanCard");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.RARE,
            CardTarget.ALL_ENEMY,
            2
    );

    public DragonTalismanCard() {
        super(ID, info);
        tags.add(CustomTags.TALISMAN_CARD);
        setDamage(15, 5);
        this.isMultiDamage = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DragonTalismanAction(p, multiDamage, damageTypeForTurn));
    }
}
