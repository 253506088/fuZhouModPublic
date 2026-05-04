package basicmod.cards;

import basicmod.BasicMod;
import basicmod.actions.SafeApplyPowerAction;
import basicmod.enums.CharacterEnums;
import basicmod.powers.NothingLackingPower;
import basicmod.util.CardStats;
import basicmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class NothingLackingCard extends BaseCard {
    public static final String ID = BasicMod.makeID(NothingLackingCard.class.getSimpleName());
    private static final String BASE_DESCRIPTION = "固有 NL 每打出1张牌，抽1张牌。获得 6 点 [E] ，6 层 *力量 与 6 层 *敏捷 。";
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.POWER,
            CardRarity.SPECIAL, // 特殊稀有度，不会在常规关卡掉落
            CardTarget.SELF,
            0 // 0 费
    );

    public NothingLackingCard() {
        super(ID, info);
        this.isInnate = true; // 固有
        this.rawDescription = BASE_DESCRIPTION;
        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        CardCrawlGame.sound.play(Sounds.NOTHING_LACKING_CARD);
        // 使用卡牌获得终极力量 Power
        boolean drawOnePerCardOnly = !this.upgraded;
        int energy = this.upgraded ? 999 : 6;
        int stats = this.upgraded ? 99 : 6;
        addToBot(new SafeApplyPowerAction(p, p, new NothingLackingPower(p, energy, stats, drawOnePerCardOnly), 1));
        addToBot(new SafeApplyPowerAction(p, p, new StrengthPower(p, stats), stats));
        addToBot(new SafeApplyPowerAction(p, p, new DexterityPower(p, stats), stats));
        addToBot(new GainEnergyAction(energy));
    }

    public void downgradeForGrandMageDad() {
        if (!this.upgraded) {
            return;
        }
        this.upgraded = false;
        this.timesUpgraded = 0;
        this.name = this.originalName;
        this.rawDescription = BASE_DESCRIPTION;
        initializeTitle();
        initializeDescription();
    }
}
