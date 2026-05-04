package basicmod.helpers;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;

import java.util.ArrayList;

/**
 * 岁月史书残卷的场景改写管理器。
 * 这里集中保存“本次营火多选”“本次商店零元购”“本次商店补货”等临时房间状态。
 */
public class HistoryBookRewriteManager {
    public static boolean campfireMultiSelect = false;
    public static boolean shopFree = false;
    public static boolean shopRestock = false;
    private static boolean shopCreatedFromRestRoom = false;
    private static boolean restRoomCreatedFromShop = false;
    private static AbstractRoom activeCampfireRoom = null;
    private static AbstractRoom activeShopRoom = null;
    private static AbstractRoom shopRoomCreatedFromRestRoomRoom = null;
    private static AbstractRoom restRoomCreatedFromShopRoom = null;
    private static int targetColoredCardCount = -1;
    private static int targetColorlessCardCount = -1;
    private static int targetRelicCount = -1;
    private static int targetPotionCount = -1;
    private static final ArrayList<Integer> targetRelicSlots = new ArrayList<>();
    private static final ArrayList<Integer> targetPotionSlots = new ArrayList<>();
    private static final ArrayList<Integer> pendingRelicSlots = new ArrayList<>();
    private static final ArrayList<Integer> pendingPotionSlots = new ArrayList<>();
    private static final ArrayList<String> usedCampfireOptionKeys = new ArrayList<>();

    /**
     * 开启本次营火多选模式。
     */
    public static void enableCampfireMultiSelect() {
        campfireMultiSelect = true;
        activeCampfireRoom = AbstractDungeon.getCurrRoom();
        clearUsedCampfireOptions();
        CardCrawlGame.sound.play("POWER_MANTRA");
    }

    /**
     * 开启本次商店零元购模式，并立刻把当前商店价格归零。
     */
    public static void enableShopFree() {
        shopFree = true;
        activeShopRoom = AbstractDungeon.getCurrRoom();
        applyFreeShopPrices(AbstractDungeon.shopScreen);
        CardCrawlGame.sound.play("GOLD_GAIN");
    }

    /**
     * 开启本次商店补货模式，并解除删牌服务的一次性限制。
     */
    public static void enableShopRestock() {
        shopRestock = true;
        activeShopRoom = AbstractDungeon.getCurrRoom();
        if (AbstractDungeon.shopScreen != null) {
            AbstractDungeon.shopScreen.purgeAvailable = true;
            rememberCurrentShopInventory(AbstractDungeon.shopScreen);
        }
        CardCrawlGame.sound.play("SHOP_PURCHASE");
    }

    /**
     * 将当前休息处临时改写成商店，并打开商店界面。
     */
    public static void transformRestRoomToShop(boolean freeShop) {
        if (AbstractDungeon.getCurrMapNode() == null) {
            return;
        }
        if (freeShop) {
            shopFree = true;
        }
        shopCreatedFromRestRoom = true;
        restRoomCreatedFromShop = false;
        ShopRoom shopRoom = new ShopRoom();
        AbstractDungeon.getCurrMapNode().setRoom(shopRoom);
        activeShopRoom = shopRoom;
        shopRoomCreatedFromRestRoomRoom = shopRoom;
        restRoomCreatedFromShopRoom = null;
        shopRoom.onPlayerEntry();
        applyFreeShopPrices(AbstractDungeon.shopScreen);
    }

    /**
     * 将当前商店临时改写成休息处，并打开营火界面。
     */
    public static void transformShopToRestRoom(boolean multiSelect) {
        if (AbstractDungeon.getCurrMapNode() == null) {
            return;
        }
        if (multiSelect) {
            campfireMultiSelect = true;
            clearUsedCampfireOptions();
        }
        restRoomCreatedFromShop = true;
        shopCreatedFromRestRoom = false;
        RestRoom restRoom = new RestRoom();
        AbstractDungeon.getCurrMapNode().setRoom(restRoom);
        restRoomCreatedFromShopRoom = restRoom;
        shopRoomCreatedFromRestRoomRoom = null;
        if (multiSelect) {
            activeCampfireRoom = restRoom;
        }
        restRoom.onPlayerEntry();
    }

