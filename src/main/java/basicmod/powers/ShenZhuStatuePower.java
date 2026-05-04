package basicmod.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import basicmod.BasicMod;
import basicmod.util.TextureLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShenZhuStatuePower extends AbstractPower {
    public static final String POWER_ID = BasicMod.makeID("ShenZhuStatuePower");
    // 我们暂时硬编码中文字符串或者使用预留的资源
    public static final String NAME = "圣主石像";
    public static final String[] DESCRIPTIONS = { "你是一尊石像。在你的回合开始时，获得 #b1 层 #y虚弱 。获得的 #y护甲 额外增加 #b4 点。每两回合开始时，获得 #b1 层 #y荆棘 。" };
    public static final Logger logger = LogManager.getLogger(ShenZhuStatuePower.class.getName());

    private int thornsGained = 2;

    public ShenZhuStatuePower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.DEBUFF; // 标记为一个负面/特殊机制状态

        // Load region from an existing texture, or fallback to artifact
        Texture normalTexture = TextureLoader.getTexture(BasicMod.powerPath("statue_state_32.png"));
        Texture hiDefImage = TextureLoader.getTexture(BasicMod.powerPath("statue_state_84.png"));
        if (normalTexture != null && hiDefImage != null) {
            this.region128 = new TextureAtlas.AtlasRegion(hiDefImage, 0, 0, hiDefImage.getWidth(),
                    hiDefImage.getHeight());
            this.region48 = new TextureAtlas.AtlasRegion(normalTexture, 0, 0, normalTexture.getWidth(),
                    normalTexture.getHeight());
        } else {
            this.loadRegion("malleable"); // 找不到图片时的兜底图
        }

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void onInitialApplication() {
        logger.info("【圣主石像】初次挂载能力。当前回合: " + com.megacrit.cardcrawl.actions.GameActionManager.turn);
        // 石像状态附加时，获得2层荆棘
        addToTop(new ApplyPowerAction(this.owner, this.owner, new ThornsPower(this.owner, 2), 2));
        // 特殊处理第一回合：如果是战斗开始时挂载此能力，atStartOfTurn 可能已经错过了，所以在这里手动补一层虚弱
        if (com.megacrit.cardcrawl.actions.GameActionManager.turn <= 1) {
            logger.info("【圣主石像】检测到正处于第一回合启动阶段，正在通过初始化补码赋予第 1 层虚弱。");
            addToTop(new ApplyPowerAction(this.owner, this.owner, new WeakPower(this.owner, 1, false), 1));
        }
    }

    @Override
    public void atStartOfTurnPostDraw() {
        int turn = com.megacrit.cardcrawl.actions.GameActionManager.turn;
        logger.info("【圣主石像】回合开始触发点（抽牌后）。当前回合: " + turn);
        // 从第二回合开始，走正常的每回合赋予逻辑
        if (turn > 1) {
            logger.info("【圣主石像】正在执行第 " + turn + " 回合的常规虚弱赋予。");
            this.flash();
            addToBot(new ApplyPowerAction(this.owner, this.owner, new WeakPower(this.owner, 1, false), 1));
        }

        // 每两回合成长一次荆棘
        if (turn % 2 == 0) {
            logger.info("【圣主石像】触发每两回合一次的荆棘成长。");
            addToBot(new ApplyPowerAction(this.owner, this.owner, new ThornsPower(this.owner, 1), 1));
            this.thornsGained += 1;
        }
    }

    @Override
    public void onRemove() {
        // 移除石像状态时，削减这期间获得的所有荆棘
        if (this.owner.hasPower(ThornsPower.POWER_ID)) {
            addToTop(new com.megacrit.cardcrawl.actions.common.ReducePowerAction(this.owner, this.owner, ThornsPower.POWER_ID, this.thornsGained));
        }
    }
}
