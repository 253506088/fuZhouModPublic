package basicmod.helpers;

import basicmod.cards.EndlessDarkness;
import basicmod.cards.NothingLackingCard;
import basicmod.enums.CustomTags;
import basicmod.relics.PanKuBox;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;
import java.util.Collection;

public class GrandMageDadHelper {
    private static final int TALISMAN_RELIC_BONUS_PER = 30;
    private static final int PANKU_DEMON_BONUS_PER = 30;
    private static final int MASK_CARD_BONUS_PER = 15;
    private static final int MASK_SUPPORT_CARD_BONUS_PER = 15;
    private static final int TALISMAN_CARD_BONUS_PER = 20;
    private static final int ORDINARY_CARD_BONUS_PER = 5;
    private static final int NOTHING_LACKING_BONUS = 100;
    private static final int ENDLESS_DARKNESS_BONUS = 300;

    private GrandMageDadHelper() {
    }

    public static int computeDynamicHpBonus() {
        return buildDynamicHpBonusReport().totalBonus;
    }

    public static int downgradeNothingLackingForGrandMageDad() {
        if (AbstractDungeon.player == null) {
            return 0;
        }
        int count = 0;
        count += downgradeNothingLackingInGroup(AbstractDungeon.player.masterDeck);
        count += downgradeNothingLackingInGroup(AbstractDungeon.player.hand);
        count += downgradeNothingLackingInGroup(AbstractDungeon.player.drawPile);
        count += downgradeNothingLackingInGroup(AbstractDungeon.player.discardPile);
        count += downgradeNothingLackingInGroup(AbstractDungeon.player.exhaustPile);
        count += downgradeNothingLackingInGroup(AbstractDungeon.player.limbo);
        if (count > 0 && AbstractDungeon.player.hand != null) {
            AbstractDungeon.player.hand.applyPowers();
            AbstractDungeon.player.hand.glowCheck();
        }
        return count;
    }

    public static DynamicHpBonusReport buildDynamicHpBonusReport() {
        ArrayList<String> talismanDetails = collectOwnedTalismans();
        ArrayList<String> panKuDemonDetails = collectPanKuDemonDetails();
        DeckCardBonusReport deckReport = collectDeckCardBonuses();

        int talismanCount = talismanDetails.size();
        int panKuDemonCount = panKuDemonDetails.size();
        int talismanRawBonus = TALISMAN_RELIC_BONUS_PER * talismanCount;
        int panKuDemonRawBonus = PANKU_DEMON_BONUS_PER * panKuDemonCount;
        int totalBonus = talismanRawBonus + panKuDemonRawBonus + deckReport.totalBonus;

        return new DynamicHpBonusReport(
                talismanDetails,
                panKuDemonDetails,
                deckReport.maskCardDetails,
                deckReport.maskSupportCardDetails,
                deckReport.talismanCardDetails,
                deckReport.ordinaryCardDetails,
                deckReport.ultimateCardDetails,
                talismanCount,
                panKuDemonCount,
                deckReport.maskCardDetails.size(),
                deckReport.maskSupportCardDetails.size(),
                deckReport.talismanCardDetails.size(),
                deckReport.ordinaryCardDetails.size(),
                deckReport.ultimateCardDetails.size(),
                talismanRawBonus,
                panKuDemonRawBonus,
                deckReport.maskRawBonus,
                deckReport.maskSupportRawBonus,
                deckReport.talismanCardRawBonus,
                deckReport.ordinaryRawBonus,
                deckReport.ultimateRawBonus,
                totalBonus
        );
    }

    private static ArrayList<String> collectOwnedTalismans() {
        ArrayList<String> details = new ArrayList<>();
        if (AbstractDungeon.player == null) {
            return details;
        }
        for (String id : TalismanHelper.ALL_TALISMAN_IDS) {
            if (AbstractDungeon.player.hasRelic(id)) {
                AbstractRelic relic = AbstractDungeon.player.getRelic(id);
                details.add(formatRelic(relic, id));
            }
        }
        return details;
    }

