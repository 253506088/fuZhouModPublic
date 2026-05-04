package basicmod.events;

import basicmod.BasicMod;
import basicmod.helpers.TalismanHelper;
import com.megacrit.cardcrawl.cards.curses.Pride;
import com.megacrit.cardcrawl.cards.curses.Regret;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import basicmod.effects.RelicExhaustEffect;

import java.util.ArrayList;

public class RobTalismanEvent extends AbstractImageEvent {
    public static final String ID = BasicMod.makeID("RobTalismanEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    private int screenNum = 0;

    public RobTalismanEvent() {
        super(NAME, DESCRIPTIONS[0], "basicmod/images/events/RobTalisman.png");

        // 新增选项 0: 金蝉脱壳 (冒牌符咒)
        boolean hasFakeTalisman = false;
        for (com.megacrit.cardcrawl.cards.AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.cardID.equals(basicmod.cards.skills.CardFakeTalisman.ID)) {
                hasFakeTalisman = true;
                break;
            }
        }

        if (hasFakeTalisman) {
            this.imageEventText.setDialogOption(OPTIONS[6]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[7], true);
        }

        // 选项 1: 失去全部金币 + 悔恨 (金币 >= 200)
        if (AbstractDungeon.player.gold >= 200) {
            this.imageEventText.setDialogOption(OPTIONS[0] + AbstractDungeon.player.gold + OPTIONS[1], CardLibrary.getCopy(Regret.ID));
        } else {
            this.imageEventText.setDialogOption(OPTIONS[2], true);
        }

        // 选项 2: 随机失去一个符咒
        this.imageEventText.setDialogOption(OPTIONS[3]);

        // 选项 3: 减少血量上限 10 + 傲慢
        this.imageEventText.setDialogOption(OPTIONS[4], CardLibrary.getCopy(Pride.ID));
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // 金蝉脱壳 (冒牌符咒版)
                        com.megacrit.cardcrawl.cards.AbstractCard cardToRemove = null;
                        for (com.megacrit.cardcrawl.cards.AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                            if (c.cardID.equals(basicmod.cards.skills.CardFakeTalisman.ID)) {
                                if (cardToRemove == null || (cardToRemove.upgraded && !c.upgraded)) {
                                    cardToRemove = c;
                                }
                            }
                        }
                        if (cardToRemove != null) {
                            AbstractDungeon.topLevelEffects.add(new com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect(cardToRemove));
                            AbstractDungeon.player.masterDeck.removeCard(cardToRemove);
                        }
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        break;
                    case 1: // 大撒币逃走 (原选项 0)
                        AbstractDungeon.player.loseGold(AbstractDungeon.player.gold);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Regret(), (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        break;
                    case 2: // 舍弃符咒 (原选项 1)
                        ArrayList<String> ownedTalismans = new ArrayList<>();
                        for (String id : TalismanHelper.ALL_TALISMAN_IDS) {
                            if (AbstractDungeon.player.hasRelic(id)) {
                                ownedTalismans.add(id);
                            }
                        }
                        if (!ownedTalismans.isEmpty()) {
                            String toRemove = ownedTalismans.get(AbstractDungeon.miscRng.random(ownedTalismans.size() - 1));
                            AbstractRelic r = AbstractDungeon.player.getRelic(toRemove);
                            if (r != null) {
                                String relicName = r.name;
                                AbstractDungeon.topLevelEffects.add(new RelicExhaustEffect(r));
                                r.onUnequip();
                                AbstractDungeon.player.relics.remove(r);
                                AbstractDungeon.player.reorganizeRelics();
                                this.imageEventText.updateBodyText(DESCRIPTIONS[2] + " ( " + relicName + " )");
                            }
                        }
                        break;
                    case 3: // 硬扛伤害 (原选项 2)
                        AbstractDungeon.player.decreaseMaxHealth(10);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Pride(), (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        break;
                }
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[5]);
                this.screenNum = 1;
                break;
            case 1:
                this.openMap();
                break;
        }
    }
}
