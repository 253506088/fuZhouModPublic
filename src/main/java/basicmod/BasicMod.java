package basicmod;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import basemod.eventUtil.AddEventParams;
import basemod.interfaces.AddAudioSubscriber;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basicmod.cards.demons.CardEightDemonPossession;
import basicmod.enums.FinalBossChoice;
import basicmod.helpers.AfuCardAudioHelper;
import basicmod.helpers.GrandMageDadCycleCurseHelper;
import basicmod.helpers.FinalBossChoiceManager;
import basicmod.powers.GrandMageCurseTaxAttackPower;
import basicmod.powers.GrandMageCurseTaxSkillPower;
import basicmod.util.GeneralUtils;
import basicmod.util.KeywordInfo;
import basicmod.util.Sounds;
import basicmod.util.TextureLoader;
import com.badlogic.gdx.Gdx;


import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.Patcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scannotation.AnnotationDB;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Mod主类，负责初始化和注册Mod的所有内容。
 * 实现了BaseMod的各种订阅者接口，用于注册卡牌、遗物、角色、事件、本地化等。
 */
@SpireInitializer
public class BasicMod implements
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        AddAudioSubscriber,
        PostInitializeSubscriber,
        basemod.interfaces.EditCardsSubscriber,
        basemod.interfaces.EditCharactersSubscriber,
        basemod.interfaces.EditRelicsSubscriber,
        basemod.interfaces.OnPlayerTurnStartPostDrawSubscriber,
        basemod.interfaces.OnCardUseSubscriber,
        basemod.interfaces.PostBattleSubscriber,
        basemod.interfaces.PostPowerApplySubscriber,
        basemod.interfaces.StartGameSubscriber {
    /** Mod信息对象，包含Mod的元数据（名称、作者、描述等） */
    public static ModInfo info;
    /** Mod的唯一标识符，从pom.xml中读取 */
    public static String modID;
    /** Mod配置对象，用于持久化存储用户设置 */
    private static SpireConfig modConfig;
    /** 半自动模式开关，控制某些自动化行为 */
    public static boolean semiAutoMode = false;
    /** 黑影兵团烧牌是否由玩家自选，true为自选，false为自动随机 */
    public static boolean shadowKhanManualExhaustMode = true;
    /** 【远古的封印】事件拒绝状态的存档键名 */
    public static final String PANKU_BOX_EVENT_DECLINED_SAVE_KEY = makeID("PanKuBoxEventDeclined");
    /** 本局游戏中玩家是否拒绝过【远古的封印】事件 */
    private static boolean panKuBoxEventDeclinedThisRun = false;
    /**
     * 保存【远古的封印】是否在本局被玩家明确拒绝。
     */
    private static final CustomSavable<Boolean> PAN_KU_BOX_EVENT_DECLINED_SAVABLE = new CustomSavable<Boolean>() {
        @Override
        public Boolean onSave() {
            return panKuBoxEventDeclinedThisRun;
        }

        @Override
        public void onLoad(Boolean declined) {
            panKuBoxEventDeclinedThisRun = Boolean.TRUE.equals(declined);
        }
    };

    static {
        loadModInfo();
    }
    /** 资源文件夹路径，基于包名自动检测 */
    private static final String resourcesFolder = checkResourcesPath();
    /** 日志记录器，用于输出调试信息到控制台 */
    public static final Logger logger = LogManager.getLogger(modID);

    /**
     * 为各种对象（卡牌、遗物等）的ID添加Mod前缀，避免不同Mod之间的命名冲突。
     *
     * @param id 原始ID
     * @return 添加了Mod前缀的完整ID，格式为 "modID:id"
     */
    public static String makeID(String id) {
        return modID + ":" + id;
    }

    /**
     * Mod初始化入口，由ModTheSpire通过@SpireInitializer注解自动调用。
     * 负责注册Mod颜色、创建Mod实例。
     */
    public static void initialize() {
        new BasicMod();
        // 注册颜色
        BaseMod.addColor(basicmod.enums.CharacterEnums.SHENGZHU_COLOR,
                com.megacrit.cardcrawl.helpers.CardHelper.getColor(200.0f, 50.0f, 50.0f),
                com.megacrit.cardcrawl.helpers.CardHelper.getColor(200.0f, 50.0f, 50.0f),
                com.megacrit.cardcrawl.helpers.CardHelper.getColor(200.0f, 50.0f, 50.0f),
                com.megacrit.cardcrawl.helpers.CardHelper.getColor(200.0f, 50.0f, 50.0f),
                com.megacrit.cardcrawl.helpers.CardHelper.getColor(200.0f, 50.0f, 50.0f),
                com.megacrit.cardcrawl.helpers.CardHelper.getColor(200.0f, 50.0f, 50.0f),
                com.megacrit.cardcrawl.helpers.CardHelper.getColor(200.0f, 50.0f, 50.0f),
                imagePath("512/bg_attack_red.png"),
                imagePath("512/bg_skill_red.png"),
                imagePath("512/bg_power_red.png"),
                imagePath("512/card_red_orb.png"),
                imagePath("1024/bg_attack_red.png"),
                imagePath("1024/bg_skill_red.png"),
                imagePath("1024/bg_power_red.png"),
                imagePath("1024/card_red_orb.png"),
                imagePath("512/card_small_orb.png"));
    }

    /**
     * 构造函数，向BaseMod订阅所有事件。
     */
    public BasicMod() {
        BaseMod.subscribe(this);
        logger.info(modID + " subscribed to BaseMod.");
    }

    /**
     * 接收遗物编辑事件，自动扫描并注册所有遗物。
     */
    @Override
    public void receiveEditRelics() {
        new basemod.AutoAdd(modID)
                .packageFilter(basicmod.relics.BaseRelic.class)
                .any(basicmod.relics.BaseRelic.class, (info, relic) -> {
                    if (relic.relicId == null || relic.relicId.startsWith(modID)) {
                        BaseMod.addRelic(relic, relic.relicType);
                    }
                });
    }

    /**
     * 接收角色编辑事件，注册圣主角色。
     */
    @Override
    public void receiveEditCharacters() {
        BaseMod.addCharacter(new basicmod.character.ShengZhuCustomPlayer(com.megacrit.cardcrawl.core.CardCrawlGame.languagePack.getCharacterString("fuZhouMod:ShengZhuCustomPlayer").NAMES[0], basicmod.enums.CharacterEnums.SHENGZHU),
                imagePath("character/shengzhu/button.png"),
                imagePath("character/shengzhu/portrait.png"),
                basicmod.enums.CharacterEnums.SHENGZHU);
    }

    /**
     * 接收卡牌编辑事件，自动扫描并注册所有卡牌。
     */
    @Override
    public void receiveEditCards() {
        new basemod.AutoAdd(modID)
                .packageFilter(basicmod.cards.BaseCard.class)
                .setDefaultSeen(true)
                .cards();
        UnlockTracker.markCardAsSeen(CardEightDemonPossession.ID);
        BaseMod.addCard(new CardEightDemonPossession());
    }

    /**
     * 接收初始化完成事件，注册Mod徽章、事件和配置。
     */
    @Override
    public void receivePostInitialize() {
        // This loads the image used as an icon in the in-game mods menu.
        Texture badgeTexture = TextureLoader.getTexture(imagePath("badge.png"));
        // Set up the mod information displayed in the in-game mods menu.
        // The information used is taken from your pom.xml file.

        // If you want to set up a config panel, that will be done here.
        // You can find information about this on the BaseMod wiki page "Mod Config and
        // Panel".
        BaseMod.registerModBadge(badgeTexture, info.Name, GeneralUtils.arrToString(info.Authors), info.Description,
                null);

        // 注册事件：圣主专属剧情——抢夺符咒 (只有持有至少一个符咒时才会在常规房池中出现)
        BaseMod.addEvent(new AddEventParams.Builder(basicmod.events.RobTalismanEvent.ID, basicmod.events.RobTalismanEvent.class)
                .playerClass(basicmod.enums.CharacterEnums.SHENGZHU) // 限制为圣主角色专属
                .spawnCondition(() -> basicmod.helpers.TalismanHelper.getOwnedTalismanCount() > 0)
                .bonusCondition(() -> basicmod.helpers.TalismanHelper.getOwnedTalismanCount() > 0) // 双重保险
                .create());

        // 注册事件：圣主专属剧情——恶魔小龙
        BaseMod.addEvent(new AddEventParams.Builder(basicmod.events.DragoEvent.ID, basicmod.events.DragoEvent.class)
                .playerClass(basicmod.enums.CharacterEnums.SHENGZHU) // 限制为圣主角色专属
                .spawnCondition(() -> !com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(basicmod.relics.CollaborationRelic.ID))
                .bonusCondition(() -> !com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(basicmod.relics.CollaborationRelic.ID)) // 双重保险，避免本层中途获得合作后仍抽到事件
                .create());

        // 注册事件：刀龙的契约 (黑暗加持)
        BaseMod.addEvent(new AddEventParams.Builder(basicmod.events.DaoLongEvent.ID, basicmod.events.DaoLongEvent.class)
                .playerClass(basicmod.enums.CharacterEnums.SHENGZHU) // 限制为圣主角色专属
                .spawnCondition(() -> com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.masterDeck.group.stream().anyMatch(c -> 
                                       c.cardID.equals(basicmod.cards.CardBlackHandChow.ID) ||
                                       c.cardID.equals(basicmod.cards.CardBlackHandRatso.ID) ||
                                       c.cardID.equals(basicmod.cards.CardBlackHandAhFen.ID) ||
                                       c.cardID.equals(basicmod.cards.CardTaiShanPress.ID)))
                .create());

        // 注册事件：远古的封印 (获取潘库宝盒)
        BaseMod.addEvent(new AddEventParams.Builder(basicmod.events.PanKuBoxEvent.ID, basicmod.events.PanKuBoxEvent.class)
                .spawnCondition(() -> (
                        // 条件1：地牢章节 = 第一幕(废墟) OR 第二幕(城市) OR 第三幕(高塔) → 你新加了第三幕！
                        com.megacrit.cardcrawl.dungeons.AbstractDungeon.id.equals("Exordium") || com.megacrit.cardcrawl.dungeons.AbstractDungeon.id.equals("TheCity") || com.megacrit.cardcrawl.dungeons.AbstractDungeon.id.equals("TheBeyond")) &&
                                      !com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(basicmod.relics.PanKuBox.ID
                                      ))
                .bonusCondition(() -> (
                        // 双重保险：事件池已生成后，如果玩家中途获得潘库宝盒，抽事件前也要过滤掉远古的封印
                        com.megacrit.cardcrawl.dungeons.AbstractDungeon.id.equals("Exordium") || com.megacrit.cardcrawl.dungeons.AbstractDungeon.id.equals("TheCity") || com.megacrit.cardcrawl.dungeons.AbstractDungeon.id.equals("TheBeyond")) &&
                                      !com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(basicmod.relics.PanKuBox.ID
                                      ))
                .create());

        // 注册事件：最"安全"的十三区（全角色可见）
        BaseMod.addEvent(new AddEventParams.Builder(basicmod.events.SafeArea13Event.ID, basicmod.events.SafeArea13Event.class)
                .create());

        // 注册事件：岁月史书（全角色随机奖励事件）
        BaseMod.addEvent(new AddEventParams.Builder(basicmod.events.HistoryBookEvent.ID, basicmod.events.HistoryBookEvent.class)
                .spawnCondition(() -> com.megacrit.cardcrawl.dungeons.AbstractDungeon.actNum > 1)
                .create());

        // 注册事件：被封印的石板——艾克佐迪亚（全角色可见）
        BaseMod.addEvent(new AddEventParams.Builder(basicmod.events.ExodiaEvent.ID, basicmod.events.ExodiaEvent.class)
                .create());

        // 初始化配置
        try {
            Properties defaults = new Properties();
            defaults.setProperty("semiAutoMode", "true");
            defaults.setProperty("shadowKhanManualExhaustMode", "true");
            defaults.setProperty("finalBossChoice", "DAD");
            modConfig = new SpireConfig(modID, "fuZhouModConfig", defaults);
            semiAutoMode = modConfig.getBool("semiAutoMode");
            shadowKhanManualExhaustMode = modConfig.getBool("shadowKhanManualExhaustMode");
            FinalBossChoice defaultChoice = FinalBossChoice.fromString(modConfig.getString("finalBossChoice"));
            FinalBossChoiceManager.setDefaultChoice(defaultChoice);
            BaseMod.addSaveField(FinalBossChoiceManager.SAVE_KEY, FinalBossChoiceManager.getInstance());
            BaseMod.addSaveField(PANKU_BOX_EVENT_DECLINED_SAVE_KEY, PAN_KU_BOX_EVENT_DECLINED_SAVABLE);
            logger.info("Configuration loaded: semiAutoMode = " + semiAutoMode + ", shadowKhanManualExhaustMode = " + shadowKhanManualExhaustMode);
        } catch (Exception e) {
            logger.error("Failed to initialize SpireConfig", e);
        }
    }

    /**
     * 保存Mod配置到文件。
     */
    public static void saveConfig() {
        try {
            if (modConfig != null) {
                modConfig.setBool("semiAutoMode", semiAutoMode);
                modConfig.setBool("shadowKhanManualExhaustMode", shadowKhanManualExhaustMode);
                modConfig.setString("finalBossChoice", FinalBossChoiceManager.getDefaultChoice().name());
                modConfig.save();
                logger.info("Configuration saved: semiAutoMode = " + semiAutoMode + ", shadowKhanManualExhaustMode = " + shadowKhanManualExhaustMode);
            }
        } catch (Exception e) {
            logger.error("Failed to save SpireConfig", e);
        }
    }

    /**
     * 判断本局是否已在【远古的封印】里选择过转身离开。
     *
     * @return true 表示本局已拒绝过该事件
     */
    public static boolean hasDeclinedPanKuBoxEventThisRun() {
        return panKuBoxEventDeclinedThisRun;
    }

    /**
     * 记录玩家本局已明确拒绝【远古的封印】。
     */
    public static void markPanKuBoxEventDeclinedThisRun() {
        panKuBoxEventDeclinedThisRun = true;
        logger.info("【远古的封印】已记录本局拒绝标记，后续二层保底将不再强制触发。");
    }

    /**
     * 新开一局时清空【远古的封印】拒绝标记。
     */
    public static void resetPanKuBoxEventDeclinedThisRun() {
        panKuBoxEventDeclinedThisRun = false;
        logger.info("【远古的封印】已重置本局拒绝标记。");
    }

    @Override
    public void receivePostPowerApplySubscriber(com.megacrit.cardcrawl.powers.AbstractPower power, com.megacrit.cardcrawl.core.AbstractCreature target, com.megacrit.cardcrawl.core.AbstractCreature source) {
        // 核心改动：使用 ID 检查，弃用 instanceof，符合杀戮尖塔最标准的底层查询规范。
        // 将刚被赋予的状态 power 实体也传进检测，防止 BaseMod 的 Hook 在对象加入怪物集合前过早触发导致遗漏。
        if (basicmod.powers.BurningPower.POWER_ID.equals(power.ID) || basicmod.powers.SoakedPower.POWER_ID.equals(power.ID)) {
            basicmod.helpers.SynthesisHelper.checkSteam(target, source, power);
        }
    }

    /**
     * 接收玩家回合开始（抽牌后）事件，重置影子卡计数并处理大法师诅咒循环。
     */
    /*----------Hook Implementation----------*/
    @Override
    public void receiveOnPlayerTurnStartPostDraw() {
        basicmod.helpers.MaskManager.shadowKhanCardsPlayedThisTurn = 0;
        GrandMageDadCycleCurseHelper.onPlayerTurnStartPostDraw();
    }

    /**
     * 接收卡牌使用事件，处理影子卡计数、大法师诅咒消耗、阿福卡牌语音播放。
     *
     * @param c 被使用的卡牌
     */
    @Override
    public void receiveCardUsed(com.megacrit.cardcrawl.cards.AbstractCard c) {
        if (c instanceof basicmod.cards.shadowkhan.BaseShadowKhanCard) {
            basicmod.helpers.MaskManager.shadowKhanCardsPlayedThisTurn++;
        }
        GrandMageDadCycleCurseHelper.onPlayerCardUsed(c);
        // 阿福卡牌打出时播放对应语音，未配置音频的阿福卡会静默跳过。
        AfuCardAudioHelper.playIfAfuCard(c);

        if (com.megacrit.cardcrawl.dungeons.AbstractDungeon.player != null) {
            if (c != null
                    && c.type == com.megacrit.cardcrawl.cards.AbstractCard.CardType.ATTACK
                    && com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasPower(GrandMageCurseTaxAttackPower.POWER_ID)) {
                logger.info("【诅咒税-消费】打出攻击牌={}({})，准备移除{}。",
                        c.name, c.cardID, GrandMageCurseTaxAttackPower.POWER_ID);
                com.megacrit.cardcrawl.dungeons.AbstractDungeon.actionManager.addToTop(
                        new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(
                                com.megacrit.cardcrawl.dungeons.AbstractDungeon.player,
                                com.megacrit.cardcrawl.dungeons.AbstractDungeon.player,
                                GrandMageCurseTaxAttackPower.POWER_ID));
            } else if (c != null
                    && c.type == com.megacrit.cardcrawl.cards.AbstractCard.CardType.SKILL
                    && com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasPower(GrandMageCurseTaxSkillPower.POWER_ID)) {
                logger.info("【诅咒税-消费】打出技能牌={}({})，准备移除{}。",
                        c.name, c.cardID, GrandMageCurseTaxSkillPower.POWER_ID);
                com.megacrit.cardcrawl.dungeons.AbstractDungeon.actionManager.addToTop(
                        new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(
                                com.megacrit.cardcrawl.dungeons.AbstractDungeon.player,
                                com.megacrit.cardcrawl.dungeons.AbstractDungeon.player,
                                GrandMageCurseTaxSkillPower.POWER_ID));
            }
        }
    }

    /**
     * 接收战斗结束事件，清理大法师诅咒循环、面具管理器、检查符咒觉醒、重置艾克佐迪亚。
     *
     * @param r 当前房间
     */
    @Override
    public void receivePostBattle(com.megacrit.cardcrawl.rooms.AbstractRoom r) {
        GrandMageDadCycleCurseHelper.clearAll();
        basicmod.helpers.MaskManager.clear();
        basicmod.helpers.TalismanAwakeningHelper.checkAndTriggerAwakening();
        basicmod.cards.colorless.ExodiaHelper.reset();
    }

    /**
     * 接收游戏开始事件，重置手牌上限、最终Boss选择、潘库宝盒事件状态，检查符咒觉醒。
     */
    @Override
    public void receiveStartGame() {
        // 游戏启动或进入存档时，重置手牌上限
        // 基础为 10，如果拥有鸡符咒则增加到 13
        basemod.BaseMod.MAX_HAND_SIZE = 10;
        if (com.megacrit.cardcrawl.dungeons.AbstractDungeon.floorNum <= 0) {
            FinalBossChoiceManager.resetForNewRun();
            resetPanKuBoxEventDeclinedThisRun();
        }
        if (com.megacrit.cardcrawl.dungeons.AbstractDungeon.player != null && 
            com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(basicmod.relics.RoosterTalisman.ID)) {
            basemod.BaseMod.MAX_HAND_SIZE = 13;
        }
        basicmod.helpers.TalismanAwakeningHelper.checkAndTriggerAwakening();
    }

    /*----------Localization----------*/

    /**
     * 获取当前语言的小写标识符。
     *
     * @return 语言标识符，如 "eng"、"zhs"
     */
    private static String getLangString() {
        return Settings.language.name().toLowerCase();
    }

    /** 默认语言（英文） */
    private static final String defaultLanguage = "eng";

    /** 已注册的关键字映射表，键为关键字ID，值为关键字信息 */
    public static final Map<String, KeywordInfo> keywords = new HashMap<>();

    /**
     * 接收字符串编辑事件，加载本地化字符串文件。
     */
    @Override
    public void receiveEditStrings() {
        /*
         * First, load the default localization.
         * Then, if the current language is different, attempt to load localization for
         * that language.
         * This results in the default localization being used for anything that might
         * be missing.
         * The same process is used to load keywords slightly below.
         */
        loadLocalization(defaultLanguage); // no exception catching for default localization; you better have at least
                                           // one that works.
        if (!defaultLanguage.equals(getLangString())) {
            try {
                loadLocalization(getLangString());
            } catch (GdxRuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载指定语言的所有本地化文件。
     *
     * @param lang 语言标识符，如 "eng"、"zhs"
     */
    private void loadLocalization(String lang) {
        BaseMod.loadCustomStringsFile(CardStrings.class,
                localizationPath(lang, "CardStrings.json"));
        BaseMod.loadCustomStringsFile(CharacterStrings.class,
                localizationPath(lang, "CharacterStrings.json"));
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                localizationPath(lang, "PowerStrings.json"));
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                localizationPath(lang, "RelicStrings.json"));
        BaseMod.loadCustomStringsFile(EventStrings.class,
                localizationPath(lang, "EventStrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class,
                localizationPath(lang, "UIStrings.json"));
    }

    /**
     * 接收关键字编辑事件，加载并注册所有自定义关键字。
     */
    @Override
    public void receiveEditKeywords() {
        Gson gson = new Gson();
        String json = Gdx.files.internal(localizationPath(defaultLanguage, "Keywords.json"))
                .readString(String.valueOf(StandardCharsets.UTF_8));
        KeywordInfo[] keywords = gson.fromJson(json, KeywordInfo[].class);
        for (KeywordInfo keyword : keywords) {
            keyword.prep();
            registerKeyword(keyword);
        }

        if (!defaultLanguage.equals(getLangString())) {
            try {
                json = Gdx.files.internal(localizationPath(getLangString(), "Keywords.json"))
                        .readString(String.valueOf(StandardCharsets.UTF_8));
                keywords = gson.fromJson(json, KeywordInfo[].class);
                for (KeywordInfo keyword : keywords) {
                    keyword.prep();
                    registerKeyword(keyword);
                }
            } catch (Exception e) {
                logger.warn(modID + " does not support " + getLangString() + " keywords.");
            }
        }
    }

    /**
     * 注册单个关键字到BaseMod。
     *
     * @param info 关键字信息
     */
    private void registerKeyword(KeywordInfo info) {
        String[] names = Arrays.copyOf(info.NAMES, info.NAMES.length);
        BaseMod.addKeyword(modID.toLowerCase(), info.PROPER_NAME, names, info.DESCRIPTION, info.COLOR);
        if (!info.ID.isEmpty()) {
            keywords.put(info.ID, info);
        }
    }

    /**
     * 接收音频添加事件，加载Sounds类中定义的所有音频文件。
     */
    @Override
    public void receiveAddAudio() {
        loadAudio(Sounds.class);
    }

    /** 支持的音频文件扩展名列表 */
    private static final String[] AUDIO_EXTENSIONS = { ".ogg", ".wav", ".mp3" };

    /**
     * 从指定类中加载所有音频字段，自动检测音频文件路径。
     *
     * @param cls 包含音频路径字段的类
     */
    private void loadAudio(Class<?> cls) {
        try {
            Field[] fields = cls.getDeclaredFields();
            outer: for (Field f : fields) {
                int modifiers = f.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && f.getType().equals(String.class)) {
                    String s = (String) f.get(null);
                    if (s == null) { // If no defined value, determine path using field name
                        s = audioPath(f.getName());

                        for (String ext : AUDIO_EXTENSIONS) {
                            String testPath = s + ext;
                            if (Gdx.files.internal(testPath).exists()) {
                                s = testPath;
                                BaseMod.addAudio(s, s);
                                f.set(null, s);
                                continue outer;
                            }
                        }
                        throw new Exception("Failed to find an audio file \"" + f.getName() + "\" in " + resourcesFolder
                                + "/audio; check to ensure the capitalization and filename are correct.");
                    } else { // Otherwise, load defined path
                        if (Gdx.files.internal(s).exists()) {
                            BaseMod.addAudio(s, s);
                        } else {
                            throw new Exception("Failed to find audio file \"" + s
                                    + "\"; check to ensure this is the correct filepath.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred in loadAudio: ", e);
        }
    }

    /**
     * 生成本地化文件的完整路径。
     *
     * @param lang 语言标识符
     * @param file 文件名
     * @return 完整的本地化文件路径
     */
    public static String localizationPath(String lang, String file) {
        return resourcesFolder + "/localization/" + lang + "/" + file;
    }

    /**
     * 生成音频文件的完整路径。
     *
     * @param file 音频文件名
     * @return 完整的音频文件路径
     */
    public static String audioPath(String file) {
        return resourcesFolder + "/audio/" + file;
    }

    /**
     * 生成图片文件的完整路径。
     *
     * @param file 图片文件名
     * @return 完整的图片文件路径
     */
    public static String imagePath(String file) {
        return resourcesFolder + "/images/" + file;
    }

    /**
     * 生成角色图片文件的完整路径。
     *
     * @param file 角色图片文件名
     * @return 完整的角色图片文件路径
     */
    public static String characterPath(String file) {
        return resourcesFolder + "/images/character/" + file;
    }

    /**
     * 生成能力图标文件的完整路径。
     *
     * @param file 能力图标文件名
     * @return 完整的能力图标文件路径
     */
    public static String powerPath(String file) {
        return resourcesFolder + "/images/powers/" + file;
    }

    /**
     * 生成遗物图标文件的完整路径。
     *
     * @param file 遗物图标文件名
     * @return 完整的遗物图标文件路径
     */
    public static String relicPath(String file) {
        return resourcesFolder + "/images/relics/" + file;
    }

    /**
     * Checks the expected resources path based on the package name.
     */
    private static String checkResourcesPath() {
        String name = BasicMod.class.getName(); // getPackage can be iffy with patching, so class name is used instead.
        int separator = name.indexOf('.');
        if (separator > 0)
            name = name.substring(0, separator);

        if (Gdx.files != null) {
            FileHandle resources = Gdx.files.internal(name);
            if (!resources.exists()) {
                throw new RuntimeException("\n\tFailed to find resources folder; expected it to be at  \"resources/" + name
                        + "\"." +
                        " Either make sure the folder under resources has the same name as your mod's package, or change the line\n"
                        +
                        "\t\"private static final String resourcesFolder = checkResourcesPath();\"\n" +
                        "\tat the top of the " + BasicMod.class.getSimpleName() + " java file.");
            }
            if (!resources.child("images").exists()) {
                throw new RuntimeException("\n\tFailed to find the 'images' folder in the mod's 'resources/" + name
                        + "' folder; Make sure the " +
                        "images folder is in the correct location.");
            }
            if (!resources.child("localization").exists()) {
                throw new RuntimeException("\n\tFailed to find the 'localization' folder in the mod's 'resources/" + name
                        + "' folder; Make sure the " +
                        "localization folder is in the correct location.");
            }
        }

        return name;
    }

    /**
     * This determines the mod's ID based on information stored by ModTheSpire.
     */
    private static void loadModInfo() {
        Optional<ModInfo> infos = Arrays.stream(Loader.MODINFOS).filter((modInfo) -> {
            AnnotationDB annotationDB = Patcher.annotationDBMap.get(modInfo.jarURL);
            if (annotationDB == null)
                return false;
            Set<String> initializers = annotationDB.getAnnotationIndex().getOrDefault(SpireInitializer.class.getName(),
                    Collections.emptySet());
            return initializers.contains(BasicMod.class.getName());
        }).findFirst();
        if (infos.isPresent()) {
            info = infos.get();
            modID = info.ID;
        } else {
            throw new RuntimeException("Failed to determine mod info/ID based on initializer.");
        }
    }

}
