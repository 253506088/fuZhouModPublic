package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.relics.RabbitTalisman;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 狡兔出击。
 * 阿福攻击牌，复制自身进弃牌堆，并与兔符咒和阿福连招联动。
 */
public class CardRabbitStrike extends BaseCard {
    public static final String ID = makeID("RabbitStrike");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.RARE,
            CardTarget.ENEMY,
            0
    );
    private static int lastTurn = -1;
    private static int energyGainCountThisTurn = 0;

    /**
     * 构造函数。
     */
    public CardRabbitStrike() {
        super(ID, info);
        tags.add(CustomTags.afu);
        setDamage(6, 3);
    }

    /**
     * 打出后造成伤害、复制自身，并按条件抽牌/回能。
     */
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        refreshTurnCounter();
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        addToBot(new MakeTempCardInDiscardAction(this.makeStatEquivalentCopy(), 1));
        if (p.hasRelic(RabbitTalisman.ID)) {
            addToBot(new DrawCardAction(p, 1));
        }
        if (isAfuComboActive() && energyGainCountThisTurn < 2) {
            energyGainCountThisTurn++;
            addToBot(new GainEnergyAction(1));
        }
    }

    private static void refreshTurnCounter() {
        int currentTurn = GameActionManager.turn;
        if (lastTurn != currentTurn || AbstractDungeon.actionManager == null || AbstractDungeon.actionManager.turnHasEnded) {
            lastTurn = currentTurn;
            energyGainCountThisTurn = 0;
        }
    }
}