    /**
     * 进入非商店/非休息处时清理本次房间标记，避免影响后续房间。
     */
    public static void clearFlagsIfLeavingSpecialRoom(AbstractRoom room) {
        if (room == null
                || !(room instanceof ShopRoom)
                || ((shopFree || shopRestock || shopCreatedFromRestRoom) && room != activeShopRoom)) {
            shopFree = false;
            shopRestock = false;
            shopCreatedFromRestRoom = false;
            activeShopRoom = null;
            shopRoomCreatedFromRestRoomRoom = null;
            clearShopInventoryMemory();
        }
        if (room == null
                || !(room instanceof RestRoom)
                || ((campfireMultiSelect || restRoomCreatedFromShop) && room != activeCampfireRoom)) {
            campfireMultiSelect = false;
            restRoomCreatedFromShop = false;
            activeCampfireRoom = null;
            restRoomCreatedFromShopRoom = null;
            clearUsedCampfireOptions();
        }
    }

    /**
     * 记录本次休息处已经使用过的营火选项。
     */
    public static void rememberUsedCampfireOption(AbstractCampfireOption option) {
        if (!campfireMultiSelect || option == null) {
            return;
        }

        String key = getCampfireOptionKey(option);
        if (!usedCampfireOptionKeys.contains(key)) {
            usedCampfireOptionKeys.add(key);
        }
    }

    /**
     * 将本次休息处已经使用过的营火选项置灰，避免同一选项重复使用。
     */
    public static void disableUsedCampfireOptions(ArrayList<AbstractCampfireOption> options) {
        if (!campfireMultiSelect || options == null) {
            return;
        }

        for (AbstractCampfireOption option : options) {
            if (option != null && usedCampfireOptionKeys.contains(getCampfireOptionKey(option))) {
                option.usable = false;
            }
        }
    }

    /**
     * 清理本次休息处的已使用选项记录。
     */
    private static void clearUsedCampfireOptions() {
        usedCampfireOptionKeys.clear();
    }

    /**
     * 获取营火选项的稳定标识，同一类按钮在本次休息处只允许使用一次。
     */
    private static String getCampfireOptionKey(AbstractCampfireOption option) {
        return option.getClass().getName();
    }

    /**
     * 当前商店是否由岁月史书从休息处临时改写而来。
     */
    public static boolean isShopCreatedFromRestRoom() {
        return shopCreatedFromRestRoom && AbstractDungeon.getCurrRoom() == shopRoomCreatedFromRestRoomRoom;
    }

    /**
     * 当前休息处是否由岁月史书从商店临时改写而来。
     */
    public static boolean isRestRoomCreatedFromShop() {
        return restRoomCreatedFromShop && AbstractDungeon.getCurrRoom() == restRoomCreatedFromShopRoom;
    }

    /**
     * 把当前商店所有可见价格改为 0。
     */
    public static void applyFreeShopPrices(ShopScreen shopScreen) {
        if (!shopFree || shopScreen == null) {
            return;
        }

        if (shopScreen.coloredCards != null) {
            for (AbstractCard card : shopScreen.coloredCards) {
                card.price = 0;
            }
        }
        if (shopScreen.colorlessCards != null) {
            for (AbstractCard card : shopScreen.colorlessCards) {
                card.price = 0;
            }
        }

        ArrayList<StoreRelic> relics = ReflectionHacks.getPrivate(shopScreen, ShopScreen.class, "relics");
        if (relics != null) {
            for (StoreRelic relic : relics) {
                relic.price = 0;
            }
        }

        ArrayList<StorePotion> potions = ReflectionHacks.getPrivate(shopScreen, ShopScreen.class, "potions");
        if (potions != null) {
            for (StorePotion potion : potions) {
                potion.price = 0;
            }
        }

        ShopScreen.actualPurgeCost = 0;
    }

    /**
     * 每帧维护商店补货状态，处理购买方法补丁没抓到的售出场景。
     */
    public static void maintainShopRestock(ShopScreen shopScreen) {
        if (!shopRestock || shopScreen == null) {
            return;
        }

        rememberCurrentShopInventory(shopScreen);
        restockMissingCards(shopScreen);
        restockSoldRelics(shopScreen);
        restockSoldPotions(shopScreen);
        applyFreeShopPrices(shopScreen);
        refreshPurgeService();
    }

