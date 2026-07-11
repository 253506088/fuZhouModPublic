package basicmod.cards.shadow;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 阵型·铁壁 - 1费技能
 * 获得5格挡。
 */
public class FormationIronWall extends BaseCard {
    public static final String ID = makeID(FormationIronWall.class.getSimpleName());
    private static final CardStats stats = new CardStats(
            CharacterEnums.SHENGZHU_COLOR, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF, 1);

    public FormationIronWall() { super(ID, stats); setBlock(5, 2); }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, this.block));
    }

    @Override
    public void upgrade() {
        if (!upgraded) { upgradeName(); upgradeBlock(2); initializeDescription(); }
    }
}
