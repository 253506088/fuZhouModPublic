package basicmod.cards.fusion;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.relics.RabbitTalisman;
import basicmod.relics.TigerTalisman;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 虎兔连势
 * 1费 普通 技能
 * 抽 M 张牌。若持有虎符咒或兔符咒，获得 1 点能量并额外抽 1 张牌。
 */
public class TigerRabbitFusion extends BaseCard {
    public static final String ID = makeID("TigerRabbitFusion");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.NONE,
            1);

    public TigerRabbitFusion() {
        super(ID, info);
        tags.add(CustomTags.FUSION);
        setMagic(1, 2);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DrawCardAction(p, this.magicNumber));
        if (AbstractDungeon.player.hasRelic(TigerTalisman.ID)
                || AbstractDungeon.player.hasRelic(RabbitTalisman.ID)) {
            addToBot(new GainEnergyAction(1));
            addToBot(new DrawCardAction(p, 1));
        }
    }
}
