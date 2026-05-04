package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class CardOneTigerTwoSheep extends BaseCard {
    public static final String ID = makeID("OneTigerTwoSheep");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.COMMON,
            CardTarget.ENEMY,
            1);

    private static final int DAMAGE = 5;
    private static final int UPG_DAMAGE = 2;
    private static final int STR_GAIN = 1;
    private static final int UPG_STR_GAIN = 1;

    public CardOneTigerTwoSheep() {
        super(ID, info);
        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(STR_GAIN, UPG_STR_GAIN);
        tags.add(CustomTags.afu);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 第一次打击
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        // 第二次打击，并带上击杀判定逻辑
        addToBot(new OneTigerTwoSheepAction(m, new DamageInfo(p, damage, damageTypeForTurn), magicNumber));
    }

    // 内部类实现的 Action
    private static class OneTigerTwoSheepAction extends AbstractGameAction {
        private DamageInfo info;
        private int strGain;

        public OneTigerTwoSheepAction(AbstractMonster target, DamageInfo info, int strGain) {
            this.target = target;
            this.info = info;
            this.strGain = strGain;
            this.actionType = ActionType.DAMAGE;
        }

        @Override
        public void update() {
            if (this.target != null) {
                this.target.damage(this.info);
                if ((this.target.isDying || this.target.currentHealth <= 0) && !this.target.halfDead) {
                    addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, this.strGain), this.strGain));
                }
            }
            this.isDone = true;
        }
    }
}
