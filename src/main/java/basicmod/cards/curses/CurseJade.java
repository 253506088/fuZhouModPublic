package basicmod.cards.curses;

import basicmod.BasicMod;
import basicmod.actions.CurseJadeDeferredAction;
import basicmod.cards.BaseCard;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CurseJade extends BaseCard {
    public static final String ID = makeID("CurseJade");
    private static final CardStats info = new CardStats(
            CardColor.CURSE,
            CardType.CURSE,
            CardRarity.CURSE,
            CardTarget.NONE,
            -2);

    public CurseJade() {
        super(ID, info);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void triggerWhenDrawn() {
        super.triggerWhenDrawn();
        if (AbstractDungeon.player == null) {
            return;
        }
        BasicMod.logger.info("【小玉诅咒】抽到小玉，效果将延后到抽牌结算后触发。");
        addToBot(new CurseJadeDeferredAction());
    }
}
