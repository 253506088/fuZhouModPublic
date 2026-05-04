package basicmod.cards.skills;

import basicmod.cards.BaseCard;
import basicmod.cards.talismans.*;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class JadeStealsTalisman extends BaseCard {
    public static final String ID = makeID(JadeStealsTalisman.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.NONE,
            1
    );

    public JadeStealsTalisman() {
        super(ID, info);
        setCostUpgrade(0);
        setExhaust(true);
        tags.add(CustomTags.TEAM_JACKIE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                ArrayList<AbstractCard> talismans = new ArrayList<>();
                talismans.add(new RatTalismanCard());
                talismans.add(new OxTalismanCard());
                talismans.add(new TigerTalismanAttackCard());
                talismans.add(new TigerTalismanSkillCard());
                talismans.add(new RabbitTalismanCard());
                talismans.add(new DragonTalismanCard());
                talismans.add(new SnakeTalismanCard());
                talismans.add(new HorseTalismanCard());
                talismans.add(new GoatTalismanCard());
                talismans.add(new MonkeyTalismanCard());
                talismans.add(new RoosterTalismanCard());
                talismans.add(new DogTalismanCard());
                talismans.add(new PigTalismanCard());

                AbstractCard c = talismans.get(AbstractDungeon.cardRandomRng.random(talismans.size() - 1)).makeCopy();
                c.setCostForTurn(0);
                addToTop(new MakeTempCardInHandAction(c, 1));
                this.isDone = true;
            }
        });
    }
}
