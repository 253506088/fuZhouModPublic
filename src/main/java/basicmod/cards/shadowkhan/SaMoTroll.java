package basicmod.cards.shadowkhan;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;

import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class SaMoTroll extends BaseShadowKhanCard {
    public static final String ID = BasicMod.makeID(SaMoTroll.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardRarity.SPECIAL,
            AbstractCard.CardTarget.SELF,
            0
    );

    public SaMoTroll() {
        super(ID, info);
        setBlock(5, 0); // Base block 5
        setMagic(1, 1); // 1 bonus block per shadow khan, augments by +1 on upgrade -> 2
        tags.add(CustomTags.SHADOW_KHAN);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int count = basicmod.helpers.MaskManager.shadowKhanCardsPlayedThisTurn;
        // Nerf: bonus now scales once per 2 other Shadowkhan cards played this turn.
        int bonusSk = Math.max(0, (count - 1) / 2);
        int totalBlock = this.block + (this.magicNumber * bonusSk);
        
        basicmod.BasicMod.logger.info("【萨莫-巨魔团】打出日志: 卡片基础" + this.baseBlock 
            + " + 敏捷等状态修正" + (this.block - this.baseBlock) 
            + " + 本回合打出其他黑影兵团卡数量(" + bonusSk + "张) x 每张加成格挡(" + this.magicNumber + ")=" + (this.magicNumber * bonusSk) 
            + " = 合计" + totalBlock + "点格挡");

        addToBot(new GainBlockAction(p, p, totalBlock));
    }
}
