package basicmod.cards.attacks;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Flatten extends BaseCard {
    public static final String ID = makeID(Flatten.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.SPECIAL,
            CardTarget.ENEMY,
            3
    );

    public Flatten() {
        super(ID, info);
        setCostUpgrade(2);
        tags.add(CustomTags.TEAM_JACKIE);
        this.baseDamage = 8;
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int realBase = this.baseDamage;
        this.baseDamage += calculateDeckSize();
        super.calculateCardDamage(mo);
        this.baseDamage = realBase;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void applyPowers() {
        int realBase = this.baseDamage;
        this.baseDamage += calculateDeckSize();
        super.applyPowers();
        this.baseDamage = realBase;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    private int calculateDeckSize() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) return 0;
        return p.drawPile.size() + p.hand.size() + p.discardPile.size() + p.exhaustPile.size();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SMASH));
    }
}
