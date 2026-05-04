package basicmod.patches;

import basicmod.helpers.HistoryBookRewriteManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;

/**
 * 岁月史书商店补丁。
 * 负责零元购、卖后补货、删牌服务重新开放等商店内临时规则。
 */
public class HistoryBookShopPatch {
    @SpirePatch(clz = ShopScreen.class, method = "open")
    /**
     * 商店打开时的价格修正补丁。
     */
    public static class OpenPatch {
        /**
         * 商店打开时应用岁月史书的零元购价格。
         */
        @SpirePostfixPatch
        public static void Postfix(ShopScreen __instance) {
            HistoryBookRewriteManager.applyFreeShopPrices(__instance);
        }
    }

    @SpirePatch(clz = ShopScreen.class, method = "update")
    /**
     * 商店每帧更新时的规则维持补丁。
     */
    public static class UpdatePatch {
        /**
         * 商店更新时持续归零，确保补货后的商品也保持零元购。
         */
        @SpirePostfixPatch
        public static void Postfix(ShopScreen __instance) {
            HistoryBookRewriteManager.applyFreeShopPrices(__instance);
            HistoryBookRewriteManager.maintainShopRestock(__instance);
        }
    }

    @SpirePatch(clz = ShopScreen.class, method = "purchaseCard", paramtypez = {AbstractCard.class})
    /**
     * 购买卡牌后的补货补丁。
     */
    public static class PurchaseCardPatch {
        /**
         * 买走卡牌后补一张新卡。
         */
        @SpirePostfixPatch
        public static void Postfix(ShopScreen __instance, AbstractCard hoveredCard) {
            HistoryBookRewriteManager.restockPurchasedCard(__instance, hoveredCard);
        }
    }

    @SpirePatch(clz = StoreRelic.class, method = "purchaseRelic")
    /**
     * 购买遗物后的补货补丁。
     */
    public static class PurchaseRelicPatch {
        /**
         * 买走遗物后在原槽位补一个新遗物。
         */
        @SpirePostfixPatch
        public static void Postfix(StoreRelic __instance) {
            HistoryBookRewriteManager.restockPurchasedRelic(__instance);
        }
    }

    @SpirePatch(clz = StorePotion.class, method = "purchasePotion")
    /**
     * 购买药水后的补货补丁。
     */
    public static class PurchasePotionPatch {
        /**
         * 买走药水后在原槽位补一瓶新药水。
         */
        @SpirePostfixPatch
        public static void Postfix(StorePotion __instance) {
            HistoryBookRewriteManager.restockPurchasedPotion(__instance);
        }
    }

    @SpirePatch(clz = ShopScreen.class, method = "purgeCard")
    /**
     * 删除卡牌后的服务刷新补丁。
     */
    public static class PurgeCardPatch {
        /**
         * 删牌完成后重新开放删牌服务。
         */
        @SpirePostfixPatch
        public static void Postfix() {
            HistoryBookRewriteManager.refreshPurgeService();
        }
    }
}
