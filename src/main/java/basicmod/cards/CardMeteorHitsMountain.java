package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;

public class CardMeteorHitsMountain extends BaseCard {
    public static final String ID = makeID("MeteorHitsMountain");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            1);

    private static final int DAMAGE = 2;
    private static final int UPG_DAMAGE = 1;
    private static final int HITS = 4;
    private static final int BONUS = 4;
    private static final int UPG_BONUS = 2;

    public CardMeteorHitsMountain() {
        super(ID, info);
        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(HITS);
        setCustomVar("B", BONUS, UPG_BONUS);
        tags.add(CustomTags.afu);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < magicNumber; i++) {
            addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        }

        // 连招检查：如果本回合已打出的上一张牌是 阿福招式
        if (isAfuComboActive()) {
            addToBot(new ApplyPowerAction(p, p, new VigorPower(p, 4), 4));
        }
    }

    @Override
    public void triggerOnGlowCheck() {
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        if (isAfuComboActive()) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
    }
}
