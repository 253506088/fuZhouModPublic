package basicmod.cards.skills;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.DeflectAndStrikePower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DeflectAndStrike extends BaseCard {
    public static final String ID = makeID(DeflectAndStrike.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.SELF,
            1
    );

    public DeflectAndStrike() {
        super(ID, info);
        // 基础版不提供格挡（0），升级后提供 5 点。
        setBlock(0, 5);
        tags.add(CustomTags.TEAM_JACKIE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 如果格挡值大于 0（升级后），执行叠甲动作
        if (this.block > 0) {
            addToBot(new GainBlockAction(p, p, this.block));
        }
        
        // 施加“借力打力”能力，持续到回合结束
        addToBot(new ApplyPowerAction(p, p, new DeflectAndStrikePower(p, 1)));
    }
}
