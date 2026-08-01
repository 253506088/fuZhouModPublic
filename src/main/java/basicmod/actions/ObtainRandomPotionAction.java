package basicmod.actions;

import basicmod.BasicMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;

/**
 * 获得随机药水动作。
 * 用于战斗中发放一瓶随机药水，药水栏满时交给原版 obtainPotion 处理。
 */
public class ObtainRandomPotionAction extends AbstractGameAction {
    private final AbstractPlayer player;

    /**
     * 构造函数。
     *
     * @param player 获得药水的玩家
     */
    public ObtainRandomPotionAction(AbstractPlayer player) {
        this.player = player;
        this.actionType = ActionType.SPECIAL;
    }

    /**
     * 执行获得药水逻辑。
     */
    @Override
    public void update() {
        if (this.player != null && !this.player.isDeadOrEscaped()) {
            AbstractPotion potion = AbstractDungeon.returnRandomPotion();
            if (potion != null) {
                boolean obtained = this.player.obtainPotion(potion);
                BasicMod.logger.info("【给皇帝的供奉】获得随机药水：{}，是否成功={}", potion.name, obtained);
            }
        }
        this.isDone = true;
    }
}
