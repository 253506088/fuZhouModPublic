package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardAxeCutsTree extends BaseCard {
    public static final String ID = makeID("AxeCutsTree");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            2);

    private static final int DAMAGE = 18;
    private static final int UPG_DAMAGE = 6;

    public CardAxeCutsTree() {
        super(ID, info);
        setDamage(DAMAGE, UPG_DAMAGE);
        tags.add(CustomTags.afu);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HEAVY));
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        int realBaseDamage = this.baseDamage;
        if (m != null && m.currentBlock > 0) {
            this.baseDamage = (int)(realBaseDamage * 1.5);
        }
        super.calculateCardDamage(m);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }
}
