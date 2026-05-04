package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.BlackHandPullStringsPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardBlackHandPullStrings extends BaseCard {
    public static final String ID = makeID("BlackHandPullStrings");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.POWER,
            CardRarity.RARE,
            CardTarget.SELF,
            1
    );

    public CardBlackHandPullStrings() {
        super(ID, info);
        setInnate(false, true);
        tags.add(CustomTags.blackhand);
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            super.upgrade();
            // Ensure upgraded state always grants Innate, even for existing saved decks.
            this.isInnate = true;
            if (cardStrings.UPGRADE_DESCRIPTION != null) {
                this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
                this.initializeDescription();
            }
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new BlackHandPullStringsPower(p, 1), 1));
    }
}
