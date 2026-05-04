package basicmod.cards;

import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.BlackHandPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardBlackHandDistrict13Insider extends BaseCard {
    public static final String ID = makeID("BlackHandDistrict13Insider");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            1
    );

    public CardBlackHandDistrict13Insider() {
        super(ID, info);
        setMagic(4, 2);
        tags.add(CustomTags.blackhand);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                int previewCount = Math.min(magicNumber, p.drawPile.size());
                if (previewCount <= 0) {
                    this.isDone = true;
                    return;
                }

                final int originalDiscardSize = p.discardPile.size();
                addToTop(new AbstractGameAction() {
                    @Override
                    public void update() {
                        int discarded = Math.max(0, p.discardPile.size() - originalDiscardSize);
                        if (discarded > 0) {
                            addToTop(new GainBlockAction(p, p, discarded * 2));
                        }

                        int kept = Math.max(0, previewCount - discarded);
                        if (kept > 0) {
                            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                                if (!mo.isDeadOrEscaped()) {
                                    BlackHandPower.apply(mo, p, kept);
                                }
                            }
                        }
                        this.isDone = true;
                    }
                });
                addToTop(new ScryAction(previewCount));
                this.isDone = true;
            }
        });
    }
}
