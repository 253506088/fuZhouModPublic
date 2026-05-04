package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.relics.RoosterTalisman;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardCrowFlying extends BaseCard {
    public static final String ID = makeID("CrowFlying");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            2);

    private static final int DAMAGE = 18;
    private static final int UPG_DAMAGE = 6;

    public CardCrowFlying() {
        super(ID, info);
        setDamage(DAMAGE, UPG_DAMAGE);
        tags.add(CustomTags.afu);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY));
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        applyRoosterCostReduction();
    }

    @Override
    public void triggerWhenDrawn() {
        super.triggerWhenDrawn();
        applyRoosterCostReduction();
    }

    private void applyRoosterCostReduction() {
        if (AbstractDungeon.player == null || !AbstractDungeon.player.hasRelic(RoosterTalisman.ID)) {
            return;
        }
        if (this.isCostModifiedForTurn && this.costForTurn == 0) {
            return;
        }
        if (this.cost > 1) {
            this.setCostForTurn(this.cost - 1);
            this.isCostModified = true;
        } else if (this.cost == 1) {
            this.setCostForTurn(0);
            this.isCostModified = true;
        }
    }
}
