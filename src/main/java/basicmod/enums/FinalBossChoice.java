package basicmod.enums;

public enum FinalBossChoice {
    HEART,
    DAD,
    RANDOM;

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

