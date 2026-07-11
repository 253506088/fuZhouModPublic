package basicmod.cards.fusion;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.relics.DogTalisman;
import basicmod.relics.PigTalisman;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 狗猪同归
 * 2费 普通 攻击
 * 造成 D 点伤害。若持有狗符咒或猪符咒，改为对所有敌人造成伤害。
 */
public class DogPigFusion extends BaseCard {
    public static final String ID = makeID("DogPigFusion");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.COMMON,
            CardTarget.ENEMY,
            2);

    private static final int DAMAGE = 10;
    private static final int UPG_DAMAGE = 4;

    public DogPigFusion() {
        super(ID, info);
        tags.add(CustomTags.FUSION);
        setDamage(DAMAGE, UPG_DAMAGE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (AbstractDungeon.player.hasRelic(DogTalisman.ID)
                || AbstractDungeon.player.hasRelic(PigTalisman.ID)) {
            addToBot(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn,
                    AbstractGameAction.AttackEffect.FIRE));
        } else {
            addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                    AbstractGameAction.AttackEffect.SLASH_HEAVY));
        }
    }
}
