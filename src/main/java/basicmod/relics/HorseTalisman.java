package basicmod.relics;

import basicmod.cards.BaseCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;
import basicmod.powers.ShenZhuStatuePower;
import com.megacrit.cardcrawl.core.AbstractCreature;

import static basicmod.BasicMod.makeID;

public class HorseTalisman extends BaseRelic implements com.evacipated.cardcrawl.mod.stslib.relics.OnReceivePowerRelic {
    public static final String NAME = "HorseTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;
    private static final int REMOVE_DEBUFF_COOLDOWN = 2;

    private boolean removedDebuffThisCombat = false;
    private boolean usedThisTurn = false;
    private boolean selectingPower = false;
    private boolean activated = false;
    private float autoTriggerTimer = 0f;

    public HorseTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atPreBattle() {
        removedDebuffThisCombat = false;
        this.usedThisTurn = false;
        this.selectingPower = false;
        this.activated = false;
        this.autoTriggerTimer = 0f;
        this.counter = 0;
        this.grayscale = false;
        stopPulse();
    }

    @Override
    public void atTurnStart() {
        this.usedThisTurn = false;
        this.activated = false;
        this.autoTriggerTimer = 0f;
        stopPulse();
        if (this.counter > 0) {
            this.counter--;
            if (this.counter == 0) {
                this.grayscale = false;
            }
        }

        // 半自动自启逻辑：自动挡下，若当前可用且身上有 Debuff，则自动弹出移除窗口
        if (TalismanLocator.isSemiAuto() && this.counter <= 0) {
            boolean hasDebuff = false;
            for (AbstractPower p : AbstractDungeon.player.powers) {
                if (p.type == AbstractPower.PowerType.DEBUFF && !p.ID.equals(ShenZhuStatuePower.POWER_ID)) {
                    hasDebuff = true;
                    break;
                }
            }
            if (hasDebuff) {
                this.activated = true;
                this.beginLongPulse();
                this.autoTriggerTimer = 0.6f; // 0.6秒倒计时后自动弹出
            }
        }
    }
    
    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature source) {
        // 鼠+马觉醒已改为战后回血强化，不再免疫负面效果
        return true;
    }
    
    @Override
    public int onReceivePowerStacks(AbstractPower power, AbstractCreature source, int stackAmount) {
        // 鼠+马觉醒已改为战后回血强化，不再免疫负面效果
        return stackAmount;
    }

    @Override
    public void update() {
        super.update();

        // 1. 处理右键点击：切换预备状态
        if (canInteractInCombat() && !this.usedThisTurn && this.counter <= 0) {
            if (this.hb.hovered && InputHelper.justClickedRight) {
                this.activated = !this.activated;
                if (this.activated) {
                    CardCrawlGame.sound.play("UI_CLICK_1");
                    this.beginLongPulse();
                    this.autoTriggerTimer = 0f; // 手动开启不带倒计时，直接等待手动触发或自动弹出？
                    // 逻辑统一起见：手动点开后，我们让它也“预备”着。
                    // 但是马符咒原本是“一点就开”。为了保持手感：
                    // 如果是手动点开，我们直接进入选择界面。
                    openDebuffSelection();
                    this.activated = false; // 弹出后即关闭预备
                    return;
                } else {
                    CardCrawlGame.sound.play("UI_CLICK_2");
                    this.stopPulse();
                    this.autoTriggerTimer = 0f;
                }
            }
        }

        // 2. 半自动倒计时触发
        if (this.activated && this.autoTriggerTimer > 0) {
            this.autoTriggerTimer -= com.badlogic.gdx.Gdx.graphics.getDeltaTime();
            if (this.autoTriggerTimer <= 0) {
                this.autoTriggerTimer = 0f;
                openDebuffSelection();
                this.activated = false;
                this.stopPulse();
            }
        }

        // 3. 处理网格选择后的移除逻辑
        if (this.selectingPower) {
            // 情况 A: 玩家选了卡
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                AbstractCard selectedCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                this.selectingPower = false;
                this.usedThisTurn = true;
                this.removedDebuffThisCombat = true; // 真正移除了才取消回血奖励
                this.counter = REMOVE_DEBUFF_COOLDOWN;
                this.grayscale = true;
                this.activated = false;
                this.autoTriggerTimer = 0f;
                this.stopPulse();
                this.flash();

                if (selectedCard instanceof PowerPreviewCard) {
                    String powerId = ((PowerPreviewCard) selectedCard).powerId;
                    addToBot(new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, powerId));
                }
            }
            // 情况 B: 玩家取消了选择（通过取消按钮或 ESC）
            else if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.GRID) {
                this.selectingPower = false;
                // 不设置 usedThisTurn，不设置 removedDebuffThisCombat
            }
        }
    }

    private void openDebuffSelection() {
        // 查找所有负面状态
        CardGroup debuffs = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractPower p : AbstractDungeon.player.powers) {
            // 仅显示负面状态，且排除掉“圣主石像”这个核心机制（隐藏不让移除）
            if (p.type == AbstractPower.PowerType.DEBUFF && !p.ID.equals(ShenZhuStatuePower.POWER_ID)) {
                debuffs.addToBottom(new PowerPreviewCard(p));
            }
        }

        if (!debuffs.isEmpty()) {
            this.selectingPower = true;
            // 使用 7 参数多载版本：open(group, numCards, tipMsg, forUpgrade, forTransform, canCancel, forPurge)
            AbstractDungeon.gridSelectScreen.open(debuffs, 1, "选择要移除的状态", false, false, true, false);
            AbstractDungeon.overlayMenu.cancelButton.show("取消");
        }
    }

    /**
     * 重置马符咒使用标记与冷却（供兔符咒刷新时调用）。
     */
    public void resetUsedThisTurn() {
        this.usedThisTurn = false;
        this.counter = 0;
        this.grayscale = false;
        this.flash();
    }

    // 内部类：用于在 GridSelectScreen 中展示状态的“虚拟卡片”
    private static class PowerPreviewCard extends AbstractCard {
        public String powerId;

        public PowerPreviewCard(AbstractPower power) {
            super("PowerPreview", power.name, null, -2, formatDescription(power), CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);
            this.powerId = power.ID;
            this.baseDamage = 0;
            this.baseBlock = 0;
            this.initializeDescription();
        }

        private static String formatDescription(AbstractPower power) {
            String desc = power.description;
            if (desc == null) return "";

            // 1. 统一使用卡片描述颜色兼容转换
            desc = BaseCard.normalizeCardDescriptionColors(desc);

            // 2. 针对常见状态进行描述美化（确保标签闭合）
            if (power.ID.equals("Vulnerable")) {
                desc = "受到来自 [#efc100]攻击[] 的伤害增加 [#71b1d1]50%[] 。 NL 持续 [#71b1d1]" + power.amount + "[] 回合。";
            } else if (power.ID.equals("Weakened")) {
                desc = "造成 [#efc100]攻击[] 的伤害减少 [#71b1d1]25%[] 。 NL 持续 [#71b1d1]" + power.amount + "[] 回合。";
            } else if (power.ID.equals("Frail")) {
                desc = "从卡牌获得的 [#efc100]格挡[] 减少 [#71b1d1]25%[] 。 NL 持续 [#71b1d1]" + power.amount + "[] 回合。";
            }

            // 3. 处理描述中的占位符闭合
            // 注意：卡片引擎认 * 关键字高亮，不认未闭合的色彩标签
            
            // 4. 清理多余空格
            desc = desc.replaceAll("\\s+", " ").trim();

            return desc;
        }

        @Override public void upgrade() {}
        @Override public void use(com.megacrit.cardcrawl.characters.AbstractPlayer p, com.megacrit.cardcrawl.monsters.AbstractMonster m) {}
        @Override public AbstractCard makeCopy() { return null; }
    }

    public void setActivated(boolean activate) {
        if (this.usedThisTurn) return;
        if (this.counter > 0) return;
        
        this.activated = activate;
        if (activate) {
            // 检测是否有 Debuff，有才开启，否则不开启（避免无用悬浮窗）
            boolean hasDebuff = false;
            for (com.megacrit.cardcrawl.powers.AbstractPower p : com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.powers) {
                if (p.type == com.megacrit.cardcrawl.powers.AbstractPower.PowerType.DEBUFF && !p.ID.equals(basicmod.powers.ShenZhuStatuePower.POWER_ID)) {
                    hasDebuff = true;
                    break;
                }
            }
            if (hasDebuff) {
                beginLongPulse();
                this.autoTriggerTimer = 0.6f;
            } else {
                this.activated = false;
            }
        } else {
            stopPulse();
            this.autoTriggerTimer = 0f;
        }
    }

    @Override
    public void onVictory() {
        // 战斗结束时，判断是否整场战斗中从未成功移除过负面状态
        if (!removedDebuffThisCombat) {
            this.flash();
            // 鼠+马觉醒：未触发过移除时，战后回血从 20% 强化为 30%
            int healPercent = (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(RatTalisman.ID)) ? 30 : 20;
            AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth * healPercent / 100, true);
        }
        removedDebuffThisCombat = false; // 重置标记位，迎接下次战斗
    }

    @Override
    public String getUpdatedDescription() {
        return com.megacrit.cardcrawl.dungeons.AbstractDungeon.player != null && com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(RatTalisman.ID) ? (DESCRIPTIONS.length > 1 ? DESCRIPTIONS[1] : DESCRIPTIONS[0]) : DESCRIPTIONS[0];
    }
}
