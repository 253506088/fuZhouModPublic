package basicmod.cards.skills;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.EnergizedPower;
import com.megacrit.cardcrawl.powers.EquilibriumPower;

public class HiddenDragon extends BaseCard {
    public static final String ID = makeID(HiddenDragon.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardRarity.COMMON,
            AbstractCard.CardTarget.SELF,
            1
    );

    public HiddenDragon() {
        super(ID, info);
        setBlock(6, 3);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, block));
        addToBot(new ApplyPowerAction(p, p, new EquilibriumPower(p, 1), 1));
        addToBot(new ApplyPowerAction(p, p, new EnergizedPower(p, 1), 1));
        addToBot(new PressEndTurnButtonAction());
    }
}
