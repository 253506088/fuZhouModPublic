package basicmod.relics;

import basicmod.monsters.GrandMageDad;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;

import static basicmod.BasicMod.makeID;

/**
 * 猪符咒
 */
public class PigTalisman extends BaseRelic {
    public static final String NAME = "PigTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.CLINK;

    private boolean activated = false;

    public PigTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public boolean canInteractInCombat() {
        return super.canInteractInCombat() && this.counter <= 0;
    }

    @Override
    public void atPreBattle() {
        this.counter = 0;
        this.activated = false;
        this.grayscale = false;
        stopPulse();
    }

    @Override
    public void atTurnStart() {
        this.activated = false;
        stopPulse();

        if (this.counter > 0) {
            this.counter--;
            if (this.counter == 0) {
                this.grayscale = false;
            }
        }

        if (TalismanLocator.isSemiAuto() && this.counter <= 0) {
            this.activated = true;
            this.beginLongPulse();
        }
    }

    @Override
    public void update() {
        super.update();
        if (canInteractInCombat() && this.counter <= 0) {
            if (this.hb.hovered && InputHelper.justClickedRight) {
                this.activated = !this.activated;
                if (this.activated) {
                    CardCrawlGame.sound.play("UI_CLICK_1");
                    this.beginLongPulse();
                } else {
                    CardCrawlGame.sound.play("UI_CLICK_2");
                    this.stopPulse();
                }
            }
        }
    }

    /**
     * 猪符咒主动效果入口：激发后打出下一张攻击牌时触发破甲/破人造制品，并进入冷却。
     */
    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        if (activated && c.type == AbstractCard.CardType.ATTACK) {
            boolean consumed = false;

            if (c.target == AbstractCard.CardTarget.ALL_ENEMY || c.target == AbstractCard.CardTarget.ALL) {
                for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                    if (!mo.isDeadOrEscaped()) {
                        if (removeBlockAndArtifact(mo)) {
                            consumed = true;
                        }
                    }
                }
            } else if (m != null) {
                consumed = removeBlockAndArtifact(m);
            }

            // 仅当本次攻击至少移除了“护甲/人造制品”其一时，才消耗激发并进入冷却
            if (consumed) {
                activated = false;
                this.counter = 3;
                this.flash();
                this.stopPulse();
                this.grayscale = true;
            }
        }
    }

    /**
     * 移除目标的格挡和人造制品（Artifact）。
     * @return 是否至少成功移除了“护甲/人造制品”其中之一
     */
    private boolean removeBlockAndArtifact(AbstractMonster m) {
        boolean removed = false;
        if (m.currentBlock > 0 && !isGrandMageChantShieldProtected(m)) {
            m.loseBlock();
            removed = true;
        }
        if (m.hasPower(ArtifactPower.POWER_ID)) {
            addToTop(new RemoveSpecificPowerAction(m, AbstractDungeon.player, ArtifactPower.POWER_ID));
            removed = true;
        }
        return removed;
    }

    private boolean isGrandMageChantShieldProtected(AbstractMonster m) {
        return m instanceof GrandMageDad && ((GrandMageDad) m).isChantShieldProtectedFromPigTalisman();
    }

    public void resetUsedThisTurn() {
        this.counter = 0;
        this.grayscale = false;
        this.activated = false;
        this.stopPulse();
        this.flash();
    }

    public void setActivated(boolean activate) {
        if (this.counter > 0) return;
        this.activated = activate;
        if (activate) {
            beginLongPulse();
        } else {
            stopPulse();
        }
    }

    @Override
    public void onUseCard(AbstractCard targetCard, com.megacrit.cardcrawl.actions.utility.UseCardAction useCardAction) {
        if (com.megacrit.cardcrawl.dungeons.AbstractDungeon.player != null
                && com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(RatTalisman.ID)) {
            if (targetCard.type == AbstractCard.CardType.ATTACK
                    && (targetCard.target == AbstractCard.CardTarget.ENEMY
                    || targetCard.target == AbstractCard.CardTarget.SELF_AND_ENEMY)
                    && useCardAction.target instanceof AbstractMonster) {

                AbstractMonster mainTarget = (AbstractMonster) useCardAction.target;
                java.util.ArrayList<AbstractMonster> monsters = com.megacrit.cardcrawl.dungeons.AbstractDungeon.getMonsters().monsters;
                int index = monsters.indexOf(mainTarget);
                java.util.ArrayList<AbstractMonster> adjacentMonsters = new java.util.ArrayList<>();

                if (index > 0) {
                    AbstractMonster left = monsters.get(index - 1);
                    if (!left.isDeadOrEscaped()) adjacentMonsters.add(left);
                }
                if (index < monsters.size() - 1) {
                    AbstractMonster right = monsters.get(index + 1);
                    if (!right.isDeadOrEscaped()) adjacentMonsters.add(right);
                }

                if (!adjacentMonsters.isEmpty()) {
                    this.flash();
                    int splashDamage = (int) (targetCard.damage * 0.15f);
                    if (splashDamage < 1) splashDamage = 1;

                    for (AbstractMonster splashTarget : adjacentMonsters) {
                        addToBot(new com.megacrit.cardcrawl.actions.common.DamageAction(
                                splashTarget,
                                new com.megacrit.cardcrawl.cards.DamageInfo(com.megacrit.cardcrawl.dungeons.AbstractDungeon.player, splashDamage, targetCard.damageTypeForTurn),
                                com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect.FIRE
                        ));
                    }
                }
            }
        }
    }

    @Override
    public void onVictory() {
        this.counter = -1;
        this.activated = false;
        this.grayscale = false;
        this.stopPulse();
    }

    @Override
    public String getUpdatedDescription() {
        return com.megacrit.cardcrawl.dungeons.AbstractDungeon.player != null
                && com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(RatTalisman.ID)
                ? (DESCRIPTIONS.length > 1 ? DESCRIPTIONS[1] : DESCRIPTIONS[0])
                : DESCRIPTIONS[0];
    }
}
