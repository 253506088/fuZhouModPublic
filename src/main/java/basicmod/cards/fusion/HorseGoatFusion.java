package basicmod.cards.fusion;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.relics.HorseTalisman;
import basicmod.relics.SheepTalisman;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 马羊同魂
 * 1费 普通 技能
 * 获得 B 点格挡。若持有马符咒或羊符咒，回复 M 点生命。
 */
public class HorseGoatFusion extends BaseCard {
    public static final String ID = makeID("HorseGoatFusion");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.SELF,
            1);

    public HorseGoatFusion() {
        super(ID, info);
        tags.add(CustomTags.FUSION);
        setBlock(6, 8);
        setMagic(3, 5);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, this.block));
        if (AbstractDungeon.player.hasRelic(HorseTalisman.ID)
                || AbstractDungeon.player.hasRelic(SheepTalisman.ID)) {
            addToBot(new HealAction(p, p, this.magicNumber));
        }
    }
}
