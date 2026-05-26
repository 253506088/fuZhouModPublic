package basicmod.cards.masks;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.DominionPower;
import basicmod.powers.masks.TaLaPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TaLaMask extends basicmod.cards.BaseCard {
    public static final String ID = BasicMod.makeID(TaLaMask.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.POWER,
            AbstractCard.CardRarity.RARE,
            AbstractCard.CardTarget.SELF,
            3
    );

    public TaLaMask() {
        super(ID, info);
        setMagic(2, 1); // slot +2, upgrade to +3
        tags.add(CustomTags.MASK);
    }

    protected int getPermanentPeelCostReductionCount() {
        return Math.max(0, this.misc);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        BasicMod.logger.info("【塔拉面具】打出：面具容量+{}，并获得1层影噬。", magicNumber);
        addToBot(new ApplyPowerAction(p, p, new TaLaPower(p, magicNumber), magicNumber));
        addToBot(new ApplyPowerAction(p, p, new DominionPower(p, 1), 1));
        final boolean castUpgraded = this.upgraded;
        final int permanentCostReductionCount = getPermanentPeelCostReductionCount();
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (p != null && p.getPower(TaLaPower.POWER_ID) instanceof TaLaPower) {
                    BasicMod.logger.info("【塔拉面具】记录施放次数：是否升级={}，永久剥离降费次数={}", castUpgraded, permanentCostReductionCount);
                    ((TaLaPower) p.getPower(TaLaPower.POWER_ID)).recordTalaMaskCast(castUpgraded, permanentCostReductionCount);
                }
                this.isDone = true;
            }
        });
        TaLaPower.queueRandomShadowKhanCard();
    }
}
