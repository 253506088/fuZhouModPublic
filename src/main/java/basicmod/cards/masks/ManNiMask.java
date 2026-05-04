package basicmod.cards.masks;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.powers.BaseMaskPower;
import basicmod.powers.masks.ManNiPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class ManNiMask extends BaseMaskCard {
    public static final String ID = BasicMod.makeID(ManNiMask.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.POWER,
            AbstractCard.CardRarity.UNCOMMON,
            AbstractCard.CardTarget.SELF,
            2
    );

    public ManNiMask() {
        super(ID, info);
        this.cardsToPreview = new basicmod.cards.shadowkhan.ManNiMantis();
    }

    @Override
    public void upgrade() {
        super.upgrade();
        if (this.cardsToPreview != null) {
            this.cardsToPreview.upgrade();
        }
    }

    @Override
    public BaseMaskPower getMaskPower(AbstractPlayer p, int amount) {
        return new ManNiPower(p, amount);
    }

    @Override
    public String getMaskPowerId() {
        return ManNiPower.POWER_ID;
    }
}
