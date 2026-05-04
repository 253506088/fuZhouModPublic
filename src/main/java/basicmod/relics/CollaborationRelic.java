package basicmod.relics;
 
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
 
import static basicmod.BasicMod.makeID;
 
public class CollaborationRelic extends BaseRelic {
    public static final String NAME = "CollaborationRelic";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.SPECIAL;
    private static final LandingSound SOUND = LandingSound.MAGICAL;
 
    public CollaborationRelic() {
        super(ID, NAME, RARITY, SOUND);
    }
 
    @Override
    public void onEquip() {
        // 获得遗物时，强行重置一次卡池，从而解锁 TEAM_JACKIE 标签的卡牌
        if (com.megacrit.cardcrawl.core.CardCrawlGame.dungeon != null) {
            com.megacrit.cardcrawl.core.CardCrawlGame.dungeon.initializeCardPools();
        }
        this.flash();
    }
 
    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
