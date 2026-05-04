package basicmod.cards.talismans;

import basicmod.cards.BaseCard;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import basicmod.powers.RoosterTalismanPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import basicmod.enums.CharacterEnums;

public class RoosterTalismanCard extends BaseCard {
    public static final String ID = makeID("RoosterTalismanCard");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.SELF,
            2
    );

    public RoosterTalismanCard() {
        super(ID, info);
        tags.add(CustomTags.TALISMAN_CARD);
        setMagic(2, 2); // Dex amount
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new RoosterTalismanPower(p, magicNumber), RoosterTalismanPower.MAX_HITS));
    }
}
