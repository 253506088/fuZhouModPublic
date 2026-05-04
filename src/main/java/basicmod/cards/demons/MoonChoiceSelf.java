package basicmod.cards.demons;

import basicmod.cards.BaseCard;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MoonChoiceSelf extends BaseCard {
    public static final String ID = makeID("MoonChoiceSelf");
    private static final CardStats info = new CardStats(
            CardColor.COLORLESS,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.NONE,
            -2
    );

    public MoonChoiceSelf() {
        super(ID, info);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {}

    @Override
    public void onChoseThisOption() {
        CardMoonDemonQi.invertStats(AbstractDungeon.player);
    }
}
