package basicmod.cards.skills;

import basemod.ReflectionHacks;
import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CloseAtHand extends BaseCard {
    public static final String ID = makeID(CloseAtHand.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.SELF,
            2
    );

    public CloseAtHand() {
        super(ID, info);
        setCostUpgrade(1); // upgrade cost 2 -> 1
        tags.add(CustomTags.TEAM_JACKIE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new LoseHPAction(p, p, 6));

        int totalIntentDmg = 0;
        for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
            if (!mo.isDeadOrEscaped()) {
                totalIntentDmg += getIntentTotalDamage(mo);
            }
        }

        addToBot(new GainBlockAction(p, p, totalIntentDmg + 1));
    }

    private int getIntentTotalDamage(AbstractMonster mo) {
        if (!isAttackIntent(mo) || mo.getIntentDmg() < 0) {
            return 0;
        }

        int hitCount = 1;
        try {
            Boolean isMulti = ReflectionHacks.getPrivate(mo, AbstractMonster.class, "isMultiDmg");
            Integer multiAmt = ReflectionHacks.getPrivate(mo, AbstractMonster.class, "intentMultiAmt");
            if (Boolean.TRUE.equals(isMulti) && multiAmt != null && multiAmt > 1) {
                hitCount = multiAmt;
            }
        } catch (Exception ignored) {
            // Fallback: treat as single-hit if reflection fails.
        }

        return mo.getIntentDmg() * hitCount;
    }

    private boolean isAttackIntent(AbstractMonster mo) {
        return mo.intent == AbstractMonster.Intent.ATTACK
                || mo.intent == AbstractMonster.Intent.ATTACK_BUFF
                || mo.intent == AbstractMonster.Intent.ATTACK_DEBUFF
                || mo.intent == AbstractMonster.Intent.ATTACK_DEFEND;
    }
}
