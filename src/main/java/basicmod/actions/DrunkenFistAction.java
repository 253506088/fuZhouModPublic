package basicmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

/**
 * 醉拳动作。
 * 消耗所有能量，对所有存活敌人造成多次随机伤害。
 */
public class DrunkenFistAction extends AbstractGameAction {
    /** 是否免费使用一次 */
    private final boolean freeToPlayOnce;
    /** 玩家对象 */
    private final AbstractPlayer p;
    /** 使用时的能量值 */
    private final int energyOnUse;
    /** 是否升级 */
    private final boolean upgraded;

    /**
     * 构造函数。
     *
     * @param p 玩家对象
     * @param energyOnUse 使用时的能量值
     * @param upgraded 是否升级
     * @param freeToPlayOnce 是否免费使用一次
     */
    public DrunkenFistAction(AbstractPlayer p, int energyOnUse, boolean upgraded, boolean freeToPlayOnce) {
        this.p = p;
        this.energyOnUse = energyOnUse;
        this.upgraded = upgraded;
        this.freeToPlayOnce = freeToPlayOnce;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = ActionType.DAMAGE;
    }

    /**
     * 执行动作逻辑：根据能量值，对所有存活敌人造成多次随机伤害。
     */
    @Override
    public void update() {
        int effect = EnergyPanel.totalCount;
        if (this.energyOnUse != -1) {
            effect = this.energyOnUse;
        }

        if (this.p.hasRelic(ChemicalX.ID)) {
            effect += 2;
        }

        if (effect > 0) {
            for (int i = 0; i < effect; i++) {
                for (AbstractMonster target : AbstractDungeon.getMonsters().monsters) {
                    if (target == null || target.isDeadOrEscaped()) {
                        continue;
                    }

                    int min = this.upgraded ? 8 : 6;
                    int max = this.upgraded ? 12 : 10;
                    int baseDmg = AbstractDungeon.cardRandomRng.random(min, max);
                    DamageInfo info = new DamageInfo(this.p, baseDmg, DamageInfo.DamageType.NORMAL);
                    info.applyPowers(this.p, target);
                    addToBot(new DamageAction(target, info, AttackEffect.BLUNT_LIGHT));
                }
            }

            if (!this.freeToPlayOnce) {
                this.p.energy.use(EnergyPanel.totalCount);
            }
        }

        this.isDone = true;
    }
}
