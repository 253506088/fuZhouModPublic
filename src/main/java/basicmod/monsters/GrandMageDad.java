package basicmod.monsters;

import basicmod.BasicMod;
import basicmod.cards.curses.CurseBlack;
import basicmod.cards.curses.CurseJackie;
import basicmod.cards.curses.CurseJade;
import basicmod.cards.curses.CurseTohru;
import basicmod.effects.RelicExhaustEffect;
import basicmod.helpers.GrandMageDadCycleCurseHelper;
import basicmod.helpers.GrandMageDadHelper;
import basicmod.powers.FlightAfuPower;
import basicmod.powers.GrandMageBlessingPower;
import basicmod.powers.GrandMageChantShieldPower;
import basicmod.powers.GrandMageCounterPouncePower;
import basicmod.powers.GrandMageCurseTaxSkillPower;
import basicmod.powers.GrandMageDamageCapPower;
import basicmod.relics.DogTalisman;
import basicmod.relics.DragonTalisman;
import basicmod.relics.HorseTalisman;
import basicmod.relics.MonkeyTalisman;
import basicmod.relics.OxTalisman;
import basicmod.relics.PigTalisman;
import basicmod.relics.RabbitTalisman;
import basicmod.relics.RatTalisman;
import basicmod.relics.RoosterTalisman;
import basicmod.relics.SheepTalisman;
import basicmod.relics.SnakeTalisman;
import basicmod.relics.TigerTalisman;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.helpers.RelicLibrary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * 大法师老爹怪物类。
 * Mod的最终Boss之一，拥有多阶段战斗、符咒封印、诅咒施加等复杂机制。
 */
public class GrandMageDad extends AbstractMonster {
    /** 怪物ID */
    public static final String ID = BasicMod.makeID("GrandMageDad");
    /** 怪物名称 */
    public static final String NAME = "大法师老爹";
    private static final String IMG = BasicMod.imagePath("monsters/grand_mage_dad/grand_mage_dad.png");
    private static final String IDLE_FRAME_DIR = "monsters/grand_mage_dad/idle";
    private static final int IDLE_SOURCE_FRAME_COUNT = 40;
    private static final float IDLE_FRAME_INTERVAL_SECONDS = 0.066F;
    private static final int IDLE_ATLAS_COLS = 8;
    private static final String LOG_PREFIX = "[大法师老爹]";
    private static final String INTENT_UI_ID = BasicMod.makeID("GrandMageDadIntentUI");
    private static final String SEAL_EFFECT_UI_ID = BasicMod.makeID("GrandMageDadSealEffectsUI");
    private static final String[] INTENT_UI_FALLBACK = new String[]{
            "雷符连打",
            "造成 #b%1$d x #b%2$d 伤害。",
            "镇邪法印",
            "造成 #b%1$d 伤害，并施加 #b2 层虚弱与 #b2 层易伤。",
            "符咒剥夺",
            "造成 #b%1$d 伤害，并从以下符咒中随机封存 #b2 个：%2$s。若无可封存符咒，则改为获得 #b2 力量与 #b1 人工制品。被封存的符咒会从遗物栏移除；当【正气不灭】本回合承伤打满上限时，返还 #b1 个已封存符咒。",
            "八卦连轰",
            "造成 #b%1$d x #b%2$d 伤害。",
            "妖魔鬼怪快离开·吟唱",
            "获得 #b%1$d 护盾并开始吟唱。若护盾未在下回合前被打破，则会在下回合发动处决。",
            "妖魔鬼怪快离开·处决",
            "若吟唱护盾仍在，则造成 #b999 伤害处决。",
            "闪腰硬直",
            "本回合不会攻击，并失去 #b50 格挡与 #b3 力量。",
            "副意图：",
            "训诫：向你的弃牌堆塞入 #b1 张【成龙】。",
            "护体：老爹获得格挡、 #b1 层人工制品，并回复最大生命值的 #b1/8 。",
            "杂念入侵：向你的弃牌堆随机塞入 #b1 张老爹诅咒。",
            "净化反噬：清除老爹身上的所有减益并获得 #b2 力量。",
            "咒阵扩散：向你的弃牌堆塞入 #b2 张不同的随机老爹诅咒。",
            "无",
            "本回合副意图因蛇符咒被隐藏，但仍会正常执行。",
            "无可封存符咒",
            "符咒剥夺：随机封存 #b2 个可封存符咒。"
    };

    private static final String[] SEAL_EFFECT_UI_FALLBACK = new String[]{
            "Unknown effect",
            "Gain +4 Strength",
            "Gain +2 Artifact",
            "Future attacks gain +1 hit",
            "Add 1 Burn after each attack",
            "Hide secondary intent display next turn",
            "Heal #b1/8 of missing HP at start of each Dad turn",
            "Apply skill tax to player (+1 cost on first Skill next turn)",
            "At the start of each Dad turn, randomly borrow 1 non-Curse, non-Status card from the player's draw pile or discard pile",
            "Gain Flight 3",
            "Per-hit damage cap becomes 45",
            "Attacks also apply 1 Weak and 1 Vulnerable",
            "Rat Talisman is sealed and cannot replace Curse or Status cards",
            "; Borrowed this turn: ",
            "; No card currently borrowed",
            "; If no card can be borrowed, heal #b30 HP",
            " (Currently heals #b",
            " HP)"
    };

    /** T1多段攻击 */
    private static final byte MOVE_T1_MULTI = 1;
    /** T2打击+减益 */
    private static final byte MOVE_T2_STRIKE_DEBUFF = 2;
    /** T3封印打击 */
    private static final byte MOVE_T3_SEAL_STRIKE = 3;
    /** T4连击 */
    private static final byte MOVE_T4_BARRAGE = 4;
    /** T5吟唱 */
    private static final byte MOVE_T5_CHANT = 5;
    /** T6处决 */
    private static final byte MOVE_T6_EXECUTE = 6;
    /** T6眩晕（护盾被打破） */
    private static final byte MOVE_T6_STUN = 7;
    /** 猴符咒无牌可借时的回血量 */
    private static final int MONKEY_NO_CARD_HEAL = 30;

    /** 副意图：无 */
    public static final int SEC_NONE = 0;
    /** 副意图：训诫（塞入成龙诅咒） */
    public static final int SEC_CURSE_JACKIE = 1;
    /** 副意图：护体（获得格挡和人工制品） */
    public static final int SEC_DEFEND_ARTIFACT = 2;
    /** 副意图：杂念入侵（随机塞入诅咒） */
    public static final int SEC_RANDOM_CURSE = 3;
    /** 副意图：净化反噬（清除减益并获得力量） */
    public static final int SEC_CLEANSE_BUFF = 4;
    /** 副意图：咒阵扩散（塞入多张诅咒） */
    public static final int SEC_DOUBLE_CURSE = 5;
    /** 副意图：符咒剥夺（封印符咒） */
    public static final int SEC_SEAL_TALISMAN = 6;

    /** 每回合获得的格挡值 */
    private final int blockPerTurn;
    /** 吟唱护盾值 */
    private final int chantShield;
    /** 吟唱获得的人工制品层数 */
    private final int chantArtifact;
    /** T1伤害 */
    private final int t1Damage;
    /** T2伤害 */
    private final int t2Damage;
    /** T3伤害 */
    private final int t3Damage;
    /** T4伤害 */
    private final int t4Damage;
    /** T4基础攻击次数 */
    private final int t4BaseHitCount;
    /** 动态生命值加成 */
    private final int dynamicHpBonus;
    /** 动态生命值加成报告 */
    private final GrandMageDadHelper.DynamicHpBonusReport dynamicHpBonusReport;
    /** 每回合承伤上限 */
    private final int capPerTurn;

    /** 当前回合索引 */
    private int turnIndex = 1;
    /** 副意图代码 */
    private int secondaryIntentCode = SEC_NONE;
    /** 兔符咒额外攻击次数 */
    private int rabbitExtraHits = 0;
    /** 单次伤害上限 */
    private int singleHitCap = -1;
    /** 是否正在吟唱护盾 */
    private boolean shieldChanting = false;
    /** 坚壁是否由自身施加 */
    private boolean chantBarricadeAppliedBySelf = false;
    /** 龙符咒是否触发灼烧 */
    private boolean dragonBurn = false;
    /** 马符咒是否触发回复 */
    private boolean horseRegen = false;
    /** 猴符咒是否触发混乱 */
    private boolean monkeyChaos = false;
    /** 猴符咒借来的卡牌 */
    private AbstractCard monkeyBorrowedCard = null;
    /** 猴符咒借牌来源 */
    private String monkeyBorrowedFrom = null;
    /** 猴符咒上次借的卡牌UUID */
    private UUID monkeyLastBorrowedCardUuid = null;
    /** 猪符咒是否触发减益 */
    private boolean pigDebuff = false;
    /** 本回合是否隐藏副意图渲染 */
    private boolean hideSecondaryForRenderThisTurn = false;
    /** 下回合是否隐藏副意图 */
    private boolean hideSecondaryNextTurn = false;
    /** 被封印的符咒映射表 */
    private final LinkedHashMap<String, AbstractRelic> sealedTalismans = new LinkedHashMap<>();
    /** 待机动画图集纹理 */
    private Texture idleAtlas;
    /** 待机动画帧宽度 */
    private int idleFrameW;
    /** 待机动画帧高度 */
    private int idleFrameH;
    /** 待机动画帧数 */
    private int idleFrameCount;
    /** 待机动画开始时间（纳秒） */
    private long idleAnimStartNano;

