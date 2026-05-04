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

public class TigerTalismanYang extends BaseCard {
    public static final String ID = makeID(TigerTalismanYang.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.SPECIAL,
            CardTarget.ENEMY,
            0
    );

    public TigerTalismanYang() {
        super(ID, info);
        this.baseDamage = 12;
        this.tags.add(CustomTags.TEAM_JACKIE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(4);
            this.initializeDescription();
        }
    }

    @Override
    public void applyPowers() {
        int otherCardsSize = Math.max(0, AbstractDungeon.player.hand.size() - 1);
        int reduction = otherCardsSize * 2;
        // 设置伤害下限：至少造成 2 点伤害
        this.baseDamage = Math.max(2, (this.upgraded ? 16 : 12) - reduction);
        super.applyPowers();
        this.rawDescription = cardStrings.DESCRIPTION;
        initializeDescription();
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        int otherCardsSize = Math.max(0, AbstractDungeon.player.hand.size() - 1);
        int reduction = otherCardsSize * 2;
        this.baseDamage = Math.max(2, (this.upgraded ? 16 : 12) - reduction);
        super.calculateCardDamage(m);
        this.rawDescription = cardStrings.DESCRIPTION;
        initializeDescription();
    }

    @Override
    public void onMoveToDiscard() {
        this.rawDescription = cardStrings.DESCRIPTION;
        initializeDescription();
    }
}
