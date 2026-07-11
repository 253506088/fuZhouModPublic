package basicmod.cards.fusion;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.relics.MonkeyTalisman;
import basicmod.relics.RoosterTalisman;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 猴鸡合形
 * 1费 普通 技能
 * 获得 B 点格挡。若持有猴符咒或鸡符咒，抽 2 张牌。
 */
public class MonkeyRoosterFusion extends BaseCard {
    public static final String ID = makeID("MonkeyRoosterFusion");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.SELF,
            1);

    public MonkeyRoosterFusion() {
        super(ID, info);
        tags.add(CustomTags.FUSION);
        setBlock(4, 6);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, this.block));
        if (AbstractDungeon.player.hasRelic(MonkeyTalisman.ID)
                || AbstractDungeon.player.hasRelic(RoosterTalisman.ID)) {
            addToBot(new DrawCardAction(p, 2));
        }
    }
}