    private static class MonkeyBorrowCandidate {
        private final AbstractCard card;
        private final CardGroup group;
        private final String sourceName;

        private MonkeyBorrowCandidate(AbstractCard card, CardGroup group, String sourceName) {
            this.card = card;
            this.group = group;
            this.sourceName = sourceName;
        }
    }

    public GrandMageDad() {
        super(NAME, ID, 1, 0.0F, 0.0F, 420.0F, 420.0F, IMG, 0.0F, 0.0F);
        initializeIdleAnimation();
        boolean a20 = AbstractDungeon.ascensionLevel >= 20;
        int baseHp = a20 ? 580 : 500;
        int downgradedNothingLackingCount = GrandMageDadHelper.downgradeNothingLackingForGrandMageDad();
        if (downgradedNothingLackingCount > 0) {
            log("大法师老爹登场压制【我什么都不缺了】：已降级 " + downgradedNothingLackingCount + " 张升级版。");
        }
        this.dynamicHpBonusReport = GrandMageDadHelper.buildDynamicHpBonusReport();
        this.dynamicHpBonus = this.dynamicHpBonusReport.totalBonus;
        int finalHp = baseHp + dynamicHpBonus;
        this.setHp(finalHp, finalHp);
        ensureHealthBarVisible();

        this.type = EnemyType.BOSS;

        this.blockPerTurn = a20 ? 15 : 12;
        this.chantShield = a20 ? 240 : 200;
        this.chantArtifact = a20 ? 8 : 6;
        this.t1Damage = a20 ? 11 : 9;
        this.t2Damage = a20 ? 45 : 35;
        this.t3Damage = a20 ? 24 : 20;
        this.t4Damage = 10;
        this.t4BaseHitCount = a20 ? 7 : 5;
        this.capPerTurn = a20 ? 200 : 250;

        this.damage.add(new DamageInfo(this, t1Damage));
        this.damage.add(new DamageInfo(this, t2Damage));
        this.damage.add(new DamageInfo(this, t3Damage));
        this.damage.add(new DamageInfo(this, t4Damage));
        this.damage.add(new DamageInfo(this, 999));

        log("创建Boss：基础生命=" + baseHp
                + "，动态生命加成=" + dynamicHpBonus
                + "，最终生命=" + finalHp
                + "，每回合格挡=" + blockPerTurn
                + "，吟唱护盾=" + chantShield
                + "，每回合承伤上限=" + capPerTurn + "。");
        initializeOpeningIntent();
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!this.isDead && !this.escaped && this.idleAtlas != null && this.idleFrameCount > 0) {
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            int frameIdx = computeIdleFrame();
            int col = frameIdx % IDLE_ATLAS_COLS;
            int row = frameIdx / IDLE_ATLAS_COLS;
            float w = idleFrameW * Settings.scale;
            float h = idleFrameH * Settings.scale;
            float x = this.drawX - w / 2.0F + this.animX;
            float y = this.drawY + this.animY + AbstractDungeon.sceneOffsetY;
            sb.setColor(this.tint.color);
            sb.draw(idleAtlas, x, y,
                    w / 2.0F, 0.0F,
                    w, h,
                    1.0F, 1.0F, 0.0F,
                    col * idleFrameW, row * idleFrameH, idleFrameW, idleFrameH,
                    this.flipHorizontal, this.flipVertical);
            Texture savedImg = this.img;
            this.img = null;
            super.render(sb);
            this.img = savedImg;
        } else {
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            super.render(sb);
        }
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void initializeIdleAnimation() {
        Pixmap[] pixmaps = new Pixmap[IDLE_SOURCE_FRAME_COUNT];
        int loaded = 0;
        int fw = 0, fh = 0;

        for (int i = 0; i < IDLE_SOURCE_FRAME_COUNT; i++) {
            String path = BasicMod.imagePath(IDLE_FRAME_DIR + "/frame_" + String.format(Locale.ROOT, "%05d", i + 1) + ".png");
            try {
                pixmaps[i] = new Pixmap(Gdx.files.internal(path));
                if (fw == 0) {
                    fw = pixmaps[i].getWidth();
                    fh = pixmaps[i].getHeight();
                }
                loaded++;
            } catch (Exception e) {
                log("Idle帧加载失败: " + path);
            }
        }

        if (loaded == 0 || fw == 0) {
            log("Idle帧初始化失败：未加载到任何可用帧，将继续使用静态贴图。");
            for (Pixmap p : pixmaps) {
                if (p != null) p.dispose();
            }
            return;
        }

        int cols = IDLE_ATLAS_COLS;
        int rows = (loaded + cols - 1) / cols;
        Pixmap atlas = new Pixmap(cols * fw, rows * fh, Pixmap.Format.RGBA8888);

        int idx = 0;
        for (int i = 0; i < IDLE_SOURCE_FRAME_COUNT; i++) {
            if (pixmaps[i] == null) continue;
            atlas.drawPixmap(pixmaps[i], (idx % cols) * fw, (idx / cols) * fh);
            pixmaps[i].dispose();
            idx++;
        }

        this.idleAtlas = new Texture(atlas);
        this.idleAtlas.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        atlas.dispose();

        this.idleFrameW = fw;
        this.idleFrameH = fh;
        this.idleFrameCount = loaded;
        this.idleAnimStartNano = System.nanoTime();

        if (loaded < IDLE_SOURCE_FRAME_COUNT) {
            log("Idle帧加载不完整：已加载 " + loaded + "/" + IDLE_SOURCE_FRAME_COUNT + "，缺失帧已自动忽略。");
        } else {
            log("Idle图集构建完成：" + cols + "x" + rows + " (" + (cols * fw) + "x" + (rows * fh) + ")，共 " + loaded + " 帧。");
        }
    }

    private int computeIdleFrame() {
        long elapsed = System.nanoTime() - idleAnimStartNano;
        float totalSec = elapsed / 1_000_000_000.0F;
        float cycle = idleFrameCount * IDLE_FRAME_INTERVAL_SECONDS;
        int frame = (int) ((totalSec % cycle) / IDLE_FRAME_INTERVAL_SECONDS);
        return Math.max(0, Math.min(frame, idleFrameCount - 1));
    }

    public static void logDetail(String message) {
        BasicMod.logger.info(LOG_PREFIX + " " + message);
    }

    @Override
    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_ENDING");
        ensureHealthBarVisible();
        int downgradedNothingLackingCount = GrandMageDadHelper.downgradeNothingLackingForGrandMageDad();
        if (downgradedNothingLackingCount > 0) {
            log("战前再次检查【我什么都不缺了】：已降级 " + downgradedNothingLackingCount + " 张升级版战斗拷贝。");
        }
        initializeOpeningIntent();
        createIntent();

        boolean a20 = AbstractDungeon.ascensionLevel >= 20;
        GrandMageDadHelper.DynamicHpBonusReport report = this.dynamicHpBonusReport;
        int baseHp = a20 ? 580 : 500;
        int finalHp = baseHp + dynamicHpBonus;
        int pounceStrength = dynamicHpBonus / 30;
        int pounceStrengthRemainder = dynamicHpBonus % 30;

