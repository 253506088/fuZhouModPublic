package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.helpers.BlackHandCardHelper;
import basicmod.powers.BlackHandPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardBlackHandValmont extends BaseCard {
    public static final String ID = makeID("BlackHandValmont");
    private static final int BASE_COST = 3;
    private static final int BLACKHAND_CARDS_PER_DISCOUNT = 5;

    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.RARE,
            CardTarget.ENEMY,
            BASE_COST);

    public CardBlackHandValmont() {
        super(ID, info);
        setDamage(12, 3);
        setMagic(2, 1);
        tags.add(CustomTags.blackhand);
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        int count = BlackHandCardHelper.countBlackHandPlayedThisTurn(this);
        this.baseDamage += count * this.magicNumber;
        updateDynamicCostFromBlackHandDeckCount();
        super.calculateCardDamage(mo);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    @Override
    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        int count = BlackHandCardHelper.countBlackHandPlayedThisTurn(this);
        this.baseDamage += count * this.magicNumber;
        updateDynamicCostFromBlackHandDeckCount();
        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    @Override
    public void triggerWhenDrawn() {
        super.triggerWhenDrawn();
        updateDynamicCostFromBlackHandDeckCount();
    }

    private void updateDynamicCostFromBlackHandDeckCount() {
        int discount = BlackHandCardHelper.countBlackHandInMasterDeck() / BLACKHAND_CARDS_PER_DISCOUNT;
        int targetCost = Math.max(0, BASE_COST - discount);
        int turnDelta = this.costForTurn - this.cost;
        boolean hasTurnCostOverride = this.isCostModifiedForTurn;

        this.cost = targetCost;
        this.costForTurn = hasTurnCostOverride ? Math.max(0, targetCost + turnDelta) : targetCost;
        this.isCostModified = this.cost != BASE_COST;
        this.isCostModifiedForTurn = this.costForTurn != this.cost;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        BlackHandPower.apply(m, p, 2 + BlackHandCardHelper.countBlackHandPlayedThisTurn(this));
    }
}
