package basicmod.cards.talismans;

import basicmod.cards.BaseCard;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import basicmod.enums.CharacterEnums;

public class TigerTalismanSkillCard extends BaseCard {
    public static final String ID = makeID("TigerTalismanSkillCard");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            1
    );

    public TigerTalismanSkillCard() {
        super(ID, info);
        tags.add(CustomTags.TALISMAN_CARD);
        setMagic(3, 1);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null) {
            if (m.currentHealth > p.currentHealth) {
                // 生命值更高的敌人直接永久失去力量，不再在回合结束时恢复。
                addToBot(new ApplyPowerAction(m, p, new com.megacrit.cardcrawl.powers.StrengthPower(m, -magicNumber), -magicNumber));
            } else if (m.currentHealth < p.currentHealth) {
                // 玩家生命值更高时，直接永久获得力量，不再在回合结束时回退。
                addToBot(new ApplyPowerAction(p, p, new com.megacrit.cardcrawl.powers.StrengthPower(p, magicNumber), magicNumber));
            }
        }
    }
}
