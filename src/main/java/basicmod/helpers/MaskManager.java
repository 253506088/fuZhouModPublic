package basicmod.helpers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.LinkedList;

public class MaskManager {
    private static final int BASE_MASK_CAPACITY = 2;
    private static final int MAX_MASK_CAPACITY = 10;
    public static LinkedList<String> maskHistory = new LinkedList<>();

    // 必须与 TaLaPower 的 ID 保持一致
    public static final String TALA_MASK_POWER_ID = basicmod.BasicMod.makeID("TaLaPower");

    public static int shadowKhanCardsPlayedThisTurn = 0;
    public static int mingTaTotalDamage = 0;
    // 本场战斗额外面具容量（不附带塔拉的“每回合随机发兵团”效果）
    public static int extraMaskCapacityThisCombat = 0;

    public static void clear() {
        maskHistory.clear();
        shadowKhanCardsPlayedThisTurn = 0;
        mingTaTotalDamage = 0;
        extraMaskCapacityThisCombat = 0;

        // 战斗结束时同步清理面具轨道球
        if (AbstractDungeon.player != null) {
            AbstractDungeon.player.maxOrbs = 0;
            AbstractDungeon.player.orbs.clear();
        }
    }

    public static int getMaskCapacity(AbstractCreature owner) {
        int talaAmount = 0;
        if (owner != null) {
            AbstractPower tala = owner.getPower(TALA_MASK_POWER_ID);
            talaAmount = (tala != null ? tala.amount : 0);
        }
        int capacity = BASE_MASK_CAPACITY + talaAmount + Math.max(0, extraMaskCapacityThisCombat);
        return Math.min(capacity, MAX_MASK_CAPACITY);
    }

    public static void addExtraMaskCapacity(int amount) {
        if (amount <= 0) {
            return;
        }
        extraMaskCapacityThisCombat += amount;
        updateMaskOrbs();
    }

    public static void maximizeMaskCapacityThisCombat() {
        extraMaskCapacityThisCombat = Math.max(extraMaskCapacityThisCombat, MAX_MASK_CAPACITY - BASE_MASK_CAPACITY);
        updateMaskOrbs();
    }

    // 当外部能力变化导致容量下降时，按 FIFO 立刻挤出超量面具
    public static void enforceCapacity(AbstractCreature owner) {
        if (owner == null) {
            return;
        }
        int capacity = getMaskCapacity(owner);
        while (maskHistory.size() > capacity) {
            evictOldestMask(owner);
        }
        updateMaskOrbs();
    }

    private static void evictOldestMask(AbstractCreature owner) {
        String oldestId = maskHistory.removeFirst();
        AbstractPower oldestPower = owner.getPower(oldestId);
        if (oldestPower != null) {
            if (oldestPower instanceof basicmod.powers.BaseMaskPower) {
                ((basicmod.powers.BaseMaskPower) oldestPower).onEvict();
            }
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(owner, owner, oldestPower));
        }
    }

    public static void updateMaskOrbs() {
        if (AbstractDungeon.player == null) {
            return;
        }

        AbstractPower tala = AbstractDungeon.player.getPower(TALA_MASK_POWER_ID);
        int capacity = getMaskCapacity(AbstractDungeon.player); // 槽位上限 10
        AbstractDungeon.player.maxOrbs = capacity;
        AbstractDungeon.player.orbs.clear();

        // 1) 先绘制塔拉面具球（若存在）
        if (tala != null && tala.amount > 0) {
            AbstractDungeon.player.orbs.add(new basicmod.orbs.GenericMaskOrb(tala.ID, tala.name, tala.amount));
        }

        // 2) 按历史顺序绘制其他面具球
        for (String maskId : maskHistory) {
            AbstractPower power = AbstractDungeon.player.getPower(maskId);
            if (power != null) {
                AbstractDungeon.player.orbs.add(new basicmod.orbs.GenericMaskOrb(power.ID, power.name, power.amount));
            }
        }

        // 3) 剩余槽位用空球补齐
        while (AbstractDungeon.player.orbs.size() < capacity) {
            AbstractDungeon.player.orbs.add(new basicmod.orbs.EmptyMaskOrbSlot());
        }

        // 4) 兜底防护：避免极端情况下超过 maxOrbs 导致布局异常
        if (AbstractDungeon.player.orbs.size() > AbstractDungeon.player.maxOrbs) {
            AbstractDungeon.player.maxOrbs = AbstractDungeon.player.orbs.size();
        }

        // 5) 重新布局所有球位
        for (int i = 0; i < AbstractDungeon.player.orbs.size(); i++) {
            AbstractDungeon.player.orbs.get(i).setSlot(i, AbstractDungeon.player.maxOrbs);
        }
    }

    public static void onMaskApplied(AbstractCreature owner, String maskPowerId, AbstractPower maskPowerInstance) {
        if (maskPowerId.equals(TALA_MASK_POWER_ID)) {
            return; // 塔拉不参与普通面具 FIFO 队列
        }

        if (maskHistory.contains(maskPowerId)) {
            // 已存在的面具类型只叠层，不改变 FIFO 顺序
            return;
        }

        // 新面具进入：满槽时按 FIFO 挤出最早面具
        int capacity = getMaskCapacity(owner);
        while (maskHistory.size() >= capacity) {
            evictOldestMask(owner);
        }

        // 新面具追加到队尾，作为最新项
        maskHistory.addLast(maskPowerId);
    }

    public static void onMaskRemoved(String maskPowerId) {
        // 兜底：若面具因其他路径移除，同步清理历史记录
        if (maskHistory.contains(maskPowerId)) {
            maskHistory.remove(maskPowerId);
        }
    }
}
