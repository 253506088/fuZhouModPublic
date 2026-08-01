package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.EmergencyEscapePower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;

/**
 * 紧急逃生。
 * 龙小组合作牌，获得覆甲并在数回合内反弹怪物减益。
 */
public class CardEmergencyEscape extends BaseCard {
    public static final String ID = makeID("EmergencyEscape");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.POWER,
            CardRarity.SPECIAL,
            CardTarget.SELF,
            2
    );

    /**
     * 构造函数。
     */
    public CardEmergencyEscape() {
        super(ID, info);
        tags.add(CustomTags.TEAM_JACKIE);
        setBlock(3, 1);
        setMagic(2, 1);
    }

    /**
     * 打出后获得覆甲与紧急逃生能力。
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new PlatedArmorPower(p, this.block), this.block));
        addToBot(new ApplyPowerAction(p, p, new EmergencyEscapePower(p, this.magicNumber), this.magicNumber));
    }
}
