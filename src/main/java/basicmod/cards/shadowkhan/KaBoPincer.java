package basicmod.cards.shadowkhan;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;

public class KaBoPincer extends BaseShadowKhanCard {
    public static final String ID = BasicMod.makeID(KaBoPincer.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardRarity.SPECIAL,
            AbstractCard.CardTarget.ENEMY,
            0
    );

    public KaBoPincer() {
        super(ID, info);
        setBlock(3, 3); // 3 block, +3 on upgrade -> 6 block
        setMagic(1, 0); // 1 Weak
        tags.add(CustomTags.SHADOW_KHAN);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, block));
        addToBot(new ApplyPowerAction(m, p, new WeakPower(m, magicNumber, false), magicNumber));
    }
}
