package basicmod.cards.masks;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.powers.BaseMaskPower;
import basicmod.powers.masks.SaMoPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class SaMoMask extends BaseMaskCard {
    public static final String ID = BasicMod.makeID(SaMoMask.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.POWER,
            AbstractCard.CardRarity.COMMON,
            AbstractCard.CardTarget.SELF,
            1
    );

    public SaMoMask() {
        super(ID, info);
        this.cardsToPreview = new basicmod.cards.shadowkhan.SaMoTroll();
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
        return new SaMoPower(p, amount);
    }

    @Override
    public String getMaskPowerId() {
        return SaMoPower.POWER_ID;
    }
}
