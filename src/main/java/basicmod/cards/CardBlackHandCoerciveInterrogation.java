package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.BlackHandPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class CardBlackHandCoerciveInterrogation extends BaseCard {
    public static final String ID = makeID("BlackHandCoerciveInterrogation");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.ENEMY,
            1
    );

    public CardBlackHandCoerciveInterrogation() {
        super(ID, info);
        tags.add(CustomTags.blackhand);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int x = BlackHandPower.getAmount(m) / 2;
        if (this.upgraded) {
            x += 1;
        }

        if (x > 0) {
            addToBot(new ApplyPowerAction(m, p, new StrengthPower(m, -x), -x));
            // Align with vanilla temporary Strength-loss pattern:
            // if Artifact blocks the debuff, do not queue the Strength refund.
            if (!m.hasPower(ArtifactPower.POWER_ID)) {
                addToBot(new ApplyPowerAction(m, p, new GainStrengthPower(m, x), x));
            }
        }
        addToBot(new ApplyPowerAction(m, p, new WeakPower(m, 1, false), 1));
    }
}
