package basicmod.cards;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.helpers.MaskManager;
import basicmod.powers.DemonCodexPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 恶魔法典。
 * 让黑影兵团重新吃常规属性修正，并提高本场战斗面具容量。
 */
public class CardDemonCodex extends BaseCard {
    public static final String ID = makeID("DemonCodex");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.POWER,
            CardRarity.RARE,
            CardTarget.SELF,
            2
    );

    /**
     * 构造函数。
     */
    public CardDemonCodex() {
        super(ID, info);
        tags.add(CustomTags.MASK_SUPPORT);
        setCostUpgrade(1);
        setMagic(2);
    }

    /**
     * 打出后获得恶魔法典能力，并允许重复增加面具容量。
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new DemonCodexPower(p, 1), 1));
        MaskManager.addExtraMaskCapacity(this.magicNumber);
        BasicMod.logger.info("【恶魔法典】本场战斗面具容量增加={}，当前额外容量={}", this.magicNumber, MaskManager.extraMaskCapacityThisCombat);
    }
}
