package basicmod.orbs;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

/**
 * 空面具球槽位。
 * 继承原版空球槽位，但位置上移以避开石雕。
 */
public class EmptyMaskOrbSlot extends EmptyOrbSlot {
    /** Y轴偏移量，往上抬高以避开石雕 */
    private static final float Y_OFFSET = 120.0F * Settings.scale;

    /**
     * 构造函数。
     */
    public EmptyMaskOrbSlot() {
        super();
    }

    /**
     * 设置槽位位置，上移以避开石雕。
     */
    @Override
    public void setSlot(int slotNum, int maxOrbs) {
        super.setSlot(slotNum, maxOrbs);
        // 在原版算好的目标 Y 坐标基础上，强行增加高度，避开肥胖石雕
        this.tY += Y_OFFSET;
    }
}
