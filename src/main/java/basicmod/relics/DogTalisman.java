package basicmod.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

public class DogTalisman extends BaseRelic {
    public static final String NAME = "DogTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    private boolean usedThisCombat = false;

    public DogTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atPreBattle() {
        usedThisCombat = false; // 战斗开始前重置本场战斗触发标记
        this.grayscale = false;
        this.pulse = true; // 遗物发光，提示可以使用
        this.beginPulse();
    }

    @Override
    public int onLoseHpLast(int damageAmount) {
        // 如果当前处于战斗阶段，受到的伤害大于等于当前血量（即受到致命伤害），且本局战斗未使用过
        if (canInteractInCombat()
                && damageAmount >= AbstractDungeon.player.currentHealth && !usedThisCombat) {
            usedThisCombat = true; // 标记本场战斗已使用
            this.grayscale = true; // 触发后变灰
            this.pulse = false; // 停止发光提示
            this.flash(); // 触发遗物闪烁特效
            
            float healPercent = (AbstractDungeon.player.hasRelic(RatTalisman.ID)) ? 0.35f : 0.25f;
            AbstractDungeon.player.currentHealth = (int) (AbstractDungeon.player.maxHealth * healPercent);
            
            AbstractDungeon.player.healthBarUpdatedEvent(); // 更新血条UI
            return 0; // Prevent the fatal damage 返回0伤害以阻止本次致命攻击
        }
        return damageAmount; // 否则正常受到伤害
    }

    @Override
    public void onVictory() {
        usedThisCombat = false; // 战斗胜利后重置状态
        this.grayscale = false; // 恢复彩色
        this.pulse = false; // 停止发光
    }

    @Override
    public String getUpdatedDescription() {
        return com.megacrit.cardcrawl.dungeons.AbstractDungeon.player != null && com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(RatTalisman.ID) ? (DESCRIPTIONS.length > 1 ? DESCRIPTIONS[1] : DESCRIPTIONS[0]) : DESCRIPTIONS[0];
    }
}
