package basicmod.cards.demons;

import basicmod.cards.BaseCard;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MoonChoiceEnemy extends BaseCard {
    public static final String ID = makeID("MoonChoiceEnemy");
    private static final CardStats info = new CardStats(
            CardColor.COLORLESS,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.NONE,
            -2
    );

    public MoonChoiceEnemy() {
        super(ID, info);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {}

    @Override
    public void onChoseThisOption() {
        MoonTargetEnemy c = new MoonTargetEnemy();
        if (this.upgraded) {
            c.upgrade();
        }
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(c, 1));
    }
}
