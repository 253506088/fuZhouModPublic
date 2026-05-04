package basicmod.cards.masks;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.powers.BaseMaskPower;
import basicmod.powers.masks.KaBoPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class KaBoMask extends BaseMaskCard {
    public static final String ID = BasicMod.makeID(KaBoMask.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.POWER,
            AbstractCard.CardRarity.COMMON,
            AbstractCard.CardTarget.SELF,
            1
    );

    public KaBoMask() {
        super(ID, info);
        this.cardsToPreview = new basicmod.cards.shadowkhan.KaBoPincer();
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
        return new KaBoPower(p, amount);
    }

    @Override
    public String getMaskPowerId() {
        return KaBoPower.POWER_ID;
    }
}
