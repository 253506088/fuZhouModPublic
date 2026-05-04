package basicmod.character;

import basemod.abstracts.CustomPlayer;
import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.Vampires;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.screens.CharSelectInfo;

import java.util.ArrayList;
import java.util.Locale;

public class ShengZhuCustomPlayer extends CustomPlayer {

    public static final int ENERGY_PER_TURN = 3;
    public static final int STARTING_HP = 80;
    public static final int MAX_HP = 80;
    public static final int STARTING_GOLD = 99;
    public static final int CARD_DRAW = 5;
    public static final int ORB_SLOTS = 0;
    private static final String STATUE_IMG_PATH = "character/shengzhu/statue.png";
    private static final String REVIVED_IMG_PATH = "character/shengzhu/main.png";
    private static final String IDLE_FRAME_DIR = "character/shengzhu/human_idle";
    private static final int IDLE_SOURCE_FRAME_COUNT = 20;
    private static final float IDLE_FRAME_INTERVAL_SECONDS = 0.05F;
    private static final float IDLE_RENDER_SCALE = 0.69F;

    private static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString("fuZhouMod:ShengZhuCustomPlayer");
    private static final String[] NAMES = characterStrings.NAMES;
    private static final String[] TEXT = characterStrings.TEXT;

    private Texture[] idleFrames;
    private int currentIdleFrame;
    private float idleFrameTimer;

    public ShengZhuCustomPlayer(String name, PlayerClass setClass) {
        super(name, setClass, new basicmod.util.ShengZhuEnergyOrb(),
                (String) null, (String) null);

        initializeClass(BasicMod.imagePath(STATUE_IMG_PATH),
                BasicMod.imagePath("character/shengzhu/shoulder.png"),
                BasicMod.imagePath("character/shengzhu/shoulder.png"),
                BasicMod.imagePath("character/shengzhu/corpse.png"),
                getLoadout(), 20.0F, -10.0F, 220.0F, 290.0F, new EnergyManager(ENERGY_PER_TURN));

        this.dialogX = this.drawX;
        this.dialogY = this.drawY + 220.0F * Settings.scale;
    }

    @Override
    public void render(SpriteBatch sb) {
        com.megacrit.cardcrawl.rooms.AbstractRoom room = AbstractDungeon.getCurrRoom();
        if (room instanceof com.megacrit.cardcrawl.rooms.RestRoom) {
            Texture shoulderImg = this.animX > 0.0F ? this.shoulder2Img : this.shoulderImg;
            if (shoulderImg != null) {
                sb.setColor(Color.WHITE);
                sb.draw(shoulderImg,
                        this.drawX - (float) shoulderImg.getWidth() * Settings.scale / 2.0F,
                        this.drawY - 300.0F * Settings.scale,
                        (float) shoulderImg.getWidth() * Settings.scale,
                        (float) shoulderImg.getHeight() * Settings.scale);
            }
            this.hb.render(sb);
            this.healthHb.render(sb);
            return;
        }

        if (room != null
                && (room.phase == com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.COMBAT
                || room instanceof com.megacrit.cardcrawl.rooms.MonsterRoom)
                && !this.isDead) {
            this.renderHealth(sb);
            if (this.orbs != null && !this.orbs.isEmpty()) {
                for (com.megacrit.cardcrawl.orbs.AbstractOrb orb : this.orbs) {
                    orb.render(sb);
                }
            }
        }

        if (this.img != null) {
            sb.setColor(Color.WHITE);
            float renderScale = this.idleFrames != null && this.idleFrames.length > 0 ? IDLE_RENDER_SCALE : 1.0F;
            float renderWidth = this.img.getWidth() * Settings.scale * renderScale;
            float renderHeight = this.img.getHeight() * Settings.scale * renderScale;
            sb.draw(this.img, this.drawX - renderWidth / 2.0F + this.animX, this.drawY,
                    renderWidth, renderHeight,
                    0, 0, this.img.getWidth(), this.img.getHeight(), this.flipHorizontal, this.flipVertical);
        }

        this.hb.render(sb);
        this.healthHb.render(sb);
    }

