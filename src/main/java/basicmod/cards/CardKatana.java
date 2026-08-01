package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.KatanaPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 武士刀。
 * 让尼嘉-忍者团攻击所有敌人。
 */
public class CardKatana extends BaseCard {
    public static final String ID = makeID("Katana");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.POWER,
            CardRarity.RARE,
            CardTarget.SELF,
            1
    );

    /**
     * 构造函数。
     */
    public CardKatana() {
        super(ID, info);
        tags.add(CustomTags.MASK_SUPPORT);
        setInnate(false, true);
    }

    /**
     * 打出后获得武士刀能力。
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new KatanaPower(p, 1), 1));
    }
}
