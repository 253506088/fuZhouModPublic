package basicmod.cards.masks;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.powers.BaseMaskPower;
import basicmod.powers.masks.LeiSuPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class LeiSuMask extends BaseMaskCard {
    public static final String ID = BasicMod.makeID(LeiSuMask.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.POWER,
            AbstractCard.CardRarity.UNCOMMON,
            AbstractCard.CardTarget.SELF,
            2
    );

    public LeiSuMask() {
        super(ID, info);
        this.cardsToPreview = new basicmod.cards.shadowkhan.LeiSuAlien();
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
        return new LeiSuPower(p, amount);
    }

    @Override
    public String getMaskPowerId() {
        return LeiSuPower.POWER_ID;
    }
}
