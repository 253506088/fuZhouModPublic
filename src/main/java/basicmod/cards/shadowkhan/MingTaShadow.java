package basicmod.cards.shadowkhan;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.helpers.MaskManager;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MingTaShadow extends BaseShadowKhanCard {
    public static final String ID = BasicMod.makeID(MingTaShadow.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.ATTACK,
            AbstractCard.CardRarity.SPECIAL,
            AbstractCard.CardTarget.ENEMY,
            0
    );

    public MingTaShadow() {
        super(ID, info);
        setDamage(2, 3); // 2 base, +3 -> 5
        tags.add(CustomTags.SHADOW_KHAN);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // Will implement the actual exhaust logic via an Action
        addToBot(new basicmod.actions.MingTaExhaustAction(m, damage, this));
    }
}
