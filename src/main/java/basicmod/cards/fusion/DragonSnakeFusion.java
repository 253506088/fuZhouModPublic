package basicmod.cards.fusion;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.relics.DragonTalisman;
import basicmod.relics.SnakeTalisman;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 龙蛇双影
 * 1费 普通 攻击
 * 造成 D 点伤害。若持有龙符咒或蛇符咒，再造成 1 次伤害。
 */
public class DragonSnakeFusion extends BaseCard {
    public static final String ID = makeID("DragonSnakeFusion");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.COMMON,
            CardTarget.ENEMY,
            1);

    private static final int DAMAGE = 6;
    private static final int UPG_DAMAGE = 3;

    public DragonSnakeFusion() {
        super(ID, info);
        tags.add(CustomTags.FUSION);
        setDamage(DAMAGE, UPG_DAMAGE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        if (AbstractDungeon.player.hasRelic(DragonTalisman.ID)
                || AbstractDungeon.player.hasRelic(SnakeTalisman.ID)) {
            addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                    AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        }
    }
}
