package basicmod.enums;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class CharacterEnums {
    @SpireEnum
    public static AbstractPlayer.PlayerClass SHENGZHU;

    @SpireEnum(name = "SHENGZHU_COLOR")
    public static AbstractCard.CardColor SHENGZHU_COLOR;

    @SpireEnum(name = "SHENGZHU_COLOR")
    @SuppressWarnings("unused")
    public static com.megacrit.cardcrawl.helpers.CardLibrary.LibraryType LIBRARY_COLOR;
}
