package basicmod.events;

import basicmod.helpers.TalismanHelper;
import basicmod.cards.EndlessDarkness;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static basicmod.BasicMod.makeID;

public class SafeArea13Event extends AbstractImageEvent {
    public static final String ID = makeID("SafeArea13Event");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final List<String> MASK_IDS = Arrays.asList(
            makeID("NiJiaMask"), makeID("LaZuoMask"), makeID("SaMoMask"),
            makeID("BaTeMask"), makeID("KaBoMask"), makeID("LeiSuMask"),
            makeID("ManNiMask"), makeID("MingTaMask"), makeID("YiKaMask"),
            makeID("TaLaMask")
    );

    private int screenNum = 0;
    private boolean hasMissingTalisman;
    private boolean hasMissingMask;

    public SafeArea13Event() {
        super(NAME, DESCRIPTIONS[0], "basicmod/images/events/safe_area_13.png");
        refreshAvailability();

        if (hasMissingTalisman) {
            this.imageEventText.setDialogOption(OPTIONS[0]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[1], true);
        }

        if (hasMissingMask) {
            this.imageEventText.setDialogOption(OPTIONS[2]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[3], true);
        }

        this.imageEventText.setDialogOption(OPTIONS[4]);
        this.imageEventText.setDialogOption(OPTIONS[6]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        if (!hasMissingTalisman) {
                            return;
                        }
                        giveMissingTalismanAndPotion();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        break;
                    case 1:
                        if (!hasMissingMask) {
                            return;
                        }
                        giveMissingMaskExpandAndPotion();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        break;
                    case 2:
                        AbstractDungeon.player.increaseMaxHp(8, true);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        break;
                    case 3:
                        giveTwoBaseGameRelics();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        break;
                    default:
                        return;
                }

                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[8]);
                this.screenNum = 1;
                break;
            case 1:
                this.openMap();
                break;
        }
    }

    private void refreshAvailability() {
        this.hasMissingTalisman = !getMissingTalismanIds().isEmpty();
        this.hasMissingMask = !getMissingMaskIds().isEmpty();
    }

    private ArrayList<String> getMissingTalismanIds() {
        ArrayList<String> missing = new ArrayList<>();
        if (AbstractDungeon.player == null) {
            return missing;
        }

        for (String id : TalismanHelper.ALL_TALISMAN_IDS) {
            if (!AbstractDungeon.player.hasRelic(id)) {
                missing.add(id);
            }
        }
        return missing;
    }

    private ArrayList<String> getMissingMaskIds() {
        ArrayList<String> missing = new ArrayList<>();
        if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) {
            return missing;
        }
        if (hasEndlessDarknessInDeck()) {
            return missing;
        }

        for (String id : MASK_IDS) {
            boolean found = false;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (id.equals(c.cardID)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                missing.add(id);
            }
        }
        return missing;
    }

    private void giveMissingTalismanAndPotion() {
        AbstractRelic talisman = TalismanHelper.getRandomMissingTalisman();
        if (talisman != null) {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, talisman);
        }
        grantOneBaseGamePotion();
    }

    private void giveMissingMaskExpandAndPotion() {
        if (hasEndlessDarknessInDeck()) {
            increasePotionSlots(2);
            grantOneBaseGamePotion();
            return;
        }

        ArrayList<String> missingMasks = getMissingMaskIds();
        if (!missingMasks.isEmpty()) {
            String cardId = missingMasks.get(AbstractDungeon.miscRng.random(missingMasks.size() - 1));
            AbstractCard template = CardLibrary.getCard(cardId);
            if (template != null) {
                AbstractCard card = template.makeCopy();
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
            }
        }

        increasePotionSlots(2);
        grantOneBaseGamePotion();
    }

    private boolean hasEndlessDarknessInDeck() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) {
            return false;
        }
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (EndlessDarkness.ID.equals(c.cardID)) {
                return true;
            }
        }
        return false;
    }

    private void increasePotionSlots(int amount) {
        if (amount <= 0 || AbstractDungeon.player == null || AbstractDungeon.player.potions == null) {
            return;
        }

        int oldSlots = AbstractDungeon.player.potionSlots;
        AbstractDungeon.player.potionSlots += amount;
        for (int i = oldSlots; i < AbstractDungeon.player.potionSlots; i++) {
            AbstractDungeon.player.potions.add(new PotionSlot(i));
        }
        AbstractDungeon.player.adjustPotionPositions();
    }

    private void grantOneBaseGamePotion() {
        if (AbstractDungeon.player == null) {
            return;
        }

        AbstractPotion potion = getRandomBaseGamePotion();
        if (potion != null) {
            AbstractDungeon.player.obtainPotion(potion);
        }
    }

    private AbstractPotion getRandomBaseGamePotion() {
        if (PotionHelper.potions == null || PotionHelper.potions.isEmpty()) {
            return AbstractDungeon.returnRandomPotion();
        }

        ArrayList<String> candidates = new ArrayList<>();
        for (String id : PotionHelper.potions) {
            if (id != null && !id.contains(":")) {
                candidates.add(id);
            }
        }

        if (candidates.isEmpty()) {
            return AbstractDungeon.returnRandomPotion();
        }

        String chosenId = candidates.get(AbstractDungeon.potionRng.random(candidates.size() - 1));
        AbstractPotion template = PotionHelper.getPotion(chosenId);
        return template == null ? null : template.makeCopy();
    }

    private void giveTwoBaseGameRelics() {
        AbstractRelic first = getRandomBaseGameRelic();
        if (first != null) {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float) Settings.WIDTH / 2.0F - 120.0F, (float) Settings.HEIGHT / 2.0F, first);
        }

        AbstractRelic second = getRandomBaseGameRelic();
        if (second != null) {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float) Settings.WIDTH / 2.0F + 120.0F, (float) Settings.HEIGHT / 2.0F, second);
        }
    }

    private AbstractRelic getRandomBaseGameRelic() {
        AbstractRelic.RelicTier firstTier = rollTier();
        AbstractRelic relic = drawBaseGameRelicFromTier(firstTier);
        if (relic != null) return relic;

        for (AbstractRelic.RelicTier tier : Arrays.asList(
                AbstractRelic.RelicTier.COMMON,
                AbstractRelic.RelicTier.UNCOMMON,
                AbstractRelic.RelicTier.RARE)) {
            if (tier == firstTier) continue;
            relic = drawBaseGameRelicFromTier(tier);
            if (relic != null) return relic;
        }
        return null;
    }

    private AbstractRelic drawBaseGameRelicFromTier(AbstractRelic.RelicTier tier) {
        ArrayList<String> pool = getRelicPoolByTier(tier);
        if (pool == null || pool.isEmpty()) {
            return null;
        }

        ArrayList<String> candidates = new ArrayList<>();
        for (String id : pool) {
            if (id == null || id.contains(":")) continue;
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(id)) continue;
            if (RelicLibrary.getRelic(id) == null) continue;
            candidates.add(id);
        }

        if (candidates.isEmpty()) {
            return null;
        }

        String chosenId = candidates.get(AbstractDungeon.relicRng.random(candidates.size() - 1));
        pool.remove(chosenId);
        AbstractRelic template = RelicLibrary.getRelic(chosenId);
        return template == null ? null : template.makeCopy();
    }

    private ArrayList<String> getRelicPoolByTier(AbstractRelic.RelicTier tier) {
        switch (tier) {
            case COMMON:
                return AbstractDungeon.commonRelicPool;
            case UNCOMMON:
                return AbstractDungeon.uncommonRelicPool;
            case RARE:
                return AbstractDungeon.rareRelicPool;
            default:
                return null;
        }
    }

    private AbstractRelic.RelicTier rollTier() {
        int roll = AbstractDungeon.miscRng.random(0, 99);
        if (roll < 50) return AbstractRelic.RelicTier.COMMON;
        if (roll < 85) return AbstractRelic.RelicTier.UNCOMMON;
        return AbstractRelic.RelicTier.RARE;
    }
}
