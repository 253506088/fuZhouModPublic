package basicmod.modifiers.demons;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import basicmod.relics.PigTalisman;
import basicmod.relics.RatTalisman;

import java.util.ArrayList;

public abstract class AbstractDemonQiModifier extends AbstractCardModifier {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:DemonQiModifiers");
    
    public abstract String getPrefix();
    public abstract String getExtraDesc();

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        // 防止由于初始化或者其他导致前缀叠床架屋
        String left = uiStrings.TEXT[16];
        String right = uiStrings.TEXT[17];
        if (cardName.contains(left + getPrefix() + right)) {
            return cardName;
        }
        return left + getPrefix() + right + cardName;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + " NL " + getExtraDesc();
    }
    
    @Override
    public String identifier(AbstractCard card) {
        return "DemonQi:" + this.getClass().getSimpleName();
    }
    
    @Override
    public boolean shouldApply(AbstractCard card) {
        // 诅咒和状态卡不能被附魔
        if (card.type == AbstractCard.CardType.CURSE || card.type == AbstractCard.CardType.STATUS) {
            return false;
        }
        // 根据选项B的防沉迷设计，一张卡只能带一种魔气
        for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
            if (mod instanceof AbstractDemonQiModifier) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取真正受到当前卡牌攻击影响的所有实体列表。
     * （包含单体、原本的群伤，以及由猪符咒+电眼逼人衍生出来的溅射附加区）
     */
    protected ArrayList<AbstractMonster> getTargets(AbstractCard card, AbstractCreature primaryTarget) {
        ArrayList<AbstractMonster> targets = new ArrayList<>();
        
        if (card.target == AbstractCard.CardTarget.ALL_ENEMY || card.target == AbstractCard.CardTarget.ALL) {
            for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                if (!m.isDeadOrEscaped()) {
                    targets.add(m);
                }
            }
        } else if (primaryTarget instanceof AbstractMonster) {
            targets.add((AbstractMonster) primaryTarget);
        }
        return targets;
    }
}
