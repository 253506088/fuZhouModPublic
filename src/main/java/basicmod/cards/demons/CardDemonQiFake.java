package basicmod.cards.demons;

import basemod.abstracts.CustomCard;
import basicmod.cards.BaseCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

@basemod.AutoAdd.Ignore
public class CardDemonQiFake extends CustomCard {
    public static final String ID = basicmod.BasicMod.makeID("CardDemonQiFake");
    
    public CardDemonQiFake(AbstractCard sourceDemon, String description) {
        this(sourceDemon, sourceDemon.name + " 的魔气", description);
    }

    public CardDemonQiFake(AbstractCard sourceDemon, String displayName, String description) {
        this(displayName, (sourceDemon instanceof CustomCard) ? ((CustomCard) sourceDemon).textureImg : "status/beta", description);
        // 继承魔卡原有的大图渲染，防止某些图需要动态加载
        this.portrait = sourceDemon.portrait;
    }

    public CardDemonQiFake(String displayName, String imagePath, String description) {
        super(ID, displayName, imagePath, -2, BaseCard.normalizeCardDescriptionColors(description), CardType.POWER, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);
    }
    
    @Override
    public void upgrade() { }
    
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }
}
