package basicmod.cards.demons;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



import java.util.ArrayList;

public class CardMoonDemonQi extends BaseCard {
    public static final String ID = makeID("CardMoonDemonQi");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.NONE,
            2
    );

    public CardMoonDemonQi() {
        super(ID, info);
        setCostUpgrade(1);
        setExhaust(true);
        tags.add(CustomTags.EIGHT_DEMONS);
    }

    public static final Logger logger = LogManager.getLogger(CardMoonDemonQi.class.getName());

    public static void invertStats(AbstractCreature target) {
        if (target == null) {
            logger.debug("【月之恶魔】invertStats 被调用，但目标为 NULL。");
            return;
        }

        boolean escaped = false;
        if (target instanceof AbstractMonster) {
            escaped = ((AbstractMonster) target).escaped;
        }

        // 记录目标的详细生存状态，排查“假死/装死”Bug
        logger.debug("【月之恶魔】逻辑启动。目标: {}, 当前血量: {}/{}, isDead: {}, halfDead: {}, escaped: {}, isDeadOrEscaped: {}",
            target.name, target.currentHealth, target.maxHealth, target.isDead, target.halfDead, escaped, target.isDeadOrEscaped());


        // 如果真的判定死亡，虽然记录了但还是跳过逻辑（物理层面已经无法反转）
        if (target.isDeadOrEscaped()) {
            logger.debug("【月之恶魔】目标生存判定失败（已死亡或逃跑），中止反转。");
            return;
        }

        // 打印能力列表进行核对
        if (logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("当前能力列表: ");
            for (AbstractPower p : target.powers) {
                sb.append("[").append(p.ID).append(": ").append(p.amount).append("] ");
            }
            logger.debug(sb.toString());
        }

        // 统一处理属性取反（即时生效，不进入 Action 队列排队）
        boolean stateChanged = false;
        stateChanged |= processDirectInversion(target, StrengthPower.POWER_ID);
        stateChanged |= processDirectInversion(target, DexterityPower.POWER_ID);

        if (stateChanged) {
            logger.debug("【月之恶魔】反转修改成功，正在刷新 UI...");
            if (target instanceof AbstractMonster) {
                ((AbstractMonster) target).applyPowers();
            } else {
                AbstractDungeon.player.hand.applyPowers();
            }
        } else {
            logger.debug("【月之恶魔】未检测到可反转的非零数值。");
        }
    }


    /**
     * 直接修改 Power 数值的核心逻辑 (添加详细日志)
     */
    private static boolean processDirectInversion(AbstractCreature target, String powerId) {
        AbstractPower p = target.getPower(powerId);
        if (p != null) {
            int current = p.amount;
            if (current == 0) {
                logger.debug("【月之恶魔】{} 为 0，跳过。", powerId);
                return false;
            }

            // 1. 人工制品交互拦截 (仅当反转造成属性下降时拦截)
            // 规则：正变负 -> 属性下降 -> 触发 Artifact
            if (current > 0 && target.hasPower("Artifact")) {
                logger.debug("【月之恶魔】检测到 {} 反转方向为负向，且目标有【人工制品】保护。", powerId);
                target.getPower("Artifact").flash();
                AbstractDungeon.actionManager.addToTop(new com.megacrit.cardcrawl.actions.common.ReducePowerAction(target, target, "Artifact", 1));
                return false;
            }

            // 2. 直接强力取反
            logger.debug("【月之恶魔】正在反转 {}: {} -> {}", powerId, current, -current);
            p.amount = -current;
            p.updateDescription();
            p.flash();
            return true;
        }
        return false;
    }



    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 发动代价：扣除当前生命值的 1/8
        addToBot(new LoseHPAction(p, p, p.currentHealth / 8));

        ArrayList<AbstractCard> choices = new ArrayList<>();
        MoonChoiceSelf selfChoice = new MoonChoiceSelf();
        MoonChoiceEnemy enemyChoice = new MoonChoiceEnemy();
        MoonChoiceUncap uncapChoice = new MoonChoiceUncap();
        
        if (this.upgraded) {
            selfChoice.upgrade();
            enemyChoice.upgrade();
            uncapChoice.upgrade();
        }
        
        choices.add(selfChoice);
        choices.add(enemyChoice);
        choices.add(uncapChoice);
        
        addToBot(new ChooseOneAction(choices));
    }
}