    private static ArrayList<String> collectPanKuDemonDetails() {
        ArrayList<String> details = new ArrayList<>();
        if (AbstractDungeon.player == null || !AbstractDungeon.player.hasRelic(PanKuBox.ID)) {
            return details;
        }
        for (Class<?> demonClass : PanKuBox.getOwnedDemonElements()) {
            if (details.size() >= 8) {
                break;
            }
            details.add(formatPanKuDemonElement(demonClass));
        }
        return details;
    }

    private static DeckCardBonusReport collectDeckCardBonuses() {
        DeckCardBonusReport report = new DeckCardBonusReport();
        if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) {
            return report;
        }
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c == null) {
                continue;
            }
            if (NothingLackingCard.ID.equals(c.cardID)) {
                report.ultimateCardDetails.add(formatCard(c) + "+" + NOTHING_LACKING_BONUS);
                report.ultimateRawBonus += NOTHING_LACKING_BONUS;
            } else if (EndlessDarkness.ID.equals(c.cardID)) {
                report.ultimateCardDetails.add(formatCard(c) + "+" + ENDLESS_DARKNESS_BONUS);
                report.ultimateRawBonus += ENDLESS_DARKNESS_BONUS;
            } else if (c.hasTag(CustomTags.MASK)) {
                report.maskCardDetails.add(formatCard(c));
                report.maskRawBonus += MASK_CARD_BONUS_PER;
            } else if (c.hasTag(CustomTags.MASK_SUPPORT)) {
                report.maskSupportCardDetails.add(formatCard(c));
                report.maskSupportRawBonus += MASK_SUPPORT_CARD_BONUS_PER;
            } else if (c.hasTag(CustomTags.TALISMAN_CARD)) {
                report.talismanCardDetails.add(formatCard(c));
                report.talismanCardRawBonus += TALISMAN_CARD_BONUS_PER;
            } else {
                report.ordinaryCardDetails.add(formatCard(c));
                report.ordinaryRawBonus += ORDINARY_CARD_BONUS_PER;
            }
        }
        report.totalBonus = report.maskRawBonus
                + report.maskSupportRawBonus
                + report.talismanCardRawBonus
                + report.ordinaryRawBonus
                + report.ultimateRawBonus;
        return report;
    }

    private static int downgradeNothingLackingInGroup(CardGroup group) {
        if (group == null) {
            return 0;
        }
        int count = 0;
        for (AbstractCard c : group.group) {
            if (c instanceof NothingLackingCard && c.upgraded) {
                ((NothingLackingCard) c).downgradeForGrandMageDad();
                count++;
            }
        }
        return count;
    }

    private static String formatRelic(AbstractRelic relic, String id) {
        if (relic == null) {
            return "未知符咒(" + id + ")";
        }
        return relic.name + "(" + id + ")";
    }

    private static String formatPanKuDemonElement(Class<?> demonClass) {
        if (demonClass == null) {
            return "未知魔气";
        }
        for (AbstractCard c : com.megacrit.cardcrawl.helpers.CardLibrary.getAllCards()) {
            if (c != null && c.getClass().equals(demonClass)) {
                return formatCard(c);
            }
        }
        return demonClass.getSimpleName();
    }

    private static String formatCard(AbstractCard c) {
        if (c == null) {
            return "空卡";
        }
        String upgraded = c.upgraded ? ",+" + c.timesUpgraded : "";
        return c.name + "(" + c.cardID + upgraded + ")";
    }

    private static class DeckCardBonusReport {
        private final ArrayList<String> maskCardDetails = new ArrayList<>();
        private final ArrayList<String> maskSupportCardDetails = new ArrayList<>();
        private final ArrayList<String> talismanCardDetails = new ArrayList<>();
        private final ArrayList<String> ordinaryCardDetails = new ArrayList<>();
        private final ArrayList<String> ultimateCardDetails = new ArrayList<>();
        private int maskRawBonus;
        private int maskSupportRawBonus;
        private int talismanCardRawBonus;
        private int ordinaryRawBonus;
        private int ultimateRawBonus;
        private int totalBonus;
    }

    public static class DynamicHpBonusReport {
        public final ArrayList<String> talismanDetails;
        public final ArrayList<String> panKuDemonDetails;
        public final ArrayList<String> maskCardDetails;
        public final ArrayList<String> maskSupportCardDetails;
        public final ArrayList<String> talismanCardDetails;
        public final ArrayList<String> ordinaryCardDetails;
        public final ArrayList<String> ultimateCardDetails;
        public final int talismanCount;
        public final int panKuDemonCount;
        public final int maskCount;
        public final int maskSupportCount;
        public final int talismanCardCount;
        public final int ordinaryCardCount;
        public final int ultimateCardCount;
        public final int talismanRawBonus;
        public final int panKuDemonRawBonus;
        public final int maskRawBonus;
        public final int maskSupportRawBonus;
        public final int talismanCardRawBonus;
        public final int ordinaryCardRawBonus;
        public final int ultimateCardRawBonus;
        public final int totalBonus;

        public DynamicHpBonusReport(
                ArrayList<String> talismanDetails,
                ArrayList<String> panKuDemonDetails,
                ArrayList<String> maskCardDetails,
                ArrayList<String> maskSupportCardDetails,
                ArrayList<String> talismanCardDetails,
                ArrayList<String> ordinaryCardDetails,
                ArrayList<String> ultimateCardDetails,
                int talismanCount,
                int panKuDemonCount,
                int maskCount,
                int maskSupportCount,
                int talismanCardCount,
                int ordinaryCardCount,
                int ultimateCardCount,
                int talismanRawBonus,
                int panKuDemonRawBonus,
                int maskRawBonus,
                int maskSupportRawBonus,
                int talismanCardRawBonus,
                int ordinaryCardRawBonus,
                int ultimateCardRawBonus,
                int totalBonus
        ) {
            this.talismanDetails = talismanDetails;
            this.panKuDemonDetails = panKuDemonDetails;
            this.maskCardDetails = maskCardDetails;
            this.maskSupportCardDetails = maskSupportCardDetails;
            this.talismanCardDetails = talismanCardDetails;
            this.ordinaryCardDetails = ordinaryCardDetails;
            this.ultimateCardDetails = ultimateCardDetails;
            this.talismanCount = talismanCount;
            this.panKuDemonCount = panKuDemonCount;
            this.maskCount = maskCount;
            this.maskSupportCount = maskSupportCount;
            this.talismanCardCount = talismanCardCount;
            this.ordinaryCardCount = ordinaryCardCount;
            this.ultimateCardCount = ultimateCardCount;
            this.talismanRawBonus = talismanRawBonus;
            this.panKuDemonRawBonus = panKuDemonRawBonus;
            this.maskRawBonus = maskRawBonus;
            this.maskSupportRawBonus = maskSupportRawBonus;
            this.talismanCardRawBonus = talismanCardRawBonus;
            this.ordinaryCardRawBonus = ordinaryCardRawBonus;
            this.ultimateCardRawBonus = ultimateCardRawBonus;
            this.totalBonus = totalBonus;
        }

        public String talismanText() {
            return joinDetails(talismanDetails);
        }

        public String panKuDemonText() {
            return joinDetails(panKuDemonDetails);
        }

        public String maskText() {
            return joinDetails(maskCardDetails);
        }

        public String maskSupportText() {
            return joinDetails(maskSupportCardDetails);
        }

        public String talismanCardText() {
            return joinDetails(talismanCardDetails);
        }

        public String ordinaryCardText() {
            return joinDetails(ordinaryCardDetails);
        }

        public String ultimateCardText() {
            return joinDetails(ultimateCardDetails);
        }

        private String joinDetails(Collection<String> details) {
            if (details == null || details.isEmpty()) {
                return "无";
            }
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String detail : details) {
                if (!first) {
                    sb.append("、");
                }
                sb.append(detail);
                first = false;
            }
            return sb.toString();
        }
    }

    public static ArrayList<String> getSealableTalismans(Collection<String> excludedIds) {
        ArrayList<String> candidates = new ArrayList<>();
        if (AbstractDungeon.player == null) {
            return candidates;
        }
        for (String id : TalismanHelper.ALL_TALISMAN_IDS) {
            if (excludedIds != null && excludedIds.contains(id)) {
                continue;
            }
            if (AbstractDungeon.player.hasRelic(id)) {
                candidates.add(id);
            }
        }
        return candidates;
    }
}
