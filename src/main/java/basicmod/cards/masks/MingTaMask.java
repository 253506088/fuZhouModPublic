package basicmod.cards.masks;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.powers.BaseMaskPower;
import basicmod.powers.masks.MingTaPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class MingTaMask extends BaseMaskCard {
    public static final String ID = BasicMod.makeID(MingTaMask.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.POWER,
            CardRarity.UNCOMMON,
            AbstractCard.CardTarget.SELF,
            2
    );

    public MingTaMask() {
        super(ID, info);
        this.cardsToPreview = new basicmod.cards.shadowkhan.MingTaShadow();
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
        return new MingTaPower(p, amount);
    }

    @Override
    public String getMaskPowerId() {
        return MingTaPower.POWER_ID;
    }
}
