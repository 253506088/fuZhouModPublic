package basicmod.cards.talismans;

import basicmod.cards.BaseCard;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import basicmod.enums.CharacterEnums;

import java.util.ArrayList;

public class GoatTalismanCard extends BaseCard {
    public static final String ID = makeID("GoatTalismanCard");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.ENEMY,
            2
    );

    public GoatTalismanCard() {
        super(ID, info);
        tags.add(CustomTags.TALISMAN_CARD);
        this.baseMagicNumber = this.magicNumber = 2;
        this.exhaust = true;
        setCostUpgrade(1);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null) {
            ArrayList<AbstractPower> powersToDouble = new ArrayList<>();
            for (AbstractPower power : m.powers) {
                if (power.type == AbstractPower.PowerType.DEBUFF) {
                    powersToDouble.add(power);
                }
            }

            // 1. 开启倍增处理标志
            addToBot(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
                @Override
                public void update() {
                    basicmod.util.MechanicsContext.isProcessingDoubling = true;
                    this.isDone = true;
                }
            });

            // 2. 依次叠加负面层数
            for (AbstractPower power : powersToDouble) {
                if (power.amount != 0) {
                    addToBot(new ApplyPowerAction(m, p, power, power.amount));
                }
            }

            // 3. 恢复标志位
            addToBot(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
                @Override
                public void update() {
                    basicmod.util.MechanicsContext.isProcessingDoubling = false;
                    this.isDone = true;
                }
            });
        }
    }
}
