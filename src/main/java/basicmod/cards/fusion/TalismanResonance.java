package basicmod.cards.fusion;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.helpers.FusionHelper;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

/**
 * 符咒共振
 * 2费 稀有 技能 · 消耗
 * 获得 M 点力量。每持有 1 个动物符咒，额外获得 1 点力量。
 */
public class TalismanResonance extends BaseCard {
    public static final String ID = makeID("TalismanResonance");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.SELF,
            2);

    public TalismanResonance() {
        super(ID, info);
        tags.add(CustomTags.FUSION);
        setMagic(2, 3);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int total = this.magicNumber + FusionHelper.countTalismanRelics();
        addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, total), total));
    }
}
