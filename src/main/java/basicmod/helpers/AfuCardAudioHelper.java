package basicmod.helpers;

import basicmod.cards.CardAfuBeatJackie;
import basicmod.cards.CardAxeCutsTree;
import basicmod.cards.CardComboMove;
import basicmod.cards.CardCraneCatchesShrimp;
import basicmod.cards.CardCrowFlying;
import basicmod.cards.CardEagleWings;
import basicmod.cards.CardElephantKick;
import basicmod.cards.CardFlamingoStep;
import basicmod.cards.CardGoatClimbsMountain;
import basicmod.cards.CardHeadCracksWalnut;
import basicmod.cards.CardLameGoose;
import basicmod.cards.CardLaserEyes;
import basicmod.cards.CardMeteorHitsMountain;
import basicmod.cards.CardOneTigerTwoSheep;
import basicmod.cards.CardRatStealsCheese;
import basicmod.cards.CardTaiShanPress;
import basicmod.cards.CardTornado;
import basicmod.cards.CardTwoDragons;
import basicmod.enums.CustomTags;
import basicmod.util.Sounds;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 负责在阿福卡牌被打出时播放对应语音。
 */
public final class AfuCardAudioHelper {
    /**
     * 卡牌 ID 到音频 key 的映射。
     */
    private static final Map<String, String> AUDIO_BY_CARD_ID = createAudioMap();

    private AfuCardAudioHelper() {
    }

    /**
     * 如果传入卡牌是已配置音频的阿福卡，就播放它的专属语音。
     */
    public static void playIfAfuCard(AbstractCard card) {
        if (card == null || !card.hasTag(CustomTags.afu)) {
            return;
        }

        String audioKey = AUDIO_BY_CARD_ID.get(card.cardID);
        if (audioKey != null) {
            CardCrawlGame.sound.play(audioKey);
        }
    }

    /**
     * 创建不可修改的音频映射，避免运行中被误改。
     */
    private static Map<String, String> createAudioMap() {
        Map<String, String> audioByCardId = new HashMap<>();
        // 普通阿福卡：文件名已经统一改成英文卡牌 ID，避免中文资源名打包出问题。
        audioByCardId.put(CardOneTigerTwoSheep.ID, Sounds.AFU_ONE_TIGER_TWO_SHEEP);
        audioByCardId.put(CardCrowFlying.ID, Sounds.AFU_CROW_FLYING);
        audioByCardId.put(CardTwoDragons.ID, Sounds.AFU_TWO_DRAGONS);
        audioByCardId.put(CardElephantKick.ID, Sounds.AFU_ELEPHANT_KICK);
        audioByCardId.put(CardGoatClimbsMountain.ID, Sounds.AFU_GOAT_CLIMBS_MOUNTAIN);
        audioByCardId.put(CardAxeCutsTree.ID, Sounds.AFU_AXE_CUTS_TREE);
        audioByCardId.put(CardFlamingoStep.ID, Sounds.AFU_FLAMINGO_STEP);
        audioByCardId.put(CardLameGoose.ID, Sounds.AFU_LAME_GOOSE);
        audioByCardId.put(CardTaiShanPress.ID, Sounds.AFU_TAI_SHAN_PRESS);
        audioByCardId.put(CardMeteorHitsMountain.ID, Sounds.AFU_METEOR_HITS_MOUNTAIN);
        audioByCardId.put(CardLaserEyes.ID, Sounds.AFU_LASER_EYES);
        audioByCardId.put(CardRatStealsCheese.ID, Sounds.AFU_RAT_STEALS_CHEESE);
        audioByCardId.put(CardHeadCracksWalnut.ID, Sounds.AFU_HEAD_CRACKS_WALNUT);
        audioByCardId.put(CardAfuBeatJackie.ID, Sounds.AFU_BEAT_JACKIE);
        audioByCardId.put(CardComboMove.ID, Sounds.AFU_COMBO_MOVE);
        audioByCardId.put(CardCraneCatchesShrimp.ID, Sounds.AFU_CRANE_CATCHES_SHRIMP);
        audioByCardId.put(CardEagleWings.ID, Sounds.AFU_EAGLE_WINGS);
        // “山”的来源音频文件名里带有旧卡名“龙卷风摧毁停车场”，这里按当前卡牌 ID 绑定。
        audioByCardId.put(CardTornado.ID, Sounds.AFU_TORNADO);
        return Collections.unmodifiableMap(audioByCardId);
    }
}
