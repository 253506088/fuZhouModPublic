package basicmod.cards.skills;

import basicmod.actions.PeelMaskAction;
import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PeelMask extends BaseCard {
    public static final String ID = makeID(PeelMask.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.NONE,
            1
    );

    public PeelMask() {
        super(ID, info);
        tags.add(CustomTags.MASK_SUPPORT);
        setCostUpgrade(0);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new PeelMaskAction(this.upgraded));
    }
}
