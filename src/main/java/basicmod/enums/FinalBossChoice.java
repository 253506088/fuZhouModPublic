package basicmod.enums;

/**
 * 最终Boss选择枚举。
 * 用于配置游戏的最终Boss类型。
 */
public enum FinalBossChoice {
    /** 心脏（原版最终Boss） */
    HEART,
    /** 大法师老爹（Mod自定义Boss） */
    DAD,
    /** 随机选择 */
    RANDOM;

    /**
     * 从字符串解析FinalBossChoice。
     *
     * @param raw 字符串
     * @return 对应的枚举值，默认返回DAD
     */
    public static FinalBossChoice fromString(String raw) {
        if (raw == null) {
            return DAD;
        }
        for (FinalBossChoice choice : values()) {
            if (choice.name().equalsIgnoreCase(raw.trim())) {
                return choice;
            }
        }
        return DAD;
    }

    /**
     * 从序号解析FinalBossChoice。
     *
     * @param raw 序号
     * @param fallback 默认值
     * @return 对应的枚举值
     */
    public static FinalBossChoice fromOrdinal(Integer raw, FinalBossChoice fallback) {
        if (raw == null) {
            return fallback;
        }
        FinalBossChoice[] vals = values();
        if (raw < 0 || raw >= vals.length) {
            return fallback;
        }
        return vals[raw];
    }
}

