package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.helpers.BlackHandCardHelper;
import basicmod.powers.BlackHandPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardTohru extends BaseCard {
    public static final String ID = makeID("Tohru");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.RARE,
            CardTarget.ENEMY,
            2);

    public CardTohru() {
        super(ID, info);
        setMagic(8, 0); // 升级前后基础始终是8
        this.baseDamage = 0;
        this.baseBlock = 0;
        tags.add(CustomTags.blackhand);
    }

    private int getBlackHandCount() {
        return BlackHandCardHelper.countBlackHandInMasterDeck();
    }

    private int calculateDynamicValue() {
        if (AbstractDungeon.player == null) return 0;
        int multiplier = this.upgraded ? 2 : 1;
        int percentage = this.baseMagicNumber + getBlackHandCount() * multiplier;
        return (AbstractDungeon.player.maxHealth * percentage) / 100;
    }

    @Override
    public void applyPowers() {
        int multiplier = this.upgraded ? 2 : 1;
        this.magicNumber = this.baseMagicNumber + getBlackHandCount() * multiplier;
        this.isMagicNumberModified = this.magicNumber != this.baseMagicNumber;
        this.baseDamage = calculateDynamicValue();
        this.baseBlock = calculateDynamicValue();
        super.applyPowers();
        this.rawDescription = this.upgraded && cardStrings.UPGRADE_DESCRIPTION != null ? cardStrings.UPGRADE_DESCRIPTION : cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        int multiplier = this.upgraded ? 2 : 1;
        this.magicNumber = this.baseMagicNumber + getBlackHandCount() * multiplier;
        this.isMagicNumberModified = this.magicNumber != this.baseMagicNumber;
        this.baseDamage = calculateDynamicValue();
        super.calculateCardDamage(m);
        this.rawDescription = this.upgraded && cardStrings.UPGRADE_DESCRIPTION != null ? cardStrings.UPGRADE_DESCRIPTION : cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void onMoveToDiscard() {
        this.rawDescription = this.upgraded && cardStrings.UPGRADE_DESCRIPTION != null ? cardStrings.UPGRADE_DESCRIPTION : cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, this.block));
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        BlackHandPower.apply(m, p, 2);
    }
}
