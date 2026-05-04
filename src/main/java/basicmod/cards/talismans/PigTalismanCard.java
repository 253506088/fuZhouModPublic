package basicmod.cards.talismans;

import basicmod.cards.BaseCard;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import basicmod.actions.PigTalismanAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import basicmod.enums.CharacterEnums;

public class PigTalismanCard extends BaseCard {
    public static final String ID = makeID("PigTalismanCard");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.RARE,
            CardTarget.ENEMY,
            3
    );

    public PigTalismanCard() {
        super(ID, info);
        tags.add(CustomTags.TALISMAN_CARD);
        setDamage(6, 6);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null) {
            addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
            addToBot(new PigTalismanAction(m));
        }
    }
}
