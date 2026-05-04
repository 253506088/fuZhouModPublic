package basicmod.cards.curses;

import basicmod.cards.BaseCard;
import basicmod.powers.GrandMageCurseTaxAttackPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CurseTohru extends BaseCard {
    public static final String ID = makeID("CurseTohru");
    private static final CardStats info = new CardStats(
            CardColor.CURSE,
            CardType.CURSE,
            CardRarity.CURSE,
            CardTarget.NONE,
            -2);

    public CurseTohru() {
        super(ID, info);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void triggerWhenDrawn() {
        super.triggerWhenDrawn();
        if (AbstractDungeon.player != null) {
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    new GrandMageCurseTaxAttackPower(AbstractDungeon.player, 1), 1));
        }
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        if (AbstractDungeon.player == null || AbstractDungeon.actionManager == null) {
            return;
        }
        boolean playedAttack = AbstractDungeon.actionManager.cardsPlayedThisTurn.stream()
                .anyMatch(c -> c != null && c.type == AbstractCard.CardType.ATTACK);
        if (!playedAttack) {
            addToBot(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, 6));
        }
    }
}

