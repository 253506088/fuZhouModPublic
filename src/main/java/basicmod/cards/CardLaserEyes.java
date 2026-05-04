package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.relics.PigTalisman;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class CardLaserEyes extends BaseCard {
    public static final String ID = makeID("LaserEyes");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.COMMON,
            CardTarget.ENEMY,
            0);

    private static final int DAMAGE = 4;
    private static final int UPG_DAMAGE = 2;
    private static final int VULN = 1;

    public CardLaserEyes() {
        super(ID, info);
        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(VULN);
        tags.add(CustomTags.afu);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (p.hasRelic(PigTalisman.ID)) {
            // 群体伤害
            addToBot(new DamageAllEnemiesAction(p, multiDamage, damageTypeForTurn, AbstractGameAction.AttackEffect.FIRE));
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped()) {
                    addToBot(new ApplyPowerAction(mo, p, new VulnerablePower(mo, magicNumber, false), magicNumber));
                }
            }
        } else {
            // 单体伤害
            addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
            addToBot(new ApplyPowerAction(m, p, new VulnerablePower(m, magicNumber, false), magicNumber));
        }
    }

    @Override
    public void applyPowers() {
        if (AbstractDungeon.player.hasRelic(PigTalisman.ID)) {
            this.isMultiDamage = true;
            this.target = CardTarget.ALL_ENEMY;
        } else {
            this.isMultiDamage = false;
            this.target = CardTarget.ENEMY;
        }
        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        if (AbstractDungeon.player.hasRelic(PigTalisman.ID)) {
            this.isMultiDamage = true;
            this.target = CardTarget.ALL_ENEMY;
        } else {
            this.isMultiDamage = false;
            this.target = CardTarget.ENEMY;
        }
        super.calculateCardDamage(m);
    }
}
