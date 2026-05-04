package basicmod.cards.demons;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;

public class CardMountainDemonQi extends BaseCard {
    public static final String ID = makeID("CardMountainDemonQi");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.SPECIAL,
            CardTarget.ENEMY,
            1
    );

    public CardMountainDemonQi() {
        super(ID, info);
        setMagic(1, 1); // 基础增加1点生命，升级提升到2点
        this.baseDamage = 0;
        tags.add(CustomTags.EIGHT_DEMONS);
    }

    @Override
    public void applyPowers() {
        this.baseDamage = AbstractDungeon.player.currentBlock;
        super.applyPowers();
        this.rawDescription = cardStrings.DESCRIPTION;
        this.rawDescription = this.rawDescription + cardStrings.EXTENDED_DESCRIPTION[0]; // 加上"造成 !D! 点伤害"的提示
        this.initializeDescription();
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        this.baseDamage = AbstractDungeon.player.currentBlock;
        super.calculateCardDamage(m);
        this.rawDescription = cardStrings.DESCRIPTION;
        this.rawDescription = this.rawDescription + cardStrings.EXTENDED_DESCRIPTION[0];
        this.initializeDescription();
    }

    @Override
    public void onMoveToDiscard() {
        this.rawDescription = cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (m != null) {
                    // 原版贪婪之咬杀敌加生命值的动画和音效
                    AbstractDungeon.effectList.add(new BiteEffect(m.hb.cX, m.hb.cY - 40.0F * Settings.scale, Settings.GOLD_COLOR.cpy()));
                    
                    DamageInfo info = new DamageInfo(p, damage, damageTypeForTurn);
                    m.damage(info);
                    
                    if ((m.isDying || m.currentHealth <= 0) && !m.halfDead && !m.hasPower("Minion")) {
                        p.increaseMaxHp(magicNumber, true);
                    }
                }
                this.isDone = true;
            }
        });
    }
}