    /**
     * 购买卡牌后，在原位置补一张同池卡牌。
     */
    public static void restockPurchasedCard(ShopScreen shopScreen, AbstractCard purchasedCard) {
        if (!shopRestock || shopScreen == null || purchasedCard == null) {
            return;
        }

        if (replaceCardInList(shopScreen.coloredCards, purchasedCard, false)
                || replaceCardInList(shopScreen.colorlessCards, purchasedCard, true)) {
            applyFreeShopPrices(shopScreen);
            ReflectionHacks.privateMethod(ShopScreen.class, "setStartingCardPositions").invoke(shopScreen);
            return;
        }

        if (purchasedCard.color == AbstractCard.CardColor.COLORLESS) {
            appendRestockCard(shopScreen.colorlessCards, true);
        } else {
            appendRestockCard(shopScreen.coloredCards, false);
        }
        applyFreeShopPrices(shopScreen);
        ReflectionHacks.privateMethod(ShopScreen.class, "setStartingCardPositions").invoke(shopScreen);
    }

    /**
     * 购买遗物后，在同一个商店槽位补一个新遗物。
     */
    public static void restockPurchasedRelic(StoreRelic storeRelic) {
        if (!shopRestock || storeRelic == null || storeRelic.relic == null) {
            return;
        }

        ShopScreen shopScreen = ReflectionHacks.getPrivate(storeRelic, StoreRelic.class, "shopScreen");
        ArrayList<StoreRelic> relics = ReflectionHacks.getPrivate(shopScreen, ShopScreen.class, "relics");
        if (relics != null && storeRelic.isPurchased) {
            int index = relics.indexOf(storeRelic);
            rememberPendingRelicSlot(getRememberedRelicSlot(index, storeRelic));
            applyFreeShopPrices(shopScreen);
        }
    }

    /**
     * 购买药水后，在同一个商店槽位补一瓶新药水。
     */
    public static void restockPurchasedPotion(StorePotion storePotion) {
        if (!shopRestock || storePotion == null || storePotion.potion == null) {
            return;
        }

        ShopScreen shopScreen = ReflectionHacks.getPrivate(storePotion, StorePotion.class, "shopScreen");
        ArrayList<StorePotion> potions = ReflectionHacks.getPrivate(shopScreen, ShopScreen.class, "potions");
        if (potions != null && storePotion.isPurchased) {
            int index = potions.indexOf(storePotion);
            rememberPendingPotionSlot(getRememberedPotionSlot(index, storePotion));
            applyFreeShopPrices(shopScreen);
        }
    }

    /**
     * 删牌后重新开放删牌服务。
     */
    public static void refreshPurgeService() {
        if (!shopRestock || AbstractDungeon.shopScreen == null) {
            return;
        }

        AbstractDungeon.shopScreen.purgeAvailable = true;
        if (shopFree) {
            ShopScreen.actualPurgeCost = 0;
        }
    }

