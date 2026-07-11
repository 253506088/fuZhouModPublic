package basicmod.cards.fusion;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.helpers.FusionHelper;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 双符共鸣
 * 1费 普通 技能
 * 抽 M 张牌。若持有 2 个及以上动物符咒，额外抽 1 张牌并恢复 1 点能量。
 */
public class DualTalismanResonance extends BaseCard {
    public static final String ID = makeID("DualTalismanResonance");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.NONE,
            1);

    public DualTalismanResonance() {
        super(ID, info);
        tags.add(CustomTags.FUSION);
        setMagic(2, 3);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DrawCardAction(p, this.magicNumber));
        if (FusionHelper.countTalismanRelics() >= 2) {
            addToBot(new GainEnergyAction(1));
            addToBot(new DrawCardAction(p, 1));
        }
    }
}
