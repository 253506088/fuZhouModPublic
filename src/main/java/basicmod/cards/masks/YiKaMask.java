package basicmod.cards.masks;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.powers.BaseMaskPower;
import basicmod.powers.masks.YiKaPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class YiKaMask extends BaseMaskCard {
    public static final String ID = BasicMod.makeID(YiKaMask.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.POWER,
            AbstractCard.CardRarity.RARE,
            AbstractCard.CardTarget.SELF,
            2
    );

    public YiKaMask() {
        super(ID, info);
        this.cardsToPreview = new basicmod.cards.shadowkhan.YiKaSamurai();
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
        return new YiKaPower(p, amount);
    }

    @Override
    public String getMaskPowerId() {
        return YiKaPower.POWER_ID;
    }
}
