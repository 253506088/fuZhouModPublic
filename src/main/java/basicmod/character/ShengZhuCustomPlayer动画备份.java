//package basicmod.character;
//
//import basemod.abstracts.CustomPlayer;
//import basicmod.BasicMod;
//import basicmod.enums.CharacterEnums;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.math.MathUtils;
//import com.megacrit.cardcrawl.actions.AbstractGameAction;
//import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
//import com.megacrit.cardcrawl.cards.AbstractCard;
//import com.megacrit.cardcrawl.characters.AbstractPlayer;
//import com.megacrit.cardcrawl.core.CardCrawlGame;
//import com.megacrit.cardcrawl.core.EnergyManager;
//import com.megacrit.cardcrawl.core.Settings;
//import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
//import com.megacrit.cardcrawl.events.city.Vampires;
//import com.megacrit.cardcrawl.helpers.FontHelper;
//import com.megacrit.cardcrawl.helpers.ScreenShake;
//import com.megacrit.cardcrawl.screens.CharSelectInfo;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.util.ArrayList;
//
//public class ShengZhuCustomPlayer动画备份 extends CustomPlayer {
//    private static final Logger logger = LogManager.getLogger(ShengZhuCustomPlayer动画备份.class.getName());
//
//    public static final int ENERGY_PER_TURN = 3;
//    public static final int STARTING_HP = 80;
//    public static final int MAX_HP = 80;
//    public static final int STARTING_GOLD = 99;
//    public static final int CARD_DRAW = 5;
//    public static final int ORB_SLOTS = 0;
//
//    // ========== idle帧序列动画相关 ==========
//    private static final int IDLE_FRAME_COUNT = 16;         // 帧总数
//    private static final float SECONDS_PER_FRAME = 0.08f;   // 每帧持续时间（秒），16帧约1.28秒一个循环
//    private Texture[] idleFrames = null;                     // 帧纹理数组
//    private int currentFrame = 0;                            // 当前帧索引
//    private float frameTimer = 0.0f;                         // 帧计时器
//    private boolean animLoggedOnce = false;                  // 防止update/render日志刷屏的标记
//
//    public ShengZhuCustomPlayer动画备份(String name, PlayerClass setClass) {
//        super(name, setClass, new basicmod.util.ShengZhuEnergyOrb(),
//                (String) null, (String) null);
//
//        initializeClass(BasicMod.imagePath("character/shengzhu/statue.png"),
//                BasicMod.imagePath("character/shengzhu/shoulder.png"),
//                BasicMod.imagePath("character/shengzhu/shoulder.png"),
//                BasicMod.imagePath("character/shengzhu/corpse.png"),
//                getLoadout(), 20.0F, -10.0F, 220.0F, 290.0F, new EnergyManager(ENERGY_PER_TURN));
//
//        this.dialogX = (this.drawX + 0.0F * Settings.scale);
//        this.dialogY = (this.drawY + 220.0F * Settings.scale);
//        logger.info("【圣主动画】角色构造完成，初始贴图为石像形态");
//    }
//
//    @Override
//    public void render(SpriteBatch sb) {
//        // 如果在篝火/营地界面，游戏引擎默认会用 512x512 且只画左下角区域
//        // 我们在这里拦截一下，如果是营地且不是战斗，就按图片的真实比例画
//        if (AbstractDungeon.getCurrRoom() instanceof com.megacrit.cardcrawl.rooms.RestRoom) {
//            Texture shoulderImg = this.animX > 0.0F ? this.shoulder2Img : this.shoulderImg;
//            if (shoulderImg != null) {
//                sb.setColor(Color.WHITE);
//                sb.draw(shoulderImg,
//                        this.drawX - (float) shoulderImg.getWidth() * Settings.scale / 2.0F,
//                        this.drawY - 300.0F * Settings.scale,
//                        (float) shoulderImg.getWidth() * Settings.scale,
//                        (float) shoulderImg.getHeight() * Settings.scale);
//            }
//            this.hb.render(sb);
//            this.healthHb.render(sb);
//            return;
//        }
//
//        // 其它情况（如战斗、事件）走原本或自定义的 renderPlayerImage 逻辑
//        super.render(sb);
//    }
//
//    @Override
//    public void update() {
//        super.update();
//        // 帧序列动画：按时间推进帧索引，并直接更新 this.img 为当前帧纹理
//        // 这样无论父类用什么渲染路径画 this.img，显示的都是正确的动画帧
//        if (idleFrames != null) {
//            frameTimer += Gdx.graphics.getDeltaTime();
//            if (frameTimer >= SECONDS_PER_FRAME) {
//                frameTimer -= SECONDS_PER_FRAME;
//                int oldFrame = currentFrame;
//                currentFrame = (currentFrame + 1) % IDLE_FRAME_COUNT;
//                this.img = idleFrames[currentFrame];
//                // 只在首次循环时打一次日志，防止刷屏
//                if (!animLoggedOnce && currentFrame == 0 && oldFrame == IDLE_FRAME_COUNT - 1) {
//                    logger.info("【圣主动画】帧序列首次完整循环完毕（0→15→0），动画正常运行中");
//                    animLoggedOnce = true;
//                }
//            }
//        }
//    }
//
//    @Override
//    public void renderPlayerImage(SpriteBatch sb) {
//        // this.img 在 update() 中已被实时更新为当前帧纹理，这里直接画即可
//        if (this.img != null) {
//            sb.setColor(Color.WHITE);
//            // 这里将图片居中放于角色坐标点
//            sb.draw(this.img, this.drawX - this.img.getWidth() * Settings.scale / 2.0F + this.animX, this.drawY,
//                    this.img.getWidth() * Settings.scale, this.img.getHeight() * Settings.scale,
//                    0, 0, this.img.getWidth(), this.img.getHeight(), this.flipHorizontal, this.flipVertical);
//        }
//        this.hb.render(sb);
//        this.healthHb.render(sb);
//    }
//
//    // ========== 帧序列动画 公共方法（供外部如 RatTalisman 调用） ==========
//
//    /**
//     * 启用idle帧序列动画（有鼠符咒时调用）
//     * 同时将 this.img 切换为 main.png 作为兜底
//     */
//    public void enableIdleAnimation() {
//        logger.info("【圣主动画】enableIdleAnimation 被调用，开始加载帧序列动画...");
//        loadIdleFrames();
//        // 立即将 this.img 设为第一帧，确保渲染立刻生效
//        if (idleFrames != null && idleFrames[0] != null) {
//            this.img = idleFrames[0];
//        }
//        logger.info("【圣主动画】帧序列动画已启用，当前 idleFrames 是否为空: " + (idleFrames == null)
//                + "，this.img 已指向第一帧");
//    }
//
//    /**
//     * 禁用idle帧序列动画（失去鼠符咒时调用）
//     * 同时将 this.img 切换为 statue.png
//     */
//    public void disableIdleAnimation() {
//        logger.info("【圣主动画】disableIdleAnimation 被调用，清除帧序列动画，回退到石像静态图...");
//        clearIdleFrames();
//        this.img = basicmod.util.TextureLoader.getTexture(BasicMod.imagePath("character/shengzhu/statue.png"));
//        logger.info("【圣主动画】帧序列动画已禁用，当前 idleFrames 是否为空: " + (idleFrames == null));
//    }
//
//    @Override
//    public CharSelectInfo getLoadout() {
//        return new CharSelectInfo("圣主", "远古恶魔，八大恶魔之一。",
//                STARTING_HP, MAX_HP, ORB_SLOTS, STARTING_GOLD, CARD_DRAW, this, getStartingRelics(),
//                getStartingDeck(), false);
//    }
//
//    @Override
//    public ArrayList<String> getStartingDeck() {
//        ArrayList<String> retVal = new ArrayList<>();
//        // 初始套牌：3张阿奋(打击)、3张周(防御)、2张拉苏、1张电眼逼人、1张尼嘉面具
//        for (int i = 0; i < 3; i++) {
//            retVal.add("fuZhouMod:BlackHandAhFen");
//            retVal.add("fuZhouMod:BlackHandChow");
//        }
//        retVal.add("fuZhouMod:BlackHandRatso");
//        retVal.add("fuZhouMod:BlackHandRatso");
//        retVal.add("fuZhouMod:LaserEyes");
//        retVal.add("fuZhouMod:NiJiaMask");
//        return retVal;
//    }
//
//    @Override
//    public ArrayList<String> getStartingRelics() {
//        ArrayList<String> retVal = new ArrayList<>();
//        // 符咒定位仪是初始遗物
//        retVal.add("fuZhouMod:TalismanLocator");
//        return retVal;
//    }
//
//    @Override
//    public void doCharSelectScreenSelectEffect() {
//        CardCrawlGame.sound.playA("ATTACK_HEAVY", MathUtils.random(-0.2F, 0.2F));
//        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT, false);
//    }
//
//    @Override
//    public String getCustomModeCharacterButtonSoundKey() {
//        return "ATTACK_HEAVY";
//    }
//
//    @Override
//    public int getAscensionMaxHPLoss() {
//        return 5;
//    }
//
//    @Override
//    public AbstractCard.CardColor getCardColor() {
//        return CharacterEnums.SHENGZHU_COLOR;
//    }
//
//    @Override
//    public Color getCardTrailColor() {
//        return com.megacrit.cardcrawl.helpers.CardHelper.getColor(200.0f, 50.0f, 50.0f);
//    }
//
//    @Override
//    public BitmapFont getEnergyNumFont() {
//        return FontHelper.energyNumFontRed;
//    }
//
//    @Override
//    public String getLocalizedCharacterName() {
//        return "圣主";
//    }
//
//    @Override
//    public Texture getEnergyImage() {
//        return basicmod.util.TextureLoader.getTexture(BasicMod.imagePath("512/card_red_orb.png"));
//    }
//
//    @Override
//    public AbstractCard getStartCardForEvent() {
//        return new basicmod.cards.CardBlackHandAhFen(); // Placeholder
//    }
//
//    @Override
//    public String getTitle(PlayerClass playerClass) {
//        return "圣主";
//    }
//
//    @Override
//    public AbstractPlayer newInstance() {
//        return new ShengZhuCustomPlayer动画备份("圣主", CharacterEnums.SHENGZHU);
//    }
//
//    @Override
//    public Color getCardRenderColor() {
//        return com.megacrit.cardcrawl.helpers.CardHelper.getColor(200.0f, 50.0f, 50.0f);
//    }
//
//    @Override
//    public Color getSlashAttackColor() {
//        return com.megacrit.cardcrawl.helpers.CardHelper.getColor(200.0f, 50.0f, 50.0f);
//    }
//
//    @Override
//    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
//        return new AbstractGameAction.AttackEffect[] {
//                AbstractGameAction.AttackEffect.SLASH_HEAVY,
//                AbstractGameAction.AttackEffect.FIRE,
//                AbstractGameAction.AttackEffect.SLASH_DIAGONAL };
//    }
//
//    @Override
//    public String getSpireHeartText() {
//        return "烧焦心脏";
//    }
//
//    @Override
//    public String getVampireText() {
//        return Vampires.DESCRIPTIONS[0];
//    }
//
//    /**
//     * 预加载idle帧序列动画纹理
//     */
//    private void loadIdleFrames() {
//        idleFrames = new Texture[IDLE_FRAME_COUNT];
//        for (int i = 0; i < IDLE_FRAME_COUNT; i++) {
//            String path = BasicMod.imagePath("character/shengzhu/idle/frame_" + String.format("%03d", i) + ".png");
//            idleFrames[i] = basicmod.util.TextureLoader.getTexture(path);
//            if (idleFrames[i] == null) {
//                logger.error("【圣主动画】帧纹理加载失败！路径: " + path);
//            } else {
//                logger.info("【圣主动画】帧纹理加载成功: " + path + " (" + idleFrames[i].getWidth() + "x" + idleFrames[i].getHeight() + ")");
//            }
//        }
//        currentFrame = 0;
//        frameTimer = 0.0f;
//        animLoggedOnce = false;
//        logger.info("【圣主动画】帧序列加载完毕，共 " + IDLE_FRAME_COUNT + " 帧，每帧 " + SECONDS_PER_FRAME + " 秒");
//    }
//
//    /**
//     * 清除帧序列动画，回退到静态图片
//     */
//    private void clearIdleFrames() {
//        idleFrames = null;
//        currentFrame = 0;
//        frameTimer = 0.0f;
//        animLoggedOnce = false;
//        logger.info("【圣主动画】帧序列已清除，回退为静态图片模式");
//    }
//
//    @Override
//    public void applyStartOfCombatLogic() {
//        super.applyStartOfCombatLogic();
//        logger.info("【圣主动画】applyStartOfCombatLogic 触发，开始检测鼠符咒...");
//        boolean hasRat = AbstractDungeon.player.hasRelic("fuZhouMod:RatTalisman");
//        logger.info("【圣主动画】是否持有鼠符咒(fuZhouMod:RatTalisman): " + hasRat);
//        // 刷新贴图，防止存读档后贴图变回石质形态
//        if (hasRat) {
//            // 有鼠符咒：使用帧序列动画
//            logger.info("【圣主动画】检测到鼠符咒，启用帧序列动画 + 复苏Power");
//            enableIdleAnimation();
//            AbstractDungeon.actionManager.addToBottom(
//                    new ApplyPowerAction(this, this, new basicmod.powers.ShenZhuRevivedPower(this)));
//        } else {
//            // 无鼠符咒：石像静态图片
//            logger.info("【圣主动画】未检测到鼠符咒，使用石像静态图 + 石像Power");
//            disableIdleAnimation();
//            AbstractDungeon.actionManager.addToBottom(
//                    new ApplyPowerAction(this, this, new basicmod.powers.ShenZhuStatuePower(this)));
//        }
//    }
//}
