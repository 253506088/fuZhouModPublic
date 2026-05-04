package basicmod.cards.demons;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.BurningPower;
import basicmod.powers.SoakedPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;

public class CardWaterDemonQi extends BaseCard {
    public static final String ID = makeID("CardWaterDemonQi");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.SPECIAL,
            CardTarget.ALL_ENEMY,
            1
    );

    public CardWaterDemonQi() {
        super(ID, info);
        setDamage(4);
        setMagic(2); 
        this.isMultiDamage = true;
        tags.add(CustomTags.EIGHT_DEMONS);
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            super.upgrade(); 
            upgradeDamage(2);
            upgradeMagicNumber(1);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 全体伤害
        addToBot(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        
        // 遍历所有敌人进行效果判定
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                    if (!mo.isDeadOrEscaped()) {
                        boolean hadSoaked = mo.hasPower(basicmod.powers.SoakedPower.POWER_ID) || mo.hasPower(basicmod.powers.FrostbitePower.POWER_ID) || mo.hasPower(basicmod.powers.FrostHellPower.POWER_ID);
                        
                        // 如果目标有基础冻伤或进化状态冰狱，继续叠加基础冻伤，否则给予潮湿
                        if (mo.hasPower(basicmod.powers.FrostbitePower.POWER_ID) || mo.hasPower(basicmod.powers.FrostHellPower.POWER_ID)) {
                            addToTop(new com.megacrit.cardcrawl.actions.common.ApplyPowerAction(mo, p, new basicmod.powers.FrostbitePower(mo, p, magicNumber), magicNumber));
                        } else {
                            addToTop(new com.megacrit.cardcrawl.actions.common.ApplyPowerAction(mo, p, new basicmod.powers.SoakedPower(mo, p, magicNumber), magicNumber));
                        }

                        // 计算金币概率
                        int maxRoll;
                        if (upgraded) {
                            maxRoll = hadSoaked ? 5 : 6;
                        } else {
                            maxRoll = hadSoaked ? 6 : 8;
                        }

                        int roll = AbstractDungeon.miscRng.random(1, maxRoll);
                        if (roll == 1) {
                            int goldGain = AbstractDungeon.miscRng.random(5, 20); // 削弱：5-20
                            p.gainGold(goldGain);
                            CardCrawlGame.sound.play("GOLD_JINGLE");
                            for (int i = 0; i < goldGain; ++i) {
                                AbstractDungeon.effectList.add(new GainPennyEffect(p, mo.hb.cX, mo.hb.cY, p.hb.cX, p.hb.cY, true));
                            }
                        }
                    }
                }
                this.isDone = true;
            }
        });
    }
}