        log("战前动作开始。当前状态：" + snapshotState());
        log("【动态强化-来源】玩家当前携带符咒(" + report.talismanCount + "个)：" + report.talismanText() + "。");
        log("【动态强化-来源】玩家潘库宝盒已获得恶魔魔气(" + report.panKuDemonCount + "个)：" + report.panKuDemonText() + "。");
        log("【动态强化-来源】玩家主卡组面具牌(" + report.maskCount + "张)：" + report.maskText() + "。");
        log("【动态强化-来源】玩家主卡组面具辅助牌(" + report.maskSupportCount + "张)：" + report.maskSupportText() + "。");
        log("【动态强化-来源】玩家主卡组动物符咒牌(" + report.talismanCardCount + "张)：" + report.talismanCardText() + "。");
        log("【动态强化-来源】玩家主卡组普通牌(" + report.ordinaryCardCount + "张)：" + report.ordinaryCardText() + "。");
        log("【动态强化-来源】玩家主卡组终局牌(" + report.ultimateCardCount + "张)：" + report.ultimateCardText() + "。");
        log("【动态强化-生命公式】额外生命="
                + "符咒(" + report.talismanCount + ")*30="
                + report.talismanRawBonus + " + 潘库魔气(" + report.panKuDemonCount + ")*30="
                + report.panKuDemonRawBonus + " + 面具(" + report.maskCount + ")*15="
                + report.maskRawBonus + " + 面具辅助(" + report.maskSupportCount + ")*15="
                + report.maskSupportRawBonus + " + 动物符咒牌(" + report.talismanCardCount + ")*20="
                + report.talismanCardRawBonus + " + 普通牌(" + report.ordinaryCardCount + ")*5="
                + report.ordinaryCardRawBonus + " + 终局牌="
                + report.ultimateCardRawBonus + " = " + dynamicHpBonus + "。");
        log("【动态强化-生命结果】老爹生命最终值=" + baseHp + "+" + dynamicHpBonus + "=" + finalHp + "（基础生命=" + baseHp + "）。");
        log("【动态强化-力量公式】正气反扑力量成长=floor(额外生命/30)=floor(" + dynamicHpBonus + "/30)="
                + pounceStrength + "，余数=" + pounceStrengthRemainder + "。");
        log("【动态强化-力量结果】最终本场施加力量+" + pounceStrength + "。");
        queueWithLog("施加【正气不灭】：本回合总承伤上限=" + capPerTurn + "。",
                new ApplyPowerAction(this, this, new GrandMageDamageCapPower(this, capPerTurn), capPerTurn));
        queueWithLog("【正气反扑-生效排队】第1步：施加展示型Buff【正气反扑】，amount=" + pounceStrength + "。",
                new ApplyPowerAction(this, this, new GrandMageCounterPouncePower(this, pounceStrength), pounceStrength));
        queueWithLog("施加【大法师的庇佑】：本回合可免疫前两次异常状态，魔气异常单次伤害上限锁定为" + GrandMageBlessingPower.DEMON_QI_SINGLE_DAMAGE_CAP + "，天之恶魔的恐惧改判为额外8点伤害。",
                new ApplyPowerAction(this, this, new GrandMageBlessingPower(this), 1));
        queueWithLog("【正气反扑-生效排队】第2步：施加同值力量 StrengthPower，力量+" + pounceStrength + "。",
                new ApplyPowerAction(this, this, new StrengthPower(this, pounceStrength), pounceStrength));
        log("战前动作排队完成，首回合主意图=" + describePrimaryMove(nextMove)
                + "，副意图=" + describeSecondaryIntent(secondaryIntentCode) + "。");
    }

    @Override
    public void takeTurn() {
        ensureHealthBarVisible();
        log("开始执行回合：主意图=" + describePrimaryMove(nextMove)
                + "，副意图=" + describeSecondaryIntent(secondaryIntentCode)
                + "，副意图显示状态=" + (hideSecondaryForRenderThisTurn ? "隐藏" : "显示")
                + "。当前状态：" + snapshotState());

        queueWithLog("回合开始获得格挡=" + blockPerTurn + "。", new GainBlockAction(this, this, blockPerTurn));
        if (horseRegen) {
            handleHorseSealAtTurnStart();
        }
        if (monkeyChaos) {
            handleMonkeySealAtTurnStart();
        }

        if (hideSecondaryForRenderThisTurn && secondaryIntentCode != SEC_NONE) {
            log("蛇符咒封存效果生效：本回合副意图对玩家隐藏，但会正常执行，实际副意图为【"
                    + describeSecondaryIntent(secondaryIntentCode) + "】。");
        }

        executePersistentCleanseIfNeeded();

        switch (nextMove) {
            case MOVE_T1_MULTI:
                log("执行主意图【雷符连打】：基础伤害=" + t1Damage + "，段数=" + getT1HitCount() + "。");
                attackPlayer(t1Damage, getT1HitCount(), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL);
                break;
            case MOVE_T2_STRIKE_DEBUFF:
                log("执行主意图【镇邪法印】：基础伤害=" + t2Damage + "，并施加虚弱2、易伤2。");
                attackPlayer(t2Damage, getT2HitCount(), AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                applyWeakVulnToPlayer(2, 2);
                break;
            case MOVE_T3_SEAL_STRIKE:
                log("执行主意图【符咒剥夺+追击】：先尝试封存符咒，再造成伤害=" + t3Damage + "。");
                attemptSealTalisman();
                attackPlayer(t3Damage, getT3HitCount(), AbstractGameAction.AttackEffect.SLASH_HEAVY);
                break;
            case MOVE_T4_BARRAGE:
                log("执行主意图【八卦连轰】：基础伤害=" + t4Damage + "，段数=" + getT4HitCount() + "。");
                attackPlayer(t4Damage, getT4HitCount(), AbstractGameAction.AttackEffect.FIRE);
                break;
            case MOVE_T5_CHANT:
                log("执行主意图【终极封印·吟唱】：开始吟唱并获得护盾=" + chantShield + "。");
                startShieldChanting();
                queueWithLog("【终极封印·吟唱】获得人工制品=" + chantArtifact + "。", new ApplyPowerAction(this, this, new ArtifactPower(this, chantArtifact), chantArtifact));
                queueWithLog("终极封印吟唱：获得格挡/护盾=" + chantShield + "。", new GainBlockAction(this, this, chantShield));
                break;
            case MOVE_T6_EXECUTE:
                if (this.currentBlock > 0) {
                    log("执行主意图【终极封印·处决】：吟唱护盾未破，发动直接斩杀。");
                    log("【终极封印·处决】本次将直接斩杀玩家，不再按999伤害结算。");
                    executeUltimateSealKill();
                    stopShieldChanting();
                } else {
                    log("原计划执行【终极封印·处决】，但发现护盾已破，直接回到T2。");
                    cancelChantAndReturnToT2("处决前发现吟唱护盾已破");
                }
                break;
            case MOVE_T6_STUN:
                log("执行主意图【闪腰硬直】：本回合眩晕并自降强度。");
                executeShieldBrokenStun();
                break;
            default:
                log("遇到未知主意图编码=" + nextMove + "，本回合不执行主动作。");
                break;
        }

        executeSecondaryIntent();
        advanceTurnIndex();
        log("本回合主副意图执行完毕，排队掷出下一回合意图。");
        queue(new RollMoveAction(this));
    }

    private void executeSecondaryIntent() {
        switch (secondaryIntentCode) {
            case SEC_CURSE_JACKIE:
                log("执行副意图【训诫】：向玩家弃牌堆塞入【成龙】诅咒1张。");
                queue(new MakeTempCardInDiscardAction(new CurseJackie(), 1));
                break;
            case SEC_DEFEND_ARTIFACT:
                int defendBlock = AbstractDungeon.ascensionLevel >= 20 ? 16 : 12;
                int defendHeal = Math.max(1, this.maxHealth / 8);
                log("执行副意图【护体】：获得格挡=" + defendBlock + "，人工制品=1。");
                queue(new GainBlockAction(this, this, defendBlock));
                queue(new ApplyPowerAction(this, this, new ArtifactPower(this, 1), 1));
                queue(new HealAction(this, this, defendHeal));
                break;
            case SEC_RANDOM_CURSE:
                AbstractCard randomCurse = randomCurseCard("副意图【杂念入侵】随机选择诅咒");
                log("执行副意图【杂念入侵】：向玩家弃牌堆塞入诅咒【" + describeCard(randomCurse) + "】1张。");
                queue(new MakeTempCardInDiscardAction(randomCurse, 1));
                break;
            case SEC_CLEANSE_BUFF:
                log("执行副意图【净化反噬】：清除自身全部Debuff并获得力量+2。");
                clearDebuffs();
                queue(new ApplyPowerAction(this, this, new StrengthPower(this, 2), 2));
                break;
            case SEC_DOUBLE_CURSE:
                ArrayList<AbstractCard> pool = buildCursePool();
                Collections.shuffle(pool, new java.util.Random(AbstractDungeon.miscRng.randomLong()));
                AbstractCard firstCurse = pool.get(0);
                AbstractCard secondCurse = pool.get(1);
                log("执行副意图【咒阵扩散】：本次随机到诅咒【" + describeCard(firstCurse) + "】和【"
                        + describeCard(secondCurse) + "】。");
                queue(new MakeTempCardInDiscardAction(firstCurse, 1));
                queue(new MakeTempCardInDiscardAction(secondCurse, 1));
                break;
            case SEC_SEAL_TALISMAN:
                log("执行副意图【符咒剥夺】：尝试封存符咒。");
                attemptSealTalisman();
                break;
            default:
                log("本回合没有副意图需要执行。");
                break;
        }
    }

    private void executePersistentCleanseIfNeeded() {
        if (!shouldPersistentCleanse()) {
            return;
        }
        log("常驻【净化反噬】：清除自身全部Debuff并获得力量+2。");
        clearDebuffs();
        queue(new ApplyPowerAction(this, this, new StrengthPower(this, 2), 2));
    }

    private boolean shouldPersistentCleanse() {
        return nextMove == MOVE_T1_MULTI
                || nextMove == MOVE_T2_STRIKE_DEBUFF
                || nextMove == MOVE_T3_SEAL_STRIKE
                || nextMove == MOVE_T4_BARRAGE
                || nextMove == MOVE_T5_CHANT;
    }

    private ArrayList<AbstractCard> buildCursePool() {
        ArrayList<AbstractCard> pool = new ArrayList<>();
        pool.add(new CurseTohru());
        pool.add(new CurseJackie());
        pool.add(new CurseJade());
        pool.add(new CurseBlack());
        return pool;
    }

    private AbstractCard randomCurseCard(String reason) {
        int roll = AbstractDungeon.cardRandomRng.random(3);
        AbstractCard card;
        switch (roll) {
            case 0:
                card = new CurseTohru();
                break;
            case 1:
                card = new CurseJackie();
                break;
            case 2:
                card = new CurseJade();
                break;
            default:
                card = new CurseBlack();
                break;
        }
        log(reason + "：随机结果=" + roll + "，诅咒=" + describeCard(card) + "。");
        return card;
    }

    private void clearDebuffs() {
        ArrayList<AbstractPower> copy = new ArrayList<>(this.powers);
        boolean removedAny = false;
        for (AbstractPower power : copy) {
            if (power != null && power.type == AbstractPower.PowerType.DEBUFF) {
                removedAny = true;
                log("【净化反噬】准备移除Debuff：" + describePower(power) + "。");
                queue(new RemoveSpecificPowerAction(this, this, power.ID));
            }
        }
        if (!removedAny) {
            log("【净化反噬】未发现可移除的Debuff。");
        }
    }

    private void attemptSealTalisman() {
        ArrayList<String> candidates = GrandMageDadHelper.getSealableTalismans(sealedTalismans.keySet());
        log("开始尝试封存符咒。已封存=" + describeTalismans(sealedTalismans.keySet())
                + "，本次可选目标=" + describeTalismans(candidates) + "。");
        if (candidates.isEmpty()) {
            log("没有可封存的符咒，触发兜底强化：力量+2，人工制品+1。");
            queue(new ApplyPowerAction(this, this, new StrengthPower(this, 2), 2));
            queue(new ApplyPowerAction(this, this, new ArtifactPower(this, 1), 1));
            refreshDynamicDescriptions();
            return;
        }

        int targetSealCount = Math.min(2, candidates.size());
        int sealedCount = 0;
        for (int i = 0; i < targetSealCount && !candidates.isEmpty(); i++) {
            int roll = AbstractDungeon.miscRng.random(candidates.size() - 1);
            String talismanId = candidates.remove(roll);
            AbstractRelic sealedRelic = sealPlayerTalisman(talismanId);
            if (sealedRelic == null) {
                log("封存符咒失败：未能从玩家遗物栏中取出 " + talismanId + "，本次跳过该目标。");
                continue;
            }

            sealedTalismans.put(talismanId, sealedRelic);
            sealedCount++;
            log("本次成功封存符咒：" + describeTalismanWithSealEffect(talismanId) + "。");

            if (OxTalisman.ID.equals(talismanId)) {
                log("封存效果【牛】：获得力量+4。");
                queue(new ApplyPowerAction(this, this, new StrengthPower(this, 4), 4));
            } else if (TigerTalisman.ID.equals(talismanId)) {
                log("封存效果【虎】：获得人工制品+2。");
                queue(new ApplyPowerAction(this, this, new ArtifactPower(this, 2), 2));
            } else if (RabbitTalisman.ID.equals(talismanId)) {
                rabbitExtraHits += 1;
                log("【兔符咒封存】后续所有攻击意图额外段数+1，当前额外段数=" + rabbitExtraHits + "。");
                log("封存效果【兔】：后续多段攻击段数+1，当前额外段数=" + rabbitExtraHits + "。");
            } else if (DragonTalisman.ID.equals(talismanId)) {
                dragonBurn = true;
                log("封存效果【龙】：后续每次攻击额外塞入1张【燃烧】。");
            } else if (SnakeTalisman.ID.equals(talismanId)) {
                hideSecondaryNextTurn = true;
                log("封存效果【蛇】：下回合副意图仅隐藏显示，不会取消执行。");
            } else if (HorseTalisman.ID.equals(talismanId)) {
                horseRegen = true;
                log("封存效果【马】：后续每个老爹回合开始回复20生命。");
            } else if (SheepTalisman.ID.equals(talismanId)) {
                if (AbstractDungeon.player != null) {
                    log("封存效果【羊】：给玩家施加下回合首张技能牌费用+1。");
                    queue(new ApplyPowerAction(AbstractDungeon.player, this,
                            new GrandMageCurseTaxSkillPower(AbstractDungeon.player, 1), 1));
                } else {
                    log("封存效果【羊】本应施加技能税，但当前玩家对象为空。");
                }
            } else if (MonkeyTalisman.ID.equals(talismanId)) {
                monkeyChaos = true;
                log("封存效果【猴】：后续每个老爹回合开始会从玩家抽牌堆或弃牌堆随机借走1张卡；若无牌可借，则回复30点生命。");
            } else if (RoosterTalisman.ID.equals(talismanId)) {
                log("封存效果【鸡】：获得飞行3层。");
                queue(new ApplyPowerAction(this, this, new FlightAfuPower(this, 3), 3));
            } else if (DogTalisman.ID.equals(talismanId)) {
                singleHitCap = 45;
                log("封存效果【狗】：单次受伤上限改为45。");
                refreshDamageCapPower();
            } else if (PigTalisman.ID.equals(talismanId)) {
                pigDebuff = true;
                log("封存效果【猪】：后续攻击额外附带虚弱1、易伤1。");
            } else {
                log("封存了未识别的符咒ID=" + talismanId + "，本次未附加额外效果。");
            }
        }

        if (sealedCount <= 0) {
            log("本次未成功封存任何符咒，触发兜底强化：力量+2，人工制品+1。");
            queue(new ApplyPowerAction(this, this, new StrengthPower(this, 2), 2));
            queue(new ApplyPowerAction(this, this, new ArtifactPower(this, 1), 1));
        }
        refreshDynamicDescriptions();
    }

    private AbstractRelic sealPlayerTalisman(String talismanId) {
        if (AbstractDungeon.player == null) {
            log("尝试封存符咒时玩家对象为空。");
            return null;
        }
        AbstractRelic relic = AbstractDungeon.player.getRelic(talismanId);
        if (relic == null) {
            log("尝试封存符咒失败：玩家当前没有遗物 " + talismanId + "。");
            return null;
        }
        log("开始执行真实封存：从玩家遗物栏移除【" + relic.name + "】并播放封存动画。");
        AbstractDungeon.topLevelEffects.add(new RelicExhaustEffect(relic));
        relic.onUnequip();
        AbstractDungeon.player.relics.remove(relic);
        AbstractDungeon.player.reorganizeRelics();
        return relic;
    }

    public void onDamageCapFilled() {
        if (sealedTalismans.isEmpty()) {
            log("【正气不灭】本回合承伤已打满，但当前没有已封存的符咒可返还。");
            refreshDynamicDescriptions();
            return;
        }

        String talismanId = sealedTalismans.keySet().iterator().next();
        returnSealedTalisman(talismanId, "【正气不灭】本回合承伤打满上限，返还1个已封存符咒");
    }

    private void returnAllSealedTalismans(String reason) {
        ArrayList<String> toReturn = new ArrayList<>(sealedTalismans.keySet());
        for (String talismanId : toReturn) {
            returnSealedTalisman(talismanId, reason);
        }
    }

    private void returnSealedTalisman(String talismanId, String reason) {
        AbstractRelic relic = sealedTalismans.remove(talismanId);
        if (relic == null) {
            log(reason + "，但未找到对应的已封存符咒记录：" + talismanId + "。");
            refreshDynamicDescriptions();
            return;
        }

        log(reason + "：返还【" + relic.name + "】。");
        revertSealBonus(talismanId);
        restorePlayerTalisman(relic);
        refreshDynamicDescriptions();
    }

    private void restorePlayerTalisman(AbstractRelic relic) {
        if (AbstractDungeon.player == null) {
            log("返还符咒失败：玩家对象为空。");
            return;
        }
        relic.instantObtain(AbstractDungeon.player, AbstractDungeon.player.relics.size(), true);
        queue(new RelicAboveCreatureAction(AbstractDungeon.player, relic));
        log("已将符咒返还给玩家遗物栏：【" + relic.name + "】。");
    }

    private void revertSealBonus(String talismanId) {
        if (OxTalisman.ID.equals(talismanId)) {
            if (this.hasPower(StrengthPower.POWER_ID)) {
                log("返还【牛】：移除老爹因封存获得的力量+4。");
                queue(new ReducePowerAction(this, this, StrengthPower.POWER_ID, 4));
            }
        } else if (TigerTalisman.ID.equals(talismanId)) {
            if (this.hasPower(ArtifactPower.POWER_ID)) {
                log("返还【虎】：移除老爹因封存获得的人工制品+2。");
                queue(new ReducePowerAction(this, this, ArtifactPower.POWER_ID, 2));
            }
        } else if (RabbitTalisman.ID.equals(talismanId)) {
            rabbitExtraHits = Math.max(0, rabbitExtraHits - 1);
            log("【兔符咒返还】后续所有攻击意图额外段数-1，当前额外段数=" + rabbitExtraHits + "。");
            log("返还【兔】：后续多段攻击额外段数-1，当前额外段数=" + rabbitExtraHits + "。");
        } else if (DragonTalisman.ID.equals(talismanId)) {
            dragonBurn = sealedTalismans.containsKey(DragonTalisman.ID);
            log("返还【龙】：取消额外塞入【燃烧】效果。");
        } else if (SnakeTalisman.ID.equals(talismanId)) {
            hideSecondaryNextTurn = false;
            log("返还【蛇】：取消下回合副意图隐藏效果。");
        } else if (HorseTalisman.ID.equals(talismanId)) {
            horseRegen = sealedTalismans.containsKey(HorseTalisman.ID);
            log("返还【马】：取消后续回合开始回复生命效果。");
        } else if (SheepTalisman.ID.equals(talismanId)) {
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasPower(GrandMageCurseTaxSkillPower.POWER_ID)) {
                log("返还【羊】：移除玩家身上的技能税。");
                queue(new RemoveSpecificPowerAction(AbstractDungeon.player, this, GrandMageCurseTaxSkillPower.POWER_ID));
            } else {
                log("返还【羊】：当前玩家身上没有技能税，直接跳过移除。");
            }
        } else if (MonkeyTalisman.ID.equals(talismanId)) {
            monkeyChaos = sealedTalismans.containsKey(MonkeyTalisman.ID);
            returnMonkeyBorrowedCardToDiscard("返还【猴】时，立即归还当前借走的卡牌", true);
            log("返还【猴】：取消后续借牌效果。");
        } else if (RoosterTalisman.ID.equals(talismanId)) {
            if (this.hasPower(FlightAfuPower.POWER_ID)) {
                log("返还【鸡】：移除飞行效果。");
                queue(new RemoveSpecificPowerAction(this, this, FlightAfuPower.POWER_ID));
            }
        } else if (DogTalisman.ID.equals(talismanId)) {
            singleHitCap = sealedTalismans.containsKey(DogTalisman.ID) ? 45 : -1;
            log("返还【狗】：恢复默认单次受伤上限。");
            refreshDamageCapPower();
        } else if (PigTalisman.ID.equals(talismanId)) {
            pigDebuff = sealedTalismans.containsKey(PigTalisman.ID);
            log("返还【猪】：取消攻击额外附带虚弱与易伤效果。");
        }
    }

    private void refreshDamageCapPower() {
        GrandMageDamageCapPower capPower = getDamageCapPower();
        if (capPower == null) {
            log("尝试同步【正气不灭】状态描述失败：尚未找到对应Power。");
            return;
        }
        capPower.setPerHitCap(singleHitCap);
        capPower.updateDescription();
        log("已同步【正气不灭】的单次受伤上限为" + (singleHitCap > 0 ? singleHitCap : "默认无限制") + "。");
    }

    private GrandMageDamageCapPower getDamageCapPower() {
        if (this.hasPower(GrandMageDamageCapPower.POWER_ID)
                && this.getPower(GrandMageDamageCapPower.POWER_ID) instanceof GrandMageDamageCapPower) {
            return (GrandMageDamageCapPower) this.getPower(GrandMageDamageCapPower.POWER_ID);
        }
        return null;
    }

    private void refreshDynamicDescriptions() {
        GrandMageDamageCapPower capPower = getDamageCapPower();
        if (capPower != null) {
            capPower.updateDescription();
        }
    }

    public static void onPlayerEndTurnBorrowCleanup() {
        GrandMageDad dad = findActiveGrandMageDad();
        if (dad != null) {
            dad.returnMonkeyBorrowedCardToDiscard("玩家回合结束，归还申猴本回合借走的卡牌", true);
        }
    }

    private static GrandMageDad findActiveGrandMageDad() {
        AbstractRoom room = AbstractDungeon.getCurrRoom();
        if (room == null || room.monsters == null || room.monsters.monsters == null) {
            return null;
        }
        for (AbstractMonster monster : room.monsters.monsters) {
            if (monster instanceof GrandMageDad && !monster.isDeadOrEscaped()) {
                return (GrandMageDad) monster;
            }
        }
        return null;
    }

    private int rollRandomSecondaryIntent(String reason) {
        int code = randomSecondaryIntent();
        log(reason + "：结果为【" + describeSecondaryIntent(code) + "】。");
        return code;
    }

    private int randomSecondaryIntent() {
        int roll = AbstractDungeon.aiRng.random(3);
        switch (roll) {
            case 0:
                return SEC_DEFEND_ARTIFACT;
            case 1:
                return SEC_RANDOM_CURSE;
            case 2:
                return SEC_SEAL_TALISMAN;
            default:
                return SEC_CURSE_JACKIE;
        }
    }

    private void applyWeakVulnToPlayer(int weak, int vuln) {
        if (AbstractDungeon.player == null) {
            log("准备施加虚弱/易伤时，玩家对象为空，跳过。");
            return;
        }
        log("向玩家施加异常：虚弱=" + weak + "，易伤=" + vuln + "。");
        queue(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, weak, true), weak));
        queue(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, vuln, true), vuln));
    }

    private void attackPlayer(int baseDamage, int hits, AbstractGameAction.AttackEffect effect) {
        if (AbstractDungeon.player == null) {
            log("准备攻击玩家时，玩家对象为空，跳过。");
            return;
        }
        for (int i = 0; i < hits; i++) {
            DamageInfo info = new DamageInfo(this, baseDamage, DamageInfo.DamageType.NORMAL);
            info.applyPowers(this, AbstractDungeon.player);
            log("排队第" + (i + 1) + "/" + hits + "段攻击：基础伤害=" + baseDamage
                    + "，结算后伤害=" + info.output
                    + "，特效=" + effect + "。");
            queue(new DamageAction(AbstractDungeon.player, info, effect));
        }

        if (dragonBurn) {
            log("龙符咒封存效果触发：本次攻击后额外塞入【燃烧】1张。");
            queue(new MakeTempCardInDiscardAction(new Burn(), 1));
        }
        if (pigDebuff) {
            log("猪符咒封存效果触发：本次攻击额外附带虚弱1、易伤1。");
            applyWeakVulnToPlayer(1, 1);
        }
    }

    private void advanceTurnIndex() {
        int oldTurnIndex = turnIndex;
        if (nextMove == MOVE_T1_MULTI) {
            turnIndex = 2;
        } else if (nextMove == MOVE_T2_STRIKE_DEBUFF) {
            turnIndex = 3;
        } else if (nextMove == MOVE_T3_SEAL_STRIKE) {
            turnIndex = 4;
        } else if (nextMove == MOVE_T4_BARRAGE) {
            turnIndex = 5;
        } else if (nextMove == MOVE_T5_CHANT) {
            turnIndex = 6;
        } else if (nextMove == MOVE_T6_EXECUTE) {
            turnIndex = 2;
        } else if (nextMove == MOVE_T6_STUN) {
            turnIndex = 2;
        }
        log("回合序号推进：从T" + oldTurnIndex + " -> T" + turnIndex + "，依据主意图="
                + describePrimaryMove(nextMove) + "。");
    }

    @Override
    protected void getMove(int i) {
        ensureHealthBarVisible();
        hideSecondaryForRenderThisTurn = hideSecondaryNextTurn;
        hideSecondaryNextTurn = false;

        log("开始规划下一回合意图。roll=" + i
                + "，turnIndex=T" + turnIndex
                + "，shieldChanting=" + shieldChanting
                + "，currentBlock=" + this.currentBlock
                + "，副意图是否隐藏显示=" + hideSecondaryForRenderThisTurn + "。");

        if (shieldChanting) {
            secondaryIntentCode = SEC_NONE;
            if (this.currentBlock > 0) {
                // ATTACK intents must carry a valid damage value, otherwise vanilla will spam "IS SET INCORRECTLY".
                setMove(getIntentTooltipTitleForMove(MOVE_T6_EXECUTE), MOVE_T6_EXECUTE, Intent.ATTACK, 99999);
                logPlannedMove("吟唱状态延续，护盾尚在，下一回合进入【终极封印·处决】");
                applyCycleCurseForMove(MOVE_T6_EXECUTE);
            } else {
                cancelChantAndReturnToT2("吟唱状态延续，但护盾已破，直接回到T2");
                logPlannedMove("吟唱状态延续，但护盾已破，直接回到T2");
                applyCycleCurseForMove(MOVE_T2_STRIKE_DEBUFF);
            }
            return;
        }

        byte plannedMoveCode;
        if (turnIndex <= 1) {
            secondaryIntentCode = SEC_CURSE_JACKIE;
            setAttackDebuffMove(MOVE_T1_MULTI, t1Damage, getT1HitCount());
            plannedMoveCode = MOVE_T1_MULTI;
            logPlannedMove("进入循环起手T1");
        } else if (turnIndex == 2) {
            secondaryIntentCode = SEC_DEFEND_ARTIFACT;
            setAttackDebuffMove(MOVE_T2_STRIKE_DEBUFF, t2Damage, getT2HitCount());
            plannedMoveCode = MOVE_T2_STRIKE_DEBUFF;
            logPlannedMove("进入循环T2");
        } else if (turnIndex == 3) {
            secondaryIntentCode = SEC_RANDOM_CURSE;
            setAttackDebuffMove(MOVE_T3_SEAL_STRIKE, t3Damage, getT3HitCount());
            plannedMoveCode = MOVE_T3_SEAL_STRIKE;
            logPlannedMove("进入循环T3");
        } else if (turnIndex == 4) {
            secondaryIntentCode = SEC_SEAL_TALISMAN;
            setAttackBuffMove(MOVE_T4_BARRAGE, t4Damage, getT4HitCount());
            plannedMoveCode = MOVE_T4_BARRAGE;
            logPlannedMove("进入循环T4");
        } else if (turnIndex == 5) {
            secondaryIntentCode = SEC_DOUBLE_CURSE;
            setMove(getIntentTooltipTitleForMove(MOVE_T5_CHANT), MOVE_T5_CHANT, Intent.DEFEND_BUFF);
            plannedMoveCode = MOVE_T5_CHANT;
            logPlannedMove("进入循环T5");
        } else {
            turnIndex = 2;
            secondaryIntentCode = SEC_DEFEND_ARTIFACT;
            setAttackDebuffMove(MOVE_T2_STRIKE_DEBUFF, t2Damage, getT2HitCount());
            plannedMoveCode = MOVE_T2_STRIKE_DEBUFF;
            logPlannedMove("检测到非法或已完成的回合序号，重置回T2循环");
        }
        applyCycleCurseForMove(plannedMoveCode);
    }

    private void applyCycleCurseForMove(byte moveCode) {
        int stage = getCycleCurseStageForMove(moveCode);
        if (stage > 0) {
            GrandMageDadCycleCurseHelper.applyStage(this, stage);
        } else {
            GrandMageDadCycleCurseHelper.endPlayerCurseTurn();
        }
    }

    private void handleHorseSealAtTurnStart() {
        int healAmount = getHorseSealHealAmount();
        if (healAmount > 0) {
            queueWithLog("午马封存效果触发：按已损生命值的1/8回复生命，本次回复=" + healAmount + "。", new HealAction(this, this, healAmount));
        } else {
            log("午马封存效果触发：当前已损生命值不足8，本次回复=0。");
        }
        refreshDynamicDescriptions();
    }
    private int getHorseSealHealAmount() {
        return Math.max(0, this.maxHealth - this.currentHealth) / 8;
    }

    private void handleMonkeySealAtTurnStart() {
        returnMonkeyBorrowedCardToDiscard("申猴封存效果再次触发前，先归还上一张借走的卡牌", true);

        ArrayList<MonkeyBorrowCandidate> candidates = buildMonkeyBorrowCandidates();
        if (candidates.isEmpty()) {
            log("封存效果【猴】：当前抽牌堆与弃牌堆都没有可借走的卡牌，改为回复30点生命。");
            queue(new HealAction(this, this, MONKEY_NO_CARD_HEAL));
            refreshDynamicDescriptions();
            return;
        }

        ArrayList<MonkeyBorrowCandidate> filteredCandidates = filterMonkeyBorrowCandidates(candidates);
        MonkeyBorrowCandidate chosen = filteredCandidates.get(AbstractDungeon.miscRng.random(filteredCandidates.size() - 1));
        chosen.group.removeCard(chosen.card);
        monkeyBorrowedCard = chosen.card;
        monkeyBorrowedFrom = chosen.sourceName;
        monkeyLastBorrowedCardUuid = chosen.card.uuid;
        showMonkeyBorrowAnimation(chosen.card);
        log("封存效果【猴】：从" + chosen.sourceName + "借走卡牌【" + describeCard(chosen.card) + "】，玩家本回合无法使用该牌。");
        refreshDynamicDescriptions();
    }

    private ArrayList<MonkeyBorrowCandidate> buildMonkeyBorrowCandidates() {
        ArrayList<MonkeyBorrowCandidate> candidates = new ArrayList<>();
        if (AbstractDungeon.player == null) {
            log("申猴借牌失败：玩家对象为空。");
            return candidates;
        }
        collectMonkeyBorrowCandidates(candidates, AbstractDungeon.player.drawPile, "抽牌堆");
        collectMonkeyBorrowCandidates(candidates, AbstractDungeon.player.discardPile, "弃牌堆");
        return candidates;
    }

    private void collectMonkeyBorrowCandidates(ArrayList<MonkeyBorrowCandidate> candidates, CardGroup group, String sourceName) {
        if (group == null || group.group == null) {
            return;
        }
        for (AbstractCard card : group.group) {
            if (isValidMonkeyBorrowCard(card)) {
                candidates.add(new MonkeyBorrowCandidate(card, group, sourceName));
            }
        }
    }

    private boolean isValidMonkeyBorrowCard(AbstractCard card) {
        return card != null && card.type != AbstractCard.CardType.CURSE && card.type != AbstractCard.CardType.STATUS;
    }

    private ArrayList<MonkeyBorrowCandidate> filterMonkeyBorrowCandidates(ArrayList<MonkeyBorrowCandidate> candidates) {
        if (candidates.size() <= 1 || monkeyLastBorrowedCardUuid == null) {
            return candidates;
        }

        ArrayList<MonkeyBorrowCandidate> filtered = new ArrayList<>();
        for (MonkeyBorrowCandidate candidate : candidates) {
            if (!monkeyLastBorrowedCardUuid.equals(candidate.card.uuid)) {
                filtered.add(candidate);
            }
        }

        if (!filtered.isEmpty()) {
            return filtered;
        }
        return candidates;
    }

    private void returnMonkeyBorrowedCardToDiscard(String reason, boolean playAnimation) {
        if (monkeyBorrowedCard == null) {
            return;
        }

        AbstractCard card = monkeyBorrowedCard;
        log(reason + "：归还卡牌【" + describeCard(card) + "】到玩家弃牌堆顶。");
        if (playAnimation) {
            showMonkeyReturnAnimation(card);
        }

        if (AbstractDungeon.player != null && AbstractDungeon.player.discardPile != null) {
            removeCardFromAllPlayerGroups(card);
            AbstractDungeon.player.discardPile.addToTop(card);
        } else {
            log("申猴归还卡牌时玩家或弃牌堆为空，直接清理暂存状态。");
        }

        monkeyBorrowedCard = null;
        monkeyBorrowedFrom = null;
        refreshDynamicDescriptions();
    }

    private void removeCardFromAllPlayerGroups(AbstractCard card) {
        if (card == null || AbstractDungeon.player == null) {
            return;
        }
        removeCardFromGroup(AbstractDungeon.player.hand, card);
        removeCardFromGroup(AbstractDungeon.player.drawPile, card);
        removeCardFromGroup(AbstractDungeon.player.discardPile, card);
        removeCardFromGroup(AbstractDungeon.player.exhaustPile, card);
        removeCardFromGroup(AbstractDungeon.player.limbo, card);
    }

    private void removeCardFromGroup(CardGroup group, AbstractCard card) {
        if (group != null && group.contains(card)) {
            group.removeCard(card);
        }
    }

    private void showMonkeyBorrowAnimation(AbstractCard card) {
        if (card == null) {
            return;
        }
        AbstractDungeon.topLevelEffectsQueue.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
    }

    private void showMonkeyReturnAnimation(AbstractCard card) {
        if (card == null) {
            return;
        }
        AbstractDungeon.topLevelEffectsQueue.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
    }

    // 旧版申猴封存逻辑已封存，暂不再调用，保留仅供后续对照。
    private void applyMonkeySealLegacyEffect() {
        if (false) {
            queueWithLog("申猴封存效果触发：额外获得格挡=10。", new GainBlockAction(this, this, 10));
            if (nextMove != MOVE_T5_CHANT && nextMove != MOVE_T6_EXECUTE && nextMove != MOVE_T6_STUN) {
                int oldSecondary = secondaryIntentCode;
                secondaryIntentCode = rollRandomSecondaryIntent("申猴封存效果在老爹回合开始时重掷副意图");
                log("副意图已从【" + describeSecondaryIntent(oldSecondary) + "】改为【"
                        + describeSecondaryIntent(secondaryIntentCode) + "】。");
            } else {
                log("申猴封存效果存在，但当前属于终极封印阶段，本回合不重掷副意图。");
            }
        }
    }

    private int getCycleCurseStageForMove(byte moveCode) {
        switch (moveCode) {
            case MOVE_T2_STRIKE_DEBUFF:
                return GrandMageDadCycleCurseHelper.STAGE_COLLAPSE_UPGRADES;
            case MOVE_T3_SEAL_STRIKE:
                return GrandMageDadCycleCurseHelper.STAGE_EXHAUST_HAND;
            case MOVE_T4_BARRAGE:
                return GrandMageDadCycleCurseHelper.STAGE_CARD_DAMAGE;
            case MOVE_T5_CHANT:
                return GrandMageDadCycleCurseHelper.STAGE_CARD_DECAY;
            case MOVE_T6_EXECUTE:
                return GrandMageDadCycleCurseHelper.STAGE_TEMP_THORNS;
            default:
                return 0;
        }
    }

    @Override
    public void die() {
        log("Boss死亡。最终状态：" + snapshotState());
        GrandMageDadCycleCurseHelper.clearAll();
        returnAllSealedTalismans("老爹被击败，归还所有仍处于封存状态的符咒");
        returnMonkeyBorrowedCardToDiscard("老爹被击败，兜底归还申猴借走的卡牌", true);
        super.die();
    }

    @Override
    public void damage(DamageInfo info) {
        int beforeHp = this.currentHealth;
        int beforeBlock = this.currentBlock;
        int incomingBase = info == null ? -1 : info.base;
        int incomingOutput = info == null ? -1 : info.output;
        String sourceName = info == null ? "未知来源" : describeCreature(info.owner);

        super.damage(info);

        log("受到伤害结算：来源=" + sourceName
                + "，基础伤害=" + incomingBase
                + "，结算伤害=" + incomingOutput
                + "，生命 " + beforeHp + " -> " + this.currentHealth
                + "，格挡 " + beforeBlock + " -> " + this.currentBlock + "。");

        refreshDynamicDescriptions();
        if (shieldChanting && this.nextMove == MOVE_T6_EXECUTE && this.currentBlock <= 0) {
            log("吟唱护盾已在处决前被击破：立即把下回合主意图改为T2并取消处决。");
            cancelChantAndReturnToT2("吟唱护盾已在处决前被击破");
            createIntent();
        }
    }

    @Override
    public void heal(int healAmount, boolean showEffect) {
        super.heal(healAmount, showEffect);
        refreshDynamicDescriptions();
    }

    private void initializeOpeningIntent() {
        turnIndex = 1;
        secondaryIntentCode = SEC_CURSE_JACKIE;
        hideSecondaryForRenderThisTurn = false;
        hideSecondaryNextTurn = false;
        setAttackDebuffMove(MOVE_T1_MULTI, t1Damage, getT1HitCount());
        log("初始化开场意图：主意图=" + describePrimaryMove(nextMove)
                + "，副意图=" + describeSecondaryIntent(secondaryIntentCode) + "。");
    }

    private void ensureHealthBarVisible() {
        this.showHealthBar();
        this.healthBarUpdatedEvent();
    }

    private void queue(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToBottom(action);
    }

    private void queueWithLog(String description, AbstractGameAction action) {
        log(description);
        queue(action);
    }

    public int getSecondaryIntentCodeForRender() {
        return hideSecondaryForRenderThisTurn ? SEC_NONE : secondaryIntentCode;
    }

    public String getIntentTooltipTitle() {
        return getIntentTooltipTitleForMove(nextMove);
    }

    public String getIntentTooltipBody() {
        String[] text = getIntentUiText();
        StringBuilder body = new StringBuilder(getPrimaryIntentTooltipBody(text));
        if (secondaryIntentCode != SEC_NONE) {
            body.append(" NL ").append(text[14]).append(getSecondaryIntentTooltipBody(text));
        }
        if (hideSecondaryForRenderThisTurn && secondaryIntentCode != SEC_NONE) {
            body.append(" NL ").append(text[21]);
        }
        return body.toString();
    }

    public String getSealedTalismansSummary(String emptyText) {
        return describeTalismans(sealedTalismans.keySet(), emptyText);
    }

    public boolean isChantShieldProtectedFromPigTalisman() {
        return shieldChanting && this.hasPower(GrandMageChantShieldPower.POWER_ID);
    }

    private void cancelChantAndReturnToT2(String reason) {
        log(reason + "：取消T6B空回合，直接回到T2。");
        stopShieldChanting();
        turnIndex = 2;
        secondaryIntentCode = SEC_DEFEND_ARTIFACT;
        setAttackDebuffMove(MOVE_T2_STRIKE_DEBUFF, t2Damage, getT2HitCount());
    }

    private void executeShieldBrokenStun() {
        log("执行【闪腰硬直】效果：本回合不会攻击，不再失去力量，也不会失去格挡。");
        int beforeBlock = this.currentBlock;
        log("【闪腰硬直】本回合不会攻击，不再失去力量，改为获得50点护盾。");
        queue(new GainBlockAction(this, this, 50));
        log("【闪腰硬直】已排队获得50点护盾。当前格挡=" + beforeBlock + "，实际结算以动作执行时为准。");
        stopShieldChanting();
    }

    private void startShieldChanting() {
        shieldChanting = true;
        log("进入【终极封印】吟唱状态。");
        if (!this.hasPower(GrandMageChantShieldPower.POWER_ID)) {
            queue(new ApplyPowerAction(this, this, new GrandMageChantShieldPower(this, chantShield), chantShield));
            log("为吟唱状态附加【封印阵咏唱】Power，护盾值=" + chantShield + "。");
        } else {
            log("检测到【封印阵咏唱】Power已存在，本次不重复施加。");
        }

        // MonsterGroup.applyPreTurnLogic only keeps monster block if Barricade is present.
        if (!this.hasPower(BarricadePower.POWER_ID)) {
            queue(new ApplyPowerAction(this, this, new BarricadePower(this), 1));
            chantBarricadeAppliedBySelf = true;
            log("【终极封印】附加临时【壁垒】：吟唱护盾可保留到下回合。");
        } else if (chantBarricadeAppliedBySelf) {
            log("【终极封印】临时【壁垒】已存在，无需重复附加。");
        } else {
            log("检测到老爹已有其他来源【壁垒】，吟唱阶段直接复用。");
        }
    }

    private void stopShieldChanting() {
        shieldChanting = false;
        log("退出【终极封印】吟唱状态。");
        if (this.hasPower(GrandMageChantShieldPower.POWER_ID)) {
            queue(new RemoveSpecificPowerAction(this, this, GrandMageChantShieldPower.POWER_ID));
            log("排队移除【封印阵咏唱】Power。");
        } else {
            log("退出吟唱时未发现【封印阵咏唱】Power，无需移除。");
        }

        if (chantBarricadeAppliedBySelf) {
            if (this.hasPower(BarricadePower.POWER_ID)) {
                queue(new RemoveSpecificPowerAction(this, this, BarricadePower.POWER_ID));
                log("【终极封印】排队移除临时【壁垒】。");
            } else {
                log("【终极封印】临时【壁垒】已提前消失，跳过移除。");
            }
            chantBarricadeAppliedBySelf = false;
        }
    }

    private void executeUltimateSealKill() {
        if (AbstractDungeon.player == null) {
            log("【终极封印·处决】玩家对象为空，无法执行直接斩杀。");
            return;
        }
        if (AbstractDungeon.player.isDead || AbstractDungeon.player.currentHealth <= 0) {
            log("【终极封印·处决】检测到玩家不可行动状态，跳过处决。isDead="
                    + AbstractDungeon.player.isDead + "，currentHealth=" + AbstractDungeon.player.currentHealth + "。");
            return;
        }
        log("【终极封印·处决】护盾未破，直接斩杀玩家。该效果无视无实体与常规伤害减免。");
        queue(new AbstractGameAction() {
            @Override
            public void update() {
                this.isDone = true;
                if (AbstractDungeon.player == null) {
                    return;
                }
                if (AbstractDungeon.player.isDead || AbstractDungeon.player.currentHealth <= 0) {
                    log("【终极封印·处决】动作执行时检测到玩家已不可行动，跳过伤害结算。isDead="
                            + AbstractDungeon.player.isDead + "，currentHealth=" + AbstractDungeon.player.currentHealth + "。");
                    return;
                }

                AbstractRoom room = AbstractDungeon.getCurrRoom();
                boolean originalCannotLose = false;
                if (room != null) {
                    originalCannotLose = room.cannotLose;
                    room.cannotLose = false;
                }
                try {
                    long lethalRaw = (long) AbstractDungeon.player.currentHealth + 999L;
                    int lethalDamage = (int) Math.min(Integer.MAX_VALUE, Math.max(999L, lethalRaw));
                    AbstractDungeon.player.damage(new DamageInfo(GrandMageDad.this, lethalDamage, DamageInfo.DamageType.HP_LOSS));
                } finally {
                    if (room != null) {
                        room.cannotLose = originalCannotLose;
                    }
                }

                if (!AbstractDungeon.player.isDead && AbstractDungeon.player.currentHealth > 0) {
                    log("【终极封印·处决】玩家未死亡，可能触发了复活或其他保命机制。当前生命="
                            + AbstractDungeon.player.currentHealth + "/" + AbstractDungeon.player.maxHealth + "。");
                }
            }
        });
    }

    private int getT1HitCount() {
        return getAttackHitCount(3);
    }

    private int getT2HitCount() {
        return getAttackHitCount(1);
    }

    private int getT3HitCount() {
        return getAttackHitCount(1);
    }

    private int getT4HitCount() {
        return getAttackHitCount(t4BaseHitCount);
    }

    private int getAttackHitCount(int baseHits) {
        return baseHits + rabbitExtraHits;
    }

    private void setAttackDebuffMove(byte moveCode, int damage, int hits) {
        if (hits > 1) {
            setMove(getIntentTooltipTitleForMove(moveCode), moveCode, Intent.ATTACK_DEBUFF, damage, hits, true);
        } else {
            setMove(getIntentTooltipTitleForMove(moveCode), moveCode, Intent.ATTACK_DEBUFF, damage);
        }
    }

    private void setAttackBuffMove(byte moveCode, int damage, int hits) {
        if (hits > 1) {
            setMove(getIntentTooltipTitleForMove(moveCode), moveCode, Intent.ATTACK_BUFF, damage, hits, true);
        } else {
            setMove(getIntentTooltipTitleForMove(moveCode), moveCode, Intent.ATTACK_BUFF, damage);
        }
    }

    private String getPrimaryIntentTooltipBody(String[] text) {
        switch (nextMove) {
            case MOVE_T1_MULTI:
                return formatUi(text[1], t1Damage, getT1HitCount());
            case MOVE_T2_STRIKE_DEBUFF:
                return formatUi(text[3], t2Damage, getT2HitCount());
            case MOVE_T3_SEAL_STRIKE:
                return formatUi(text[5], t3Damage, getT3HitCount(), getSealableCandidateSummary(text[22]));
            case MOVE_T4_BARRAGE:
                return formatUi(text[7], t4Damage, getT4HitCount());
            case MOVE_T5_CHANT:
                return formatUi(text[9], chantShield, chantArtifact);
            case MOVE_T6_EXECUTE:
                return text[11];
            case MOVE_T6_STUN:
                return text[13];
            default:
                return describePrimaryMove(nextMove);
        }
    }

    private String getSecondaryIntentTooltipBody(String[] text) {
        switch (secondaryIntentCode) {
            case SEC_CURSE_JACKIE:
                return text[15];
            case SEC_DEFEND_ARTIFACT:
                return text[16];
            case SEC_RANDOM_CURSE:
                return text[17];
            case SEC_CLEANSE_BUFF:
                return text[18];
            case SEC_DOUBLE_CURSE:
                return text[19];
            case SEC_SEAL_TALISMAN:
                return text[23];
            default:
                return text[20];
        }
    }

    private String getSealableCandidateSummary(String emptyText) {
        ArrayList<String> candidates = GrandMageDadHelper.getSealableTalismans(sealedTalismans.keySet());
        return describeTalismans(candidates, emptyText);
    }

    private String getIntentTooltipTitleForMove(byte moveCode) {
        String[] text = getIntentUiText();
        switch (moveCode) {
            case MOVE_T1_MULTI:
                return text[0];
            case MOVE_T2_STRIKE_DEBUFF:
                return text[2];
            case MOVE_T3_SEAL_STRIKE:
                return text[4];
            case MOVE_T4_BARRAGE:
                return text[6];
            case MOVE_T5_CHANT:
                return text[8];
            case MOVE_T6_EXECUTE:
                return text[10];
            case MOVE_T6_STUN:
                return text[12];
            default:
                return describePrimaryMove(moveCode);
        }
    }

    private static String[] getIntentUiText() {
        UIStrings ui = CardCrawlGame.languagePack == null ? null : CardCrawlGame.languagePack.getUIString(INTENT_UI_ID);
        String[] text = ui == null ? null : ui.TEXT;
        return text != null && text.length >= INTENT_UI_FALLBACK.length ? text : INTENT_UI_FALLBACK;
    }

    private static String[] getSealEffectUiText() {
        UIStrings ui = CardCrawlGame.languagePack == null ? null : CardCrawlGame.languagePack.getUIString(SEAL_EFFECT_UI_ID);
        String[] text = ui == null ? null : ui.TEXT;
        return text != null && text.length >= SEAL_EFFECT_UI_FALLBACK.length ? text : SEAL_EFFECT_UI_FALLBACK;
    }

    private String formatUi(String template, Object... args) {
        return String.format(Locale.ROOT, template, args);
    }

    private void logPlannedMove(String reason) {
        log(reason + "。规划结果：主意图=" + describePrimaryMove(nextMove)
                + "，副意图=" + describeSecondaryIntent(secondaryIntentCode)
                + "，副意图显示状态=" + (hideSecondaryForRenderThisTurn ? "隐藏" : "显示")
                + "。当前状态：" + snapshotState());
    }

    private String snapshotState() {
        return "生命=" + this.currentHealth + "/" + this.maxHealth
                + "，格挡=" + this.currentBlock
                + "，turnIndex=T" + turnIndex
                + "，吟唱中=" + shieldChanting
                + "，兔额外段数=" + rabbitExtraHits
                + "，单次受伤上限=" + singleHitCap
                + "，龙燃烧=" + dragonBurn
                + "，马回血=" + horseRegen
                + "，猴重掷=" + monkeyChaos
                + "，猪附伤=" + pigDebuff
                + "，已封存符咒=" + describeTalismans(sealedTalismans.keySet(), "无");
    }

    private String describePrimaryMove(byte moveCode) {
        switch (moveCode) {
            case MOVE_T1_MULTI:
                return "雷符连打";
            case MOVE_T2_STRIKE_DEBUFF:
                return "镇邪法印";
            case MOVE_T3_SEAL_STRIKE:
                return "符咒剥夺+追击";
            case MOVE_T4_BARRAGE:
                return "八卦连轰";
            case MOVE_T5_CHANT:
                return "终极封印·吟唱";
            case MOVE_T6_EXECUTE:
                return "终极封印·处决";
            case MOVE_T6_STUN:
                return "闪腰硬直";
            default:
                return "未知主意图(" + moveCode + ")";
        }
    }

    private String describeSecondaryIntent(int code) {
        switch (code) {
            case SEC_NONE:
                return "无";
            case SEC_CURSE_JACKIE:
                return "训诫（塞成龙）";
            case SEC_DEFEND_ARTIFACT:
                return "护体（格挡+人工制品+回血）";
            case SEC_RANDOM_CURSE:
                return "杂念入侵（随机诅咒）";
            case SEC_CLEANSE_BUFF:
                return "净化反噬（清Debuff并强化）";
            case SEC_DOUBLE_CURSE:
                return "咒阵扩散（双随机诅咒）";
            case SEC_SEAL_TALISMAN:
                return "符咒剥夺（封存符咒）";
            default:
                return "未知副意图(" + code + ")";
        }
    }

    private String describeCard(AbstractCard card) {
        if (card == null) {
            return "空卡";
        }
        return card.name + "/" + card.cardID;
    }

    private String describePower(AbstractPower power) {
        if (power == null) {
            return "空Power";
        }
        return power.name + "/" + power.ID + " x" + power.amount;
    }

    private String describeCreature(AbstractCreature creature) {
        if (creature == null) {
            return "未知单位";
        }
        return creature.name + "(" + creature.id + ")";
    }

    private String describeTalismans(Collection<String> talismanIds, String emptyText) {
        if (talismanIds == null || talismanIds.isEmpty()) {
            return emptyText;
        }
        StringBuilder builder = new StringBuilder();
        for (String talismanId : talismanIds) {
            builder.append(" NL ");
            builder.append(describeTalismanWithSealEffect(talismanId));
        }
        return builder.toString();
    }

    private String describeTalismans(Collection<String> talismanIds) {
        return describeTalismans(talismanIds, "无");
    }

    private String describeTalisman(String talismanId) {
        if (talismanId == null) {
            return "空符咒ID";
        }
        AbstractRelic sealedRelic = sealedTalismans.get(talismanId);
        if (sealedRelic != null) {
            return sealedRelic.name;
        }
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(talismanId)) {
            return AbstractDungeon.player.getRelic(talismanId).name;
        }
        AbstractRelic libraryRelic = RelicLibrary.getRelic(talismanId);
        return libraryRelic == null ? talismanId : libraryRelic.name;
    }

    private String describeTalismanWithSealEffect(String talismanId) {
        return "【 #b" + describeTalisman(talismanId) + " 】（" + getTalismanSealEffectText(talismanId) + "）";
    }

    private String getTalismanSealEffectText(String talismanId) {
        String[] text = getSealEffectUiText();
        if (talismanId == null) {
            return text[0];
        }
        if (RatTalisman.ID.equals(talismanId)) {
            return text[12];
        }
        if (OxTalisman.ID.equals(talismanId)) {
            return text[1];
        }
        if (TigerTalisman.ID.equals(talismanId)) {
            return text[2];
        }
        if (RabbitTalisman.ID.equals(talismanId)) {
            return text[3];
        }
        if (DragonTalisman.ID.equals(talismanId)) {
            return text[4];
        }
        if (SnakeTalisman.ID.equals(talismanId)) {
            return text[5];
        }
        if (HorseTalisman.ID.equals(talismanId)) {
            return getHorseSealEffectText(text);
        }
        if (SheepTalisman.ID.equals(talismanId)) {
            return text[7];
        }
        if (MonkeyTalisman.ID.equals(talismanId)) {
            return getMonkeySealEffectText(text);
        }
        if (RoosterTalisman.ID.equals(talismanId)) {
            return text[9];
        }
        if (DogTalisman.ID.equals(talismanId)) {
            return text[10];
        }
        if (PigTalisman.ID.equals(talismanId)) {
            return text[11];
        }
        return text[0];
    }

    private String getMonkeySealEffectText(String[] text) {
        StringBuilder builder = new StringBuilder(text[8]);
        if (monkeyBorrowedCard != null) {
            builder.append(text[13]);
            if (builder.length() > 0 && !Character.isWhitespace(builder.charAt(builder.length() - 1))) {
                builder.append(' ');
            }
            builder.append(FontHelper.colorString(monkeyBorrowedCard.name, "y"));
        } else {
            builder.append(text[14]);
        }
        builder.append(text[15]);
        return builder.toString();
    }

    private String getHorseSealEffectText(String[] text) {
        StringBuilder builder = new StringBuilder(text[6]);
        if (text[16] != null && !text[16].isEmpty()
                && builder.length() > 0
                && !Character.isWhitespace(builder.charAt(builder.length() - 1))
                && !Character.isWhitespace(text[16].charAt(0))) {
            builder.append(' ');
        }
        builder.append(text[16]).append(getHorseSealHealAmount()).append(text[17]);
        return builder.toString();
    }

    private void log(String message) {
        logDetail(message);
    }
}
