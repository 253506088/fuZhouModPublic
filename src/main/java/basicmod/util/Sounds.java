package basicmod.util;

import static basicmod.BasicMod.audioPath;

/**
 * 统一声明本 Mod 需要注册的音频资源路径。
 */
public class Sounds {
    public static String TEST_SOUND = audioPath("test.wav"); //Load audio using a given path
    public static String ding; //Load audio from audioPath based on the field name

    // 阿福卡牌出牌音效：字段值会被 BasicMod.receiveAddAudio 统一注册为播放 key。
    public static String AFU_ONE_TIGER_TWO_SHEEP = audioPath("afu/OneTigerTwoSheep.mp3");
    public static String AFU_CROW_FLYING = audioPath("afu/CrowFlying.mp3");
    public static String AFU_TWO_DRAGONS = audioPath("afu/TwoDragons.mp3");
    public static String AFU_ELEPHANT_KICK = audioPath("afu/ElephantKick.mp3");
    public static String AFU_GOAT_CLIMBS_MOUNTAIN = audioPath("afu/GoatClimbsMountain.mp3");
    public static String AFU_AXE_CUTS_TREE = audioPath("afu/AxeCutsTree.mp3");
    public static String AFU_FLAMINGO_STEP = audioPath("afu/FlamingoStep.mp3");
    public static String AFU_LAME_GOOSE = audioPath("afu/LameGoose.mp3");
    public static String AFU_TAI_SHAN_PRESS = audioPath("afu/TaiShanPress.mp3");
    public static String AFU_METEOR_HITS_MOUNTAIN = audioPath("afu/MeteorHitsMountain.mp3");
    public static String AFU_LASER_EYES = audioPath("afu/LaserEyes.mp3");
    public static String AFU_RAT_STEALS_CHEESE = audioPath("afu/RatStealsCheese.mp3");
    public static String AFU_HEAD_CRACKS_WALNUT = audioPath("afu/HeadCracksWalnut.mp3");
    public static String AFU_BEAT_JACKIE = audioPath("afu/AfuBeatJackie.mp3");
    public static String AFU_COMBO_MOVE = audioPath("afu/ComboMove.mp3");
    public static String AFU_CRANE_CATCHES_SHRIMP = audioPath("afu/CraneCatchesShrimp.mp3");
    public static String AFU_EAGLE_WINGS = audioPath("afu/EagleWings.mp3");
    public static String AFU_TORNADO = audioPath("afu/Tornado.mp3");
    public static String NOTHING_LACKING_CARD = audioPath("cards/NothingLackingCard.mp3");
}
