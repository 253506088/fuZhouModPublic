package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.TornadoPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 曾经叫【龙卷风摧毁停车场】，后来还是觉得还是叫【山】
 */
public class CardTornado extends BaseCard {
    public static final String ID = makeID("Tornado");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.POWER,
            CardRarity.SPECIAL,
            CardTarget.SELF,
            2);

    public CardTornado() {
        super(ID, info);
        setCostUpgrade(1); // 升级后费用-1
        tags.add(CustomTags.afu);
        tags.add(CustomTags.DAOLONG_DARK_ASSASSIN);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new TornadoPower(p, 1), 1));
    }
}
