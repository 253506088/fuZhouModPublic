package basicmod.actions;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;

/**
 * 骰子投掷Action
 * 随机生成1~6的点数，根据isAngel决定是提升自身力量还是降低目标力量
 * 附带头顶气泡文字显示骰子点数
 */
public class DiceRollAction extends AbstractGameAction {
    private AbstractCreature target;
    private AbstractPlayer source;
    private boolean isAngel; // true=天使骰子(加力量), false=恶魔骰子(减力量)

    /**
     * @param target  效果目标（天使骰子时为玩家自身，恶魔骰子时为敌人）
     * @param source  施放者
     * @param isAngel true=天使骰子提升力量，false=恶魔骰子降低力量
     */
    public DiceRollAction(AbstractCreature target, AbstractPlayer source, boolean isAngel) {
        this.target = target;
        this.source = source;
        this.isAngel = isAngel;
        this.duration = Settings.ACTION_DUR_MED;
        this.actionType = ActionType.SPECIAL;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_MED) {
            // 掷骰子：随机1~6
            int roll = AbstractDungeon.cardRandomRng.random(1, 6);

            // 显示骰子效果气泡（统一显示在玩家头顶，避免怪物位置异常）
            String diceText;
            if (isAngel) {
                diceText = "[骰子] #b" + roll + " ! 力量 +" + roll;
            } else {
                diceText = "[骰子] #b" + roll + " ! 力量 -" + roll;
            }
            // 气泡统一显示在玩家头顶位置
            AbstractDungeon.effectList.add(new ThoughtBubble(source.dialogX, source.dialogY, 2.0F, diceText, true));

            BasicMod.logger.info("骰子投掷结果: " + roll + " (天使=" + isAngel + ")");

            // 施加力量变化
            if (isAngel) {
                // 天使骰子：提升目标（玩家）力量
                addToBot(new ApplyPowerAction(target, source, new StrengthPower(target, roll), roll));
            } else {
                // 恶魔骰子：降低目标（敌人）力量
                addToBot(new ApplyPowerAction(target, source, new StrengthPower(target, -roll), -roll));
            }
        }
        tickDuration();
    }
}