    /**
     * 在指定列表中替换刚买走的卡牌。
     */
    private static boolean replaceCardInList(ArrayList<AbstractCard> cards, AbstractCard purchasedCard, boolean colorless) {
        if (cards == null) {
            return false;
        }

        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) == purchasedCard || cards.get(i).uuid.equals(purchasedCard.uuid)) {
                AbstractCard newCard = colorless ? AbstractDungeon.returnTrulyRandomColorlessCardInCombat() : AbstractDungeon.returnRandomCard();
                if (newCard != null) {
                    cards.set(i, prepareRestockCard(newCard, colorless));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 当本体已经把卡牌从列表移除时，向对应列表补一张卡。
     */
    private static void appendRestockCard(ArrayList<AbstractCard> cards, boolean colorless) {
        if (cards == null) {
            return;
        }

        AbstractCard newCard = colorless ? AbstractDungeon.returnTrulyRandomColorlessCardInCombat() : AbstractDungeon.returnRandomCard();
        if (newCard != null) {
            cards.add(prepareRestockCard(newCard, colorless));
        }
    }

    /**
     * 给补货卡牌设置合理价格，零元购模式下统一为0。
     */
    private static AbstractCard prepareRestockCard(AbstractCard source, boolean colorless) {
        AbstractCard copy = source.makeCopy();
        if (shopFree) {
            copy.price = 0;
        } else {
            copy.price = AbstractCard.getPrice(copy.rarity);
            if (colorless) {
                copy.price = Math.round(copy.price * 1.2F);
            }
        }
        return copy;
    }

    /**
     * 记录开启补货时商店应保持的商品槽位数量。
     */
    private static void rememberCurrentShopInventory(ShopScreen shopScreen) {
        if (shopScreen == null) {
            return;
        }

        if (targetColoredCardCount < 0 && shopScreen.coloredCards != null) {
            targetColoredCardCount = shopScreen.coloredCards.size();
        }
        if (targetColorlessCardCount < 0 && shopScreen.colorlessCards != null) {
            targetColorlessCardCount = shopScreen.colorlessCards.size();
        }

        ArrayList<StoreRelic> relics = ReflectionHacks.getPrivate(shopScreen, ShopScreen.class, "relics");
        if (targetRelicCount < 0 && relics != null) {
            targetRelicCount = relics.size();
            targetRelicSlots.clear();
            for (int i = 0; i < relics.size(); i++) {
                targetRelicSlots.add(readRelicSlot(relics.get(i), i));
            }
        }

        ArrayList<StorePotion> potions = ReflectionHacks.getPrivate(shopScreen, ShopScreen.class, "potions");
        if (targetPotionCount < 0 && potions != null) {
            targetPotionCount = potions.size();
            targetPotionSlots.clear();
            for (int i = 0; i < potions.size(); i++) {
                targetPotionSlots.add(readPotionSlot(potions.get(i), i));
            }
        }
    }

    /**
     * 清理商店库存数量记忆，避免下一间商店沿用上一间商店的槽位数量。
     */
    private static void clearShopInventoryMemory() {
        targetColoredCardCount = -1;
        targetColorlessCardCount = -1;
        targetRelicCount = -1;
        targetPotionCount = -1;
        targetRelicSlots.clear();
        targetPotionSlots.clear();
        pendingRelicSlots.clear();
        pendingPotionSlots.clear();
    }

    /**
     * 若卡牌购买后从商店列表消失，则补回对应数量的新卡。
     */
    private static void restockMissingCards(ShopScreen shopScreen) {
        boolean changed = false;
        if (shopScreen.coloredCards != null && targetColoredCardCount >= 0) {
            while (shopScreen.coloredCards.size() < targetColoredCardCount) {
                appendRestockCard(shopScreen.coloredCards, false);
                changed = true;
            }
        }
        if (shopScreen.colorlessCards != null && targetColorlessCardCount >= 0) {
            while (shopScreen.colorlessCards.size() < targetColorlessCardCount) {
                appendRestockCard(shopScreen.colorlessCards, true);
                changed = true;
            }
        }
        if (changed) {
            ReflectionHacks.privateMethod(ShopScreen.class, "setStartingCardPositions").invoke(shopScreen);
        }
    }

    /**
     * 若遗物槽位已经售出，则在原槽位生成一个新遗物。
     */
    private static void restockSoldRelics(ShopScreen shopScreen) {
        ArrayList<StoreRelic> relics = ReflectionHacks.getPrivate(shopScreen, ShopScreen.class, "relics");
        if (relics == null) {
            return;
        }

        for (int i = relics.size() - 1; i >= 0; i--) {
            StoreRelic storeRelic = relics.get(i);
            if (storeRelic != null && storeRelic.isPurchased) {
                rememberPendingRelicSlot(getRememberedRelicSlot(i, storeRelic));
                relics.remove(i);
            }
        }

        removeDuplicateRelicSlots(relics);
        refillPendingRelicSlots(shopScreen, relics);
        refillMissingRelicSlots(shopScreen, relics);
    }

    /**
     * 若药水槽位已经售出，则在原槽位生成一瓶新药水。
     */
    private static void restockSoldPotions(ShopScreen shopScreen) {
        ArrayList<StorePotion> potions = ReflectionHacks.getPrivate(shopScreen, ShopScreen.class, "potions");
        if (potions == null) {
            return;
        }

        for (int i = potions.size() - 1; i >= 0; i--) {
            StorePotion storePotion = potions.get(i);
            if (storePotion != null && storePotion.isPurchased) {
                rememberPendingPotionSlot(getRememberedPotionSlot(i, storePotion));
                potions.remove(i);
            }
        }

        removeDuplicatePotionSlots(potions);
        refillPendingPotionSlots(shopScreen, potions);
        refillMissingPotionSlots(shopScreen, potions);
    }

    /**
     * 记录刚售出的遗物槽位，等待本体移除旧商品后再补货。
     */
    private static void rememberPendingRelicSlot(int slot) {
        if (slot < 0) {
            return;
        }
        if (!pendingRelicSlots.contains(slot)) {
            pendingRelicSlots.add(slot);
        }
    }

    /**
     * 记录刚售出的药水槽位，等待本体移除旧商品后再补货。
     */
    private static void rememberPendingPotionSlot(int slot) {
        if (slot < 0) {
            return;
        }
        if (!pendingPotionSlots.contains(slot)) {
            pendingPotionSlots.add(slot);
        }
    }

    /**
     * 在指定遗物槽位放入新遗物。
     */
    private static void addRelicAtSlot(ShopScreen shopScreen, ArrayList<StoreRelic> relics, int slot) {
        AbstractRelic relic = AbstractDungeon.returnRandomRelic(AbstractDungeon.returnRandomRelicTier());
        if (relic == null) {
            return;
        }

        StoreRelic replacement = new StoreRelic(relic, slot, shopScreen);
        relics.add(getRelicInsertIndex(slot, relics), replacement);
    }

    /**
     * 在指定药水槽位放入新药水。
     */
    private static void addPotionAtSlot(ShopScreen shopScreen, ArrayList<StorePotion> potions, int slot) {
        StorePotion replacement = new StorePotion(AbstractDungeon.returnRandomPotion(), slot, shopScreen);
        potions.add(getPotionInsertIndex(slot, potions), replacement);
    }

    /**
     * 读取遗物商品的原始横向槽位，读取失败时用列表下标兜底。
     */
    private static int readRelicSlot(StoreRelic storeRelic, int fallback) {
        if (storeRelic == null) {
            return fallback;
        }

        Integer slot = ReflectionHacks.getPrivate(storeRelic, StoreRelic.class, "slot");
        return slot == null ? fallback : slot;
    }

    /**
     * 读取药水商品的原始横向槽位，读取失败时用列表下标兜底。
     */
    private static int readPotionSlot(StorePotion storePotion, int fallback) {
        if (storePotion == null) {
            return fallback;
        }

        Integer slot = ReflectionHacks.getPrivate(storePotion, StorePotion.class, "slot");
        return slot == null ? fallback : slot;
    }

    /**
     * 获取开启补货时记录下来的遗物槽位，保证反复补货仍然回到原位置。
     */
    private static int getRememberedRelicSlot(int index, StoreRelic fallbackRelic) {
        if (index >= 0 && index < targetRelicSlots.size()) {
            return targetRelicSlots.get(index);
        }
        return readRelicSlot(fallbackRelic, index);
    }

    /**
     * 获取开启补货时记录下来的药水槽位，保证反复补货仍然回到原位置。
     */
    private static int getRememberedPotionSlot(int index, StorePotion fallbackPotion) {
        if (index >= 0 && index < targetPotionSlots.size()) {
            return targetPotionSlots.get(index);
        }
        return readPotionSlot(fallbackPotion, index);
    }

    /**
     * 清理重复遗物槽位，避免同一个横向位置渲染多个遗物。
     */
    private static void removeDuplicateRelicSlots(ArrayList<StoreRelic> relics) {
        for (int i = relics.size() - 1; i >= 0; i--) {
            int slot = readRelicSlot(relics.get(i), i);
            if (hasRelicSlotBefore(relics, i, slot)) {
                relics.remove(i);
            }
        }
    }

    /**
     * 清理重复药水槽位，避免同一个横向位置渲染多瓶药水。
     */
    private static void removeDuplicatePotionSlots(ArrayList<StorePotion> potions) {
        for (int i = potions.size() - 1; i >= 0; i--) {
            int slot = readPotionSlot(potions.get(i), i);
            if (hasPotionSlotBefore(potions, i, slot)) {
                potions.remove(i);
            }
        }
    }

    /**
     * 优先补回本帧刚售出的遗物槽位。
     */
    private static void refillPendingRelicSlots(ShopScreen shopScreen, ArrayList<StoreRelic> relics) {
        for (Integer slot : pendingRelicSlots) {
            if (slot != null && !hasRelicInSlot(relics, slot)) {
                addRelicAtSlot(shopScreen, relics, slot);
            }
        }
        pendingRelicSlots.clear();
    }

    /**
     * 优先补回本帧刚售出的药水槽位。
     */
    private static void refillPendingPotionSlots(ShopScreen shopScreen, ArrayList<StorePotion> potions) {
        for (Integer slot : pendingPotionSlots) {
            if (slot != null && !hasPotionInSlot(potions, slot)) {
                addPotionAtSlot(shopScreen, potions, slot);
            }
        }
        pendingPotionSlots.clear();
    }

    /**
     * 按开启补货时记录的遗物槽位补齐缺失商品。
     */
    private static void refillMissingRelicSlots(ShopScreen shopScreen, ArrayList<StoreRelic> relics) {
        int count = getTargetRelicSlotCount();
        for (int i = 0; i < count; i++) {
            int slot = getTargetRelicSlot(i);
            if (!hasRelicInSlot(relics, slot)) {
                addRelicAtSlot(shopScreen, relics, slot);
            }
        }
    }

    /**
     * 按开启补货时记录的药水槽位补齐缺失商品。
     */
    private static void refillMissingPotionSlots(ShopScreen shopScreen, ArrayList<StorePotion> potions) {
        int count = getTargetPotionSlotCount();
        for (int i = 0; i < count; i++) {
            int slot = getTargetPotionSlot(i);
            if (!hasPotionInSlot(potions, slot)) {
                addPotionAtSlot(shopScreen, potions, slot);
            }
        }
    }

    /**
     * 判断指定遗物槽位是否已经有商品。
     */
    private static boolean hasRelicInSlot(ArrayList<StoreRelic> relics, int slot) {
        for (int i = 0; i < relics.size(); i++) {
            if (readRelicSlot(relics.get(i), i) == slot) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断指定药水槽位是否已经有商品。
     */
    private static boolean hasPotionInSlot(ArrayList<StorePotion> potions, int slot) {
        for (int i = 0; i < potions.size(); i++) {
            if (readPotionSlot(potions.get(i), i) == slot) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前位置之前是否已经存在同槽位遗物。
     */
    private static boolean hasRelicSlotBefore(ArrayList<StoreRelic> relics, int index, int slot) {
        for (int i = 0; i < index; i++) {
            if (readRelicSlot(relics.get(i), i) == slot) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前位置之前是否已经存在同槽位药水。
     */
    private static boolean hasPotionSlotBefore(ArrayList<StorePotion> potions, int index, int slot) {
        for (int i = 0; i < index; i++) {
            if (readPotionSlot(potions.get(i), i) == slot) {
                return true;
            }
        }
        return false;
    }

    /**
     * 按槽位大小计算新遗物应该插入列表的下标，方便手柄选择顺序保持正常。
     */
    private static int getRelicInsertIndex(int slot, ArrayList<StoreRelic> relics) {
        for (int i = 0; i < relics.size(); i++) {
            if (readRelicSlot(relics.get(i), i) > slot) {
                return i;
            }
        }
        return relics.size();
    }

    /**
     * 按槽位大小计算新药水应该插入列表的下标，方便手柄选择顺序保持正常。
     */
    private static int getPotionInsertIndex(int slot, ArrayList<StorePotion> potions) {
        for (int i = 0; i < potions.size(); i++) {
            if (readPotionSlot(potions.get(i), i) > slot) {
                return i;
            }
        }
        return potions.size();
    }

    /**
     * 获取需要维持的遗物槽位数量。
     */
    private static int getTargetRelicSlotCount() {
        return targetRelicSlots.isEmpty() ? Math.max(targetRelicCount, 0) : targetRelicSlots.size();
    }

    /**
     * 获取需要维持的药水槽位数量。
     */
    private static int getTargetPotionSlotCount() {
        return targetPotionSlots.isEmpty() ? Math.max(targetPotionCount, 0) : targetPotionSlots.size();
    }

    /**
     * 获取指定下标对应的目标遗物槽位。
     */
    private static int getTargetRelicSlot(int index) {
        if (index >= 0 && index < targetRelicSlots.size()) {
            return targetRelicSlots.get(index);
        }
        return index;
    }

    /**
     * 获取指定下标对应的目标药水槽位。
     */
    private static int getTargetPotionSlot(int index) {
        if (index >= 0 && index < targetPotionSlots.size()) {
            return targetPotionSlots.get(index);
        }
        return index;
    }
}
