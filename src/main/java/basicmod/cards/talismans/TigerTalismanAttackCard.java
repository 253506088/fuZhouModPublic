package basicmod.cards.talismans;

import basicmod.cards.BaseCard;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import basicmod.enums.CharacterEnums;

public class TigerTalismanAttackCard extends BaseCard {
    public static final String ID = makeID("TigerTalismanAttackCard");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            2
    );

    public TigerTalismanAttackCard() {
        super(ID, info);
        tags.add(CustomTags.TALISMAN_CARD);
        setDamage(6);
        setMagic(3); // Heal amount
        setCostUpgrade(1);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null) {
            float playerHPPercent = (float) p.currentHealth / p.maxHealth;
            float monsterHPPercent = (float) m.currentHealth / m.maxHealth;

            if (monsterHPPercent > playerHPPercent) {
                // Goal: 6 damage + 3 heal
                addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                addToBot(new HealAction(p, p, magicNumber));
            } else {
                // Goal: 6 damage + 30% missing HP
                int missingHP = m.maxHealth - m.currentHealth;
                int bonusDamage = (int) (missingHP * 0.3f);
                int totalDamage = damage + bonusDamage;
                addToBot(new DamageAction(m, new DamageInfo(p, totalDamage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HEAVY));
            }
        }
    }
}
