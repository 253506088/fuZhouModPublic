package basicmod.cards.fusion;

import basicmod.cards.BaseCard;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.relics.OxTalisman;
import basicmod.relics.RatTalisman;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

/**
 * 鼠牛合击
 * 2费 普通 攻击
 * 造成 D 点伤害。若持有鼠符咒或牛符咒，获得 M 点力量。
 */
public class RatOxFusion extends BaseCard {
    public static final String ID = makeID("RatOxFusion");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.COMMON,
            CardTarget.ENEMY,
            2);

    private static final int DAMAGE = 8;
    private static final int UPG_DAMAGE = 3;

    public RatOxFusion() {
        super(ID, info);
        tags.add(CustomTags.FUSION);
        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(1, 1);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        if (AbstractDungeon.player.hasRelic(RatTalisman.ID)
                || AbstractDungeon.player.hasRelic(OxTalisman.ID)) {
            addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));
        }
    }
}
