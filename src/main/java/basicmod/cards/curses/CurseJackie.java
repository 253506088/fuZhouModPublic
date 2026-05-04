package basicmod.cards.curses;

import basicmod.cards.BaseCard;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;

public class CurseJackie extends BaseCard {
    public static final String ID = makeID("CurseJackie");
    private static final CardStats info = new CardStats(
            CardColor.CURSE,
            CardType.CURSE,
            CardRarity.CURSE,
            CardTarget.NONE,
            -2);

    public CurseJackie() {
        super(ID, info);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        if (AbstractDungeon.player != null) {
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    new WeakPower(AbstractDungeon.player, 1, true), 1));
        }
    }
}

