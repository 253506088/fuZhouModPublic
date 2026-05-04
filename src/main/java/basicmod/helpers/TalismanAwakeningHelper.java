package basicmod.helpers;

import basicmod.cards.NothingLackingCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TalismanAwakeningHelper {
    private static final Logger logger = LogManager.getLogger(TalismanAwakeningHelper.class.getName());
    // 防止“异步入组未完成”窗口内被重复触发，导致重复发放《我什么都不缺了》
    private static boolean awakeningPending = false;
    
    // 集齐12符咒，圣主归位
    public static void checkAndTriggerAwakening() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) return;
        
        logger.info("【圣主觉醒检测】开始核验12符咒收集情况...");
        
        // 详细检查 12 符咒
        int count = 0;
        StringBuilder missing = new StringBuilder();
        for (String id : TalismanHelper.ALL_TALISMAN_IDS) {
            if (AbstractDungeon.player.hasRelic(id)) {
                count++;
            } else {
                missing.append(id).append(", ");
            }
        }

        // 如果已经拥有此卡，不再重复触发；并顺手清理防重入标记
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.cardID.equals(NothingLackingCard.ID)) {
                if (awakeningPending) {
                    logger.info("【圣主觉醒检测】检测到《我什么都不缺了》已入组，清除防重入标记。");
                    awakeningPending = false;
                }
                logger.info("【圣主觉醒检测】玩家卡组中已存在《我什么都不缺了》，跳过后续逻辑。");
                return;
            }
        }

        // 异步发卡进行中：若仍是12符咒则等待入组完成；若不足12则判定为陈旧标记并清理
        if (awakeningPending) {
            if (count >= 12) {
                logger.info("【圣主觉醒检测】觉醒流程进行中，等待《我什么都不缺了》入组完成，跳过本次触发。");
                return;
            }
            logger.info("【圣主觉醒检测】符咒数量不足12，判定防重入标记过期，已自动清理。");
            awakeningPending = false;
        }

        logger.info("【圣主觉醒检测】当前已集齐符咒数: " + count + " / 12");
        if (count < 12) {
            logger.info("【圣主觉醒检测】尚缺少的符咒: " + missing.toString());
        }

        // 检查 12 符咒是否集齐
        if (count >= 12) {
            logger.info("【圣主觉醒检测】条件达成！！！12枚符咒已悉数归位，正在触发觉醒...");
            triggerAwakening();
        }
    }
    
    private static void triggerAwakening() {
        // 同一时刻只允许排队一次觉醒发卡，避免并发检测造成重复入组
        if (awakeningPending) {
            logger.info("【圣主觉醒检测】检测到觉醒流程已在进行中，本次触发忽略。");
            return;
        }
        awakeningPending = true;

        // 0. 保留 12 个符咒遗物（老大爷说不要删除）
        // for (String id : TalismanHelper.ALL_TALISMAN_IDS) {
        //     AbstractDungeon.player.loseRelic(id);
        // }

        // 1. 获得“我什么都不缺了”卡牌
        AbstractCard card = new NothingLackingCard();
        AbstractDungeon.topLevelEffectsQueue.add(new ShowCardAndObtainEffect(card, Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f));
        
        // 2. 特效与音效：震撼登场
        com.megacrit.cardcrawl.core.CardCrawlGame.sound.play("GHOST_ORB_HOWL"); 
        AbstractDungeon.topLevelEffectsQueue.add(new com.megacrit.cardcrawl.vfx.combat.GiantFireEffect());
        AbstractDungeon.topLevelEffectsQueue.add(new com.megacrit.cardcrawl.vfx.BorderLongFlashEffect(com.badlogic.gdx.graphics.Color.GOLD.cpy()));
    }
}
