package basicmod.powers;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class ShenZhuRevivedPower extends AbstractPower {
    public static final String POWER_ID = BasicMod.makeID("ShenZhuRevivedPower");
    public static final String NAME = "圣主复苏";
    public static final String[] DESCRIPTIONS = { "持有鼠符咒圣主恢复了真身。还有一件事！ 符咒之力聚得越齐，圣主就越难对付！" };

    public ShenZhuRevivedPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.BUFF;
        this.loadRegion("demonForm"); // 使用原版的恶魔形态红眼图片作为标志
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
