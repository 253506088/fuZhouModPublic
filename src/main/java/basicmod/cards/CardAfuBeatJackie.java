package basicmod.cards;

import basicmod.powers.ValmontPower;
import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.HashSet;
import java.util.Set;

public class CardAfuBeatJackie extends BaseCard {
    public static final String ID = makeID("AfuBeatJackie");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.RARE,
            CardTarget.ENEMY,
            4);

    private static final int DAMAGE = 10;
    private static final int UPG_DAMAGE = 2;
    private static final int BONUS_PER_CARD = 2;
    private static final int UPG_BONUS_PER_CARD = 1;

    public CardAfuBeatJackie() {
        super(ID, info);
        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(BONUS_PER_CARD, UPG_BONUS_PER_CARD);
        tags.add(CustomTags.afu);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
    }

    @Override
    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        int count = getUniqueAfuMoves();
        this.baseDamage += count * this.magicNumber;
        super.applyPowers();
        updateTurnCostReductionFromAfu();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        int realBaseDamage = this.baseDamage;
        int count = getUniqueAfuMoves();
        this.baseDamage += count * this.magicNumber;
        super.calculateCardDamage(m);
        updateTurnCostReductionFromAfu();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void triggerWhenDrawn() {
        super.triggerWhenDrawn();
        updateTurnCostReductionFromAfu();
    }

    @Override
    public void triggerOnOtherCardPlayed(AbstractCard c) {
        super.triggerOnOtherCardPlayed(c);
        if (c != null && c.hasTag(CustomTags.afu)) {
            applyPowers();
        }
    }

    private int getUniqueAfuMoves() {
        Set<String> uniqueIDs = new HashSet<>();
        for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisCombat) {
            if (c.tags.contains(CustomTags.afu)) {
                uniqueIDs.add(c.cardID);
            }
        }
        return uniqueIDs.size();
    }

    private void updateTurnCostReductionFromAfu() {
        if (AbstractDungeon.actionManager == null) return;
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasPower(ValmontPower.POWER_ID)) return;

        int afuPlayedThisTurn = 0;
        for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisTurn) {
            if (c != null && c.hasTag(CustomTags.afu)) {
                afuPlayedThisTurn++;
            }
        }

        int targetCost = Math.max(0, this.cost - afuPlayedThisTurn);
        if (this.costForTurn >= 0 && targetCost > this.costForTurn) {
            return;
        }
        this.setCostForTurn(targetCost);
        this.isCostModified = (this.costForTurn != this.cost);
    }
}
