package basicmod.cards;

import basicmod.actions.PossessionStealAction;
import basicmod.enums.CharacterEnums;
import basicmod.powers.PossessionNoAttackPower;
import basicmod.util.CardStats;
import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

/**
 * 夺舍。
 * 未打出攻击牌时才能使用，眩晕目标并偷取部分正面资源。
 */
public class CardPossession extends BaseCard {
    public static final String ID = makeID("Possession");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            2
    );

    /**
     * 构造函数。
     */
    public CardPossession() {
        super(ID, info);
        setMagic(3, 1);
        setExhaust(true);
    }

    /**
     * 检查本回合是否已经打出过攻击牌。
     */
    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        if (!super.canUse(p, m)) {
            return false;
        }
        if (hasPlayedAttackThisTurn()) {
            if (this.cardStrings != null && this.cardStrings.EXTENDED_DESCRIPTION != null && this.cardStrings.EXTENDED_DESCRIPTION.length > 0) {
                this.cantUseMessage = this.cardStrings.EXTENDED_DESCRIPTION[0];
            }
            return false;
        }
        return true;
    }

    /**
     * 打出后禁止本回合继续打攻击牌，并对目标执行夺舍。
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m == null) {
            return;
        }
        addToBot(new ApplyPowerAction(p, p, new PossessionNoAttackPower(p, 1), 1));
        addToBot(new PossessionStealAction(p, m));
        addToBot(new StunMonsterAction(m, p, 1));
        addToBot(new ApplyPowerAction(m, p, new VulnerablePower(m, this.magicNumber, false), this.magicNumber));
        addToBot(new ApplyPowerAction(m, p, new WeakPower(m, this.magicNumber, false), this.magicNumber));
    }

    private boolean hasPlayedAttackThisTurn() {
        if (AbstractDungeon.actionManager == null) {
            return false;
        }
        for (AbstractCard card : AbstractDungeon.actionManager.cardsPlayedThisTurn) {
            if (card != null && card.type == CardType.ATTACK) {
                return true;
            }
        }
        return false;
    }
}
