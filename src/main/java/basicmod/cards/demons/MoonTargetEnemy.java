package basicmod.cards.demons;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MoonTargetEnemy extends BaseCard {
    public static final String ID = makeID("MoonTargetEnemy");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.ENEMY,
            0
    );

    public MoonTargetEnemy() {
        super(ID, info);
        this.exhaust = true;
        this.isEthereal = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m == null) {
            CardMoonDemonQi.logger.info("【月之恶魔】MoonTargetEnemy.use 被调用，但目标怪物 (m) 为 NULL！索敌环节异常。");
        } else {
            CardMoonDemonQi.logger.info(String.format("【月之恶魔】MoonTargetEnemy.use 捕获到目标: %s, ID: %s, HP: %d/%d, isDead: %b", 
                m.name, m.id, m.currentHealth, m.maxHealth, m.isDead));
        }
        CardMoonDemonQi.invertStats(m);
    }

}
