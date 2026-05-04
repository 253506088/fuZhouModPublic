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

public class DrunkenFistAction extends AbstractGameAction {
    private boolean freeToPlayOnce;
    private AbstractPlayer p;
    private int energyOnUse;
    private boolean upgraded;

    public DrunkenFistAction(AbstractPlayer p, int energyOnUse, boolean upgraded, boolean freeToPlayOnce) {
        this.p = p;
        this.energyOnUse = energyOnUse;
        this.upgraded = upgraded;
        this.freeToPlayOnce = freeToPlayOnce;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = ActionType.DAMAGE;
    }

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
                // 1. 掷骰子计算基础伤害
                int min = upgraded ? 8 : 6;
                int max = upgraded ? 12 : 10;
                int baseDmg = AbstractDungeon.cardRandomRng.random(min, max);
                
                // 2. 选取随机目标
                AbstractMonster target = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                
                if (target != null) {
                    // 3. 构建伤害信息并应用威能计算（力量、虚弱等）
                    DamageInfo info = new DamageInfo(this.p, baseDmg, DamageInfo.DamageType.NORMAL);
                    info.applyPowers(this.p, target);
                    
                    // 使用 addToTop 确保这些伤害动作在当前 Action 结束后立即按顺序执行（由于是循环加入，最后加入的最先执行，这里用 addToTop 会导致顺序反向，但对随机目标无所谓）
                    // 为了保证表现顺序符合直觉，我们其实可以用正常的逻辑，或者在一次 update 里处理完。
                    // 实际上在 X 消耗 Action 里，通常直接触发伤害特效。
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
