package basicmod.relics;

import basemod.abstracts.CustomReward;
import basicmod.modifiers.demons.AbstractDemonQiModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;

public class PanKuRewardItem extends CustomReward {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:PanKuUI");
    public AbstractCard demonCard;
    public AbstractCard enchantDummyCard;
    public AbstractCard storageDummyCard;
    public AbstractDemonQiModifier qiModifier;

    private final String enchantBaseName;
    private final String enchantBaseDesc;

    public PanKuRewardItem(AbstractCard demonCard, AbstractCard enchantDummyCard, AbstractCard storageDummyCard, AbstractDemonQiModifier qiModifier) {
        super(ImageMaster.REWARD_CARD_NORMAL, uiStrings.TEXT[5], PanKuRewardItemPatch.PANKU_REWARD_TYPE);
        this.demonCard = demonCard;
        this.enchantDummyCard = enchantDummyCard;
        this.storageDummyCard = storageDummyCard;
        this.qiModifier = qiModifier;
        this.enchantBaseName = enchantDummyCard.name;
        this.enchantBaseDesc = enchantDummyCard.rawDescription;
    }

    public boolean canEnchantNow() {
        return PanKuDemonQiHelper.hasEnchantTarget(qiModifier);
    }

    public void openChoiceScreen() {
        boolean canEnchant = canEnchantNow();
        enchantDummyCard.name = enchantBaseName;
        enchantDummyCard.rawDescription = canEnchant ? enchantBaseDesc : enchantBaseDesc + uiStrings.TEXT[6];
        enchantDummyCard.initializeDescription();

        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        group.addToBottom(demonCard);
        group.addToBottom(enchantDummyCard);
        group.addToBottom(storageDummyCard);
        AbstractDungeon.dynamicBanner.hide();
        AbstractDungeon.gridSelectScreen.open(group, 1, uiStrings.TEXT[7], false, false, true, false);
    }

    @Override
    public boolean claimReward() {
        openChoiceScreen();
        AbstractDungeon.effectList.add(new PanKuRewardEffect(this));
        return false;
    }
}
