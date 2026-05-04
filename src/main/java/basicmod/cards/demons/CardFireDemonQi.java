package basicmod.cards.demons;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.BurningPower;
import basicmod.powers.SoakedPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardFireDemonQi extends BaseCard {
    public static final String ID = makeID("CardFireDemonQi");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.ALL_ENEMY,
            1
    );

    public CardFireDemonQi() {
        super(ID, info);
        setMagic(2, 1); // 基础2层，升级+1变3层
        tags.add(CustomTags.EIGHT_DEMONS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 全体逻辑：直接施加灼烧。如果敌人有潮湿，底层的 Power 逻辑会自动执行合成。
        for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
            if (!mo.isDeadOrEscaped()) {
                addToBot(new ApplyPowerAction(mo, p, new BurningPower(mo, p, this.magicNumber), this.magicNumber));
            }
        }
    }
}
