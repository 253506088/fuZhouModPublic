package basicmod.relics;

import basicmod.cards.demons.CardDemonQiFake;
import basicmod.cards.demons.CardEarthDemonQi;
import basicmod.cards.demons.CardFireDemonQi;
import basicmod.cards.demons.CardHeavenDemonQi;
import basicmod.cards.demons.CardMoonDemonQi;
import basicmod.cards.demons.CardMountainDemonQi;
import basicmod.cards.demons.CardThunderDemonQi;
import basicmod.cards.demons.CardWaterDemonQi;
import basicmod.cards.demons.CardWindDemonQi;
import basicmod.modifiers.demons.AbstractDemonQiModifier;
import basicmod.modifiers.demons.EarthDemonQiModifier;
import basicmod.modifiers.demons.FireDemonQiModifier;
import basicmod.modifiers.demons.HeavenDemonQiModifier;
import basicmod.modifiers.demons.MoonDemonQiModifier;
import basicmod.modifiers.demons.MountainDemonQiModifier;
import basicmod.modifiers.demons.ThunderDemonQiModifier;
import basicmod.modifiers.demons.WaterDemonQiModifier;
import basicmod.modifiers.demons.WindDemonQiModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;

public class PanKuDemonQiHelper {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("fuZhouMod:PanKuUI");
    private static final UIStrings qiDescriptions = CardCrawlGame.languagePack.getUIString("fuZhouMod:PanKuQiDescriptions");

    private PanKuDemonQiHelper() {
    }

    public static AbstractCard getDemonCardCopyByID(String cardID) {
        if (cardID == null) {
            return null;
        }
        AbstractCard template = CardLibrary.getCard(cardID);
        return template == null ? null : template.makeCopy();
    }

    public static AbstractDemonQiModifier createModifierByCardID(String cardID) {
        AbstractCard demonCard = getDemonCardCopyByID(cardID);
        return demonCard == null ? null : createModifierByDemonCard(demonCard);
    }

    public static AbstractDemonQiModifier createModifierByDemonCard(AbstractCard demonCard) {
        if (demonCard instanceof CardFireDemonQi) {
            return new FireDemonQiModifier();
        } else if (demonCard instanceof CardWaterDemonQi) {
            return new WaterDemonQiModifier();
        } else if (demonCard instanceof CardWindDemonQi) {
            return new WindDemonQiModifier();
        } else if (demonCard instanceof CardThunderDemonQi) {
            return new ThunderDemonQiModifier();
        } else if (demonCard instanceof CardEarthDemonQi) {
            return new EarthDemonQiModifier();
        } else if (demonCard instanceof CardMountainDemonQi) {
            return new MountainDemonQiModifier();
        } else if (demonCard instanceof CardHeavenDemonQi) {
            return new HeavenDemonQiModifier();
        } else if (demonCard instanceof CardMoonDemonQi) {
            return new MoonDemonQiModifier();
        }
        return null;
    }

    public static String getQiDescriptionByDemonCard(AbstractCard demonCard) {
        if (demonCard instanceof CardFireDemonQi) {
            return qiDescriptions.TEXT[0];
        } else if (demonCard instanceof CardWaterDemonQi) {
            return qiDescriptions.TEXT[1];
        } else if (demonCard instanceof CardWindDemonQi) {
            return qiDescriptions.TEXT[2];
        } else if (demonCard instanceof CardThunderDemonQi) {
            return qiDescriptions.TEXT[3];
        } else if (demonCard instanceof CardEarthDemonQi) {
            return qiDescriptions.TEXT[4];
        } else if (demonCard instanceof CardMountainDemonQi) {
            return qiDescriptions.TEXT[5];
        } else if (demonCard instanceof CardHeavenDemonQi) {
            return qiDescriptions.TEXT[6];
        } else if (demonCard instanceof CardMoonDemonQi) {
            return qiDescriptions.TEXT[7];
        }
        return "";
    }

    public static CardGroup getEnchantTargets(AbstractDemonQiModifier modifier) {
        CardGroup targets = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        if (modifier == null || AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) {
            return targets;
        }
        for (AbstractCard deckCard : AbstractDungeon.player.masterDeck.group) {
            if (modifier.shouldApply(deckCard)) {
                targets.addToBottom(deckCard);
            }
        }
        return targets;
    }

    public static boolean hasEnchantTarget(AbstractDemonQiModifier modifier) {
        return !getEnchantTargets(modifier).isEmpty();
    }

    public static AbstractCard createRewardEnchantPreviewCard(AbstractCard demonCard) {
        String name = uiStrings.TEXT[8];
        String desc = uiStrings.TEXT[9] + demonCard.name + uiStrings.TEXT[10]
                + " NL " + getQiDescriptionByDemonCard(demonCard);
        return new CardDemonQiFake(demonCard, name, desc);
    }

    public static AbstractCard createRewardStorePreviewCard(AbstractCard demonCard) {
        String name = uiStrings.TEXT[11];
        String desc = uiStrings.TEXT[12] + demonCard.name + uiStrings.TEXT[13]
                + " NL " + getQiDescriptionByDemonCard(demonCard);
        return new CardDemonQiFake(demonCard, name, desc);
    }

    public static AbstractCard createCampfireStoredQiPreviewCard(AbstractCard demonCard, boolean canEnchantNow) {
        String name = demonCard.name + uiStrings.TEXT[14];
        String desc = getQiDescriptionByDemonCard(demonCard) + " NL " + uiStrings.TEXT[15];
        if (!canEnchantNow) {
            desc += uiStrings.TEXT[6];
        }
        return new CardDemonQiFake(demonCard, name, desc);
    }

    public static AbstractCard createCampfireCancelPreviewCard(AbstractCard sourceDemon) {
        return new CardDemonQiFake(
                uiStrings.TEXT[16],
                basicmod.BasicMod.imagePath("ui/back.png"),
                uiStrings.TEXT[17]);
    }
}
