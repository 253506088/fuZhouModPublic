package basicmod.cards.skills;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TigerTalismanYin extends BaseCard {
    public static final String ID = makeID(TigerTalismanYin.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.SELF,
            1
    );

    public TigerTalismanYin() {
        super(ID, info);
        this.baseBlock = 0;
        this.baseMagicNumber = this.magicNumber = 2;
        this.tags.add(CustomTags.TEAM_JACKIE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, this.block));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
            this.initializeDescription();
        }
    }

    @Override
    public void applyPowers() {
        int rawBlock = Math.max(0, AbstractDungeon.player.hand.size() - 1) * this.magicNumber;
        // 设置格挡上限：基础版 16，升级版 20
        this.baseBlock = Math.min(this.upgraded ? 20 : 16, rawBlock);
        super.applyPowers();
        updateDescriptionText();
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        int rawBlock = Math.max(0, AbstractDungeon.player.hand.size() - 1) * this.magicNumber;
        this.baseBlock = Math.min(this.upgraded ? 20 : 16, rawBlock);
        super.calculateCardDamage(m);
        updateDescriptionText();
    }

    @Override
    public void onMoveToDiscard() {
        updateDescriptionText();
    }

    private void updateDescriptionText() {
        if (this.upgraded) {
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
        } else {
            this.rawDescription = cardStrings.DESCRIPTION;
        }
        initializeDescription();
    }
}
