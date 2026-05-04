package basicmod.cards.shadowkhan;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;

import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;


public class LeiSuAlien extends BaseShadowKhanCard {
    public static final String ID = BasicMod.makeID(LeiSuAlien.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardRarity.SPECIAL,
            AbstractCard.CardTarget.ENEMY,
            0
    );

    public LeiSuAlien() {
        super(ID, info);
        setMagic(6, 4); // 6 curse, +4 on upgrade -> 10
        tags.add(CustomTags.SHADOW_KHAN);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int count = basicmod.helpers.MaskManager.shadowKhanCardsPlayedThisTurn;
        int bonusCurse = Math.max(0, count - 1);
        int totalCurse = this.magicNumber + bonusCurse;
        
        basicmod.BasicMod.logger.info("【雷苏-异形团】打出日志: 卡片基础诅咒" + this.magicNumber 
            + " + 本回合额外计数加成" + bonusCurse 
            + " = 合计" + totalCurse + "层诅咒");

        addToBot(new ApplyPowerAction(m, p, new basicmod.powers.LeiSuCursePower(m, p, totalCurse), totalCurse));
    }
}
