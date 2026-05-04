package basicmod.cards.demons;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.EarthDemonPower;
import basicmod.powers.EarthPrisonPrepPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.BarricadePower;

public class CardEarthDemonQi extends BaseCard {
    public static final String ID = makeID("CardEarthDemonQi");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.POWER,
            CardRarity.SPECIAL,
            CardTarget.SELF,
            2
    );

    public CardEarthDemonQi() {
        super(ID, info);
        setMagic(3, -1); // 基础失去3血，升级版-1 也就是失去2血
        tags.add(CustomTags.EIGHT_DEMONS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 获得格挡不消失效果 (壁垒)
        addToBot(new ApplyPowerAction(p, p, new BarricadePower(p)));
        // 挂上地之恶魔Debuff，回合结束无格挡则掉血，掉血换格挡 (1/8 最大生命值)
        addToBot(new ApplyPowerAction(p, p, new EarthDemonPower(p, p, this.magicNumber), this.magicNumber));
        // 挂上大地守望，3回合如果不破甲，对全体施加地缚
        addToBot(new ApplyPowerAction(p, p, new EarthPrisonPrepPower(p, 3), 3));
    }
}
