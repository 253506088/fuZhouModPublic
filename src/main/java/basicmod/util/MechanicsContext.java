package basicmod.util;

/**
 * 用来管理战斗中的一些全局状态标志位
 */
public class MechanicsContext {
    // 标志位：是否正在处理“主动翻倍”逻辑（如巨大小玉或未羊）
    // 如果为 true，则被动的翻倍逻辑（石盘、羊符咒）应跳过叠加动作，避免 2*2=4 的问题
    public static boolean isProcessingDoubling = false;

    // 标志位：是否正在执行“月之恶魔力量反转”的专用动作
    // 用于精确豁免羊符咒对这一次负面施加的翻倍，不影响同一卡的其他负面效果
    public static boolean isApplyingMoonInversion = false;
}
