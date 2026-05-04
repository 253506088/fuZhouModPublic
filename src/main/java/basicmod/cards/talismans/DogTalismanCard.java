package basicmod.cards.talismans;

import basicmod.cards.BaseCard;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import basicmod.powers.DogTalismanPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import basicmod.enums.CharacterEnums;

public class DogTalismanCard extends BaseCard {
    public static final String ID = makeID("DogTalismanCard");
    private static final int BASE_DURATION = 5;
    private static final int UPGRADE_DURATION = 8;
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.SELF,
            1
    );

    public DogTalismanCard() {
        super(ID, info);
        tags.add(CustomTags.TALISMAN_CARD);
        // Revive amount for each fatal-prevention trigger.
        setMagic(10);
        this.exhaust = true;
        this.upgradesDescription = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        final int reviveHeal = this.magicNumber;
        final int effectTurns = this.upgraded ? UPGRADE_DURATION : BASE_DURATION;
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractPower existing = p.getPower(DogTalismanPower.POWER_ID);
                if (existing instanceof DogTalismanPower) {
                    existing.flash();
                    ((DogTalismanPower) existing).addStack(effectTurns);
                } else {
                    addToTop(new ApplyPowerAction(p, p, new DogTalismanPower(p, reviveHeal, effectTurns), 1));
                }
                this.isDone = true;
            }
        });
    }
}
