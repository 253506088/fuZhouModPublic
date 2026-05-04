package basicmod.helpers;

import basicmod.cards.EndlessDarkness;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import java.util.ArrayList;

public class EndlessDarknessHelper {
    
    // Check if the master deck contains all 10 masks. If so, trigger the Exodia awakening.
    public static void checkAndTriggerAwakening() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) return;
        
        // If we already have EndlessDarkness, abort
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c instanceof EndlessDarkness) return;
        }

        String[] requiredMasks = {
            "fuZhouMod:NiJiaMask", "fuZhouMod:LaZuoMask", "fuZhouMod:SaMoMask", 
            "fuZhouMod:BaTeMask", "fuZhouMod:KaBoMask", "fuZhouMod:LeiSuMask", 
            "fuZhouMod:ManNiMask", "fuZhouMod:MingTaMask", "fuZhouMod:YiKaMask", 
            "fuZhouMod:TaLaMask"
        };
        
        boolean hasAll = true;
        for (String id : requiredMasks) {
            boolean found = false;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c.cardID.equals(id)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                hasAll = false;
                break;
            }
        }
        
        if (hasAll) {
            triggerAwakening(requiredMasks);
        }
    }
    
    private static void triggerAwakening(String[] requiredMasks) {
        // Collect one instance of each mask to remove
        ArrayList<AbstractCard> cardsToRemove = new ArrayList<>();
        
        for (String id : requiredMasks) {
            AbstractCard unupgraded = null;
            AbstractCard upgraded = null;
            
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c.cardID.equals(id)) {
                    if (!c.upgraded) unupgraded = c;
                    else upgraded = c;
                }
            }
            
            // Prioritize removing the unupgraded version
            if (unupgraded != null) {
                cardsToRemove.add(unupgraded);
            } else if (upgraded != null) {
                cardsToRemove.add(upgraded);
            }
        }
        
        // 触发觉醒动画与逻辑：使用 AbstractDungeon.effects 而非 topLevelEffects 来避开并发修改异常
        // 1. 移除 10 张面具
        for (AbstractCard c : cardsToRemove) {
            AbstractDungeon.player.masterDeck.removeCard(c);
        }
        
        // 2. 获得“终极黑暗”卡牌
        AbstractCard endDark = new EndlessDarkness();
        // 此处改用 topLevelEffectsQueue.add 避免并发修改异常
        AbstractDungeon.topLevelEffectsQueue.add(new ShowCardAndObtainEffect(endDark, Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f));
        
        // 3. 播放音效与视觉特效
        com.megacrit.cardcrawl.core.CardCrawlGame.sound.play("INTIMIDATE"); 
        AbstractDungeon.topLevelEffectsQueue.add(new com.megacrit.cardcrawl.vfx.combat.GiantFireEffect());
        AbstractDungeon.topLevelEffectsQueue.add(new com.megacrit.cardcrawl.vfx.BorderLongFlashEffect(com.badlogic.gdx.graphics.Color.BLACK.cpy()));
    }
}
