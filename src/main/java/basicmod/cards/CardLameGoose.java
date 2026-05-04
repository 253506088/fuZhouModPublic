package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class CardLameGoose extends BaseCard {
    public static final String ID = makeID("LameGoose");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.SELF,
            0);

    private static final int BLOCK = 4;
    private static final int UPG_BLOCK = 2;
    private static final int STR = 1;

    public CardLameGoose() {
        super(ID, info);
        setBlock(BLOCK, UPG_BLOCK);
        setMagic(STR);
        tags.add(CustomTags.afu);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, block));

        if (isAfuComboActive()) {
            addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, magicNumber), magicNumber));
            addToBot(new ApplyPowerAction(p, p, new LoseStrengthPower(p, magicNumber), magicNumber));
        }
    }

    @Override
    public void triggerOnGlowCheck() {
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        if (isAfuComboActive()) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
    }
}
