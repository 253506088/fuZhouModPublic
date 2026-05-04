package basicmod.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

public class DragonTalisman extends BaseRelic {
    public static final String NAME = "DragonTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.HEAVY;

    public DragonTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void onEquip() {
        this.counter = 0;
    }

    @Override
    public void onUseCard(AbstractCard targetCard, UseCardAction useCardAction) {
        this.counter++; // 任意卡牌打出，计数器+1
        if (this.counter >= 10) {
            this.counter = 0; // 满10次后，计数器重置
            this.flash(); // 遗物闪烁提示触发
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            
            // 基础伤害为 5
            int finalDamage = 5;
            
            // 只有在拥有鼠符咒（圣主真身）时才吃力量加成
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(RatTalisman.ID)) {
                if (AbstractDungeon.player.hasPower(com.megacrit.cardcrawl.powers.StrengthPower.POWER_ID)) {
                    finalDamage += AbstractDungeon.player.getPower(com.megacrit.cardcrawl.powers.StrengthPower.POWER_ID).amount;
                }
            }
            
            if (finalDamage < 0) finalDamage = 0; // 防御极端负力量

            // 对所有敌人造成荆棘类型伤害
            addToBot(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(finalDamage, true),
                    DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.FIRE));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return com.megacrit.cardcrawl.dungeons.AbstractDungeon.player != null && com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(RatTalisman.ID) ? (DESCRIPTIONS.length > 1 ? DESCRIPTIONS[1] : DESCRIPTIONS[0]) : DESCRIPTIONS[0];
    }
}
