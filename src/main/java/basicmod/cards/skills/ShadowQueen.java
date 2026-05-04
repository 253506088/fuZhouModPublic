package basicmod.cards.skills;

import basicmod.actions.ShadowQueenAction;
import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ShadowQueen extends BaseCard {
    public static final String ID = makeID(ShadowQueen.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.NONE,
            2
    );

    public ShadowQueen() {
        super(ID, info);
        tags.add(CustomTags.MASK_SUPPORT);
        this.setCostUpgrade(1); // 升级后费用降低至 1
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ShadowQueenAction());
    }
}


