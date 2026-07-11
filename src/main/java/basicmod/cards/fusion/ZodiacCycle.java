package basicmod.cards.fusion;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.helpers.FusionHelper;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 生肖轮转
 * 0费 普通 技能 · 消耗
 * 获得 B 点格挡。每持有 1 个动物符咒抽 1 张牌（最多 3 张）。
 */
public class ZodiacCycle extends BaseCard {
    public static final String ID = makeID("ZodiacCycle");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.SELF,
            0);

    public ZodiacCycle() {
        super(ID, info);
        tags.add(CustomTags.FUSION);
        setBlock(5, 8);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, this.block));
        int draw = Math.min(FusionHelper.countTalismanRelics(), 3);
        if (draw > 0) {
            addToBot(new DrawCardAction(p, draw));
        }
    }
}
