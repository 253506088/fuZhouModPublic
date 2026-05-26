package basicmod.helpers;

import basicmod.cards.EndlessDarkness;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import java.util.ArrayList;

/**
 * 无尽黑暗辅助类。
 * 检查主牌组是否集齐10张面具，如果是则触发觉醒，获得"终极黑暗"卡牌。
 */
public class EndlessDarknessHelper {

    /**
     * 检查主牌组是否集齐10张面具，如果是则触发觉醒。
     */
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
    
    /**
     * 触发觉醒：移除10张面具，获得"终极黑暗"卡牌，播放音效和特效。
     *
     * @param requiredMasks 需要移除的面具ID数组
     */
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
        CardRewardHelper.grantCardToMasterDeck(endDark);
        CardRewardHelper.showCardBriefly(endDark);
        
        // 3. 播放音效与视觉特效
        com.megacrit.cardcrawl.core.CardCrawlGame.sound.play("INTIMIDATE"); 
        AbstractDungeon.topLevelEffectsQueue.add(new com.megacrit.cardcrawl.vfx.combat.GiantFireEffect());
        AbstractDungeon.topLevelEffectsQueue.add(new com.megacrit.cardcrawl.vfx.BorderLongFlashEffect(com.badlogic.gdx.graphics.Color.BLACK.cpy()));
    }
}
