package basicmod.orbs;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

public class EmptyMaskOrbSlot extends EmptyOrbSlot {
    private static final float Y_OFFSET = 120.0F * Settings.scale; // 往上抬高多少像素

    public EmptyMaskOrbSlot() {
        super();
    }

    @Override
    public void setSlot(int slotNum, int maxOrbs) {
        super.setSlot(slotNum, maxOrbs);
        // 在原版算好的目标 Y 坐标基础上，强行增加高度，避开肥胖石雕
        this.tY += Y_OFFSET;
    }
}