    @Override
    public void update() {
        super.update();
        if (this.idleFrames == null || this.idleFrames.length == 0) {
            return;
        }

        this.idleFrameTimer += Gdx.graphics.getDeltaTime();
        while (this.idleFrameTimer >= IDLE_FRAME_INTERVAL_SECONDS) {
            this.idleFrameTimer -= IDLE_FRAME_INTERVAL_SECONDS;
            this.currentIdleFrame = (this.currentIdleFrame + 1) % this.idleFrames.length;
            Texture frame = this.idleFrames[this.currentIdleFrame];
            if (frame != null) {
                this.img = frame;
            }
        }
    }

    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(NAMES[0], TEXT[0],
                STARTING_HP, MAX_HP, ORB_SLOTS, STARTING_GOLD, CARD_DRAW, this, getStartingRelics(),
                getStartingDeck(), false);
    }

    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> retVal = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            retVal.add("fuZhouMod:BlackHandAhFen");
            retVal.add("fuZhouMod:BlackHandChow");
        }
        retVal.add("fuZhouMod:BlackHandRatso");
        retVal.add("fuZhouMod:BlackHandFirstStrike");
        retVal.add("fuZhouMod:LaserEyes");
        retVal.add("fuZhouMod:NiJiaMask");
        return retVal;
    }

    @Override
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add("fuZhouMod:TalismanLocator");
        return retVal;
    }

    @Override
    public void doCharSelectScreenSelectEffect() {
        CardCrawlGame.sound.playA("ATTACK_HEAVY", MathUtils.random(-0.2F, 0.2F));
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT, false);
    }

    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return "ATTACK_HEAVY";
    }

    @Override
    public int getAscensionMaxHPLoss() {
        return 5;
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return CharacterEnums.SHENGZHU_COLOR;
    }

    @Override
    public Color getCardTrailColor() {
        return com.megacrit.cardcrawl.helpers.CardHelper.getColor(200.0f, 50.0f, 50.0f);
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontRed;
    }

    @Override
    public String getLocalizedCharacterName() {
        return NAMES[0];
    }

    @Override
    public Texture getEnergyImage() {
        return basicmod.util.TextureLoader.getTexture(BasicMod.imagePath("512/card_red_orb.png"));
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        return new basicmod.cards.CardBlackHandAhFen();
    }

    @Override
    public String getTitle(PlayerClass playerClass) {
        return NAMES[0];
    }

    @Override
    public AbstractPlayer newInstance() {
        return new ShengZhuCustomPlayer(NAMES[0], CharacterEnums.SHENGZHU);
    }

    @Override
    public Color getCardRenderColor() {
        return com.megacrit.cardcrawl.helpers.CardHelper.getColor(200.0f, 50.0f, 50.0f);
    }

    @Override
    public Color getSlashAttackColor() {
        return com.megacrit.cardcrawl.helpers.CardHelper.getColor(200.0f, 50.0f, 50.0f);
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[] {
                AbstractGameAction.AttackEffect.SLASH_HEAVY,
                AbstractGameAction.AttackEffect.FIRE,
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL };
    }

    @Override
    public String getSpireHeartText() {
        return TEXT[1];
    }

    @Override
    public String getVampireText() {
        return Vampires.DESCRIPTIONS[0];
    }

    @Override
    public void applyStartOfCombatLogic() {
        super.applyStartOfCombatLogic();
        if (AbstractDungeon.player.hasRelic("fuZhouMod:RatTalisman")) {
            enableIdleAnimation();
            AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(this, this, new basicmod.powers.ShenZhuRevivedPower(this)));
        } else {
            disableIdleAnimation();
            this.img = basicmod.util.TextureLoader.getTexture(BasicMod.imagePath(STATUE_IMG_PATH));
            AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(this, this, new basicmod.powers.ShenZhuStatuePower(this)));
        }
    }

    public void enableIdleAnimation() {
        loadIdleFrames();
        this.currentIdleFrame = 0;
        this.idleFrameTimer = 0.0F;
        if (this.idleFrames != null && this.idleFrames.length > 0 && this.idleFrames[0] != null) {
            this.img = this.idleFrames[0];
        } else {
            this.img = basicmod.util.TextureLoader.getTexture(BasicMod.imagePath(REVIVED_IMG_PATH));
        }
    }

    public void disableIdleAnimation() {
        this.idleFrames = null;
        this.currentIdleFrame = 0;
        this.idleFrameTimer = 0.0F;
        this.img = basicmod.util.TextureLoader.getTexture(BasicMod.imagePath(STATUE_IMG_PATH));
    }

    private void loadIdleFrames() {
        if (this.idleFrames != null && this.idleFrames.length == IDLE_SOURCE_FRAME_COUNT) {
            return;
        }

        Texture[] frames = new Texture[IDLE_SOURCE_FRAME_COUNT];
        int loaded = 0;
        for (int i = 0; i < IDLE_SOURCE_FRAME_COUNT; i++) {
            String path = BasicMod.imagePath(IDLE_FRAME_DIR + "/frame_" + String.format(Locale.ROOT, "%05d", i + 1) + ".png");
            try {
                Pixmap pixmap = new Pixmap(Gdx.files.internal(path));
                Texture texture = new Texture(pixmap);
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                pixmap.dispose();
                frames[i] = texture;
                loaded++;
            } catch (Exception ignored) {
                frames[i] = null;
            }
        }
        this.idleFrames = loaded > 0 ? frames : null;
    }
}
