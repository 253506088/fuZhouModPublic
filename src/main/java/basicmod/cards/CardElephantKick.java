package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardElephantKick extends BaseCard {
    public static final String ID = makeID("ElephantKick");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.ENEMY,
            1);

    private static final int BLOCK = 7;
    private static final int UPG_BLOCK = 3;
    private static final int DAMAGE = 5;
    private static final int UPG_DAMAGE = 2;

    public CardElephantKick() {
        super(ID, info);
        setBlock(BLOCK, UPG_BLOCK);
        setDamage(DAMAGE, UPG_DAMAGE);
        tags.add(CustomTags.afu);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, block));

        if (m != null && isAttacking(m)) {
            addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        }
    }

    private boolean isAttacking(AbstractMonster m) {
        return m.intent == AbstractMonster.Intent.ATTACK || 
               m.intent == AbstractMonster.Intent.ATTACK_BUFF || 
               m.intent == AbstractMonster.Intent.ATTACK_DEBUFF || 
               m.intent == AbstractMonster.Intent.ATTACK_DEFEND;
    }
}
