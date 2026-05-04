package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.BlackHandPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardBlackHandBlackMarketDeal extends BaseCard {
    public static final String ID = makeID("BlackHandBlackMarketDeal");
    private static final int BASE_GOLD_LOSS = 15;
    private static final int UPGRADE_GOLD_LOSS = 15;
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.SELF,
            0
    );

    public CardBlackHandBlackMarketDeal() {
        super(ID, info);
        setMagic(1, 1);
        tags.add(CustomTags.blackhand);
    }

    private int getGoldLoss() {
        return this.upgraded ? UPGRADE_GOLD_LOSS : BASE_GOLD_LOSS;
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        if (!super.canUse(p, m)) {
            return false;
        }
        if (p == null || p.gold < getGoldLoss()) {
            this.cantUseMessage = (cardStrings != null
                    && cardStrings.EXTENDED_DESCRIPTION != null
                    && cardStrings.EXTENDED_DESCRIPTION.length > 0)
                    ? cardStrings.EXTENDED_DESCRIPTION[0]
                    : "Not enough Gold.";
            return false;
        }
        return true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        p.loseGold(getGoldLoss());
        CardCrawlGame.sound.play("GOLD_JINGLE");
        addToBot(new GainEnergyAction(1));
        addToBot(new DrawCardAction(p, this.magicNumber));

        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                BlackHandPower.apply(mo, p, 1);
            }
        }
    }
}
