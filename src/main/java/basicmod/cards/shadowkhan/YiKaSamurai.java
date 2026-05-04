package basicmod.cards.shadowkhan;

import basicmod.BasicMod;
import basicmod.actions.YiKaExhaustAction; // Added import
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class YiKaSamurai extends BaseShadowKhanCard {
    public static final String ID = BasicMod.makeID(YiKaSamurai.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.ATTACK,
            AbstractCard.CardRarity.SPECIAL,
            AbstractCard.CardTarget.ENEMY,
            0
    );

    public YiKaSamurai() {
        super(ID, info);
        setDamage(6, 3); // 6 damage, +3 -> 9
        tags.add(CustomTags.SHADOW_KHAN);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 造成基础伤害
        addToBot(new DamageAction(m, new DamageInfo(p, damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HEAVY));
        // 触发消耗逻辑
        addToBot(new YiKaExhaustAction(m));
    }
}
