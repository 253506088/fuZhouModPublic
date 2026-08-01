package basicmod.cards;

import basemod.BaseMod;
import basemod.abstracts.CustomCard;
import basemod.abstracts.DynamicVariable;
import basemod.helpers.TooltipInfo;
import basicmod.BasicMod;
import basicmod.util.CardStats;
import basicmod.util.KeywordInfo;
import basicmod.util.TriFunction;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import basicmod.enums.CustomTags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static basicmod.util.GeneralUtils.removePrefix;
import static basicmod.util.TextureLoader.getCardTextureString;


/**
 * 卡牌基类。
 * 提供卡牌的基础功能，包括升级逻辑、自定义变量、关键字提示等。
 */
public abstract class BaseCard extends CustomCard {
    /** 自定义动态变量映射表 */
    final private static Map<String, DynamicVariable> customVars = new HashMap<>();
    /** 旧版颜色标记正则表达式 */
    private static final Pattern LEGACY_CARD_COLOR_PATTERN = Pattern.compile("(?<!\\[)#([ybrgp])([^\\s#]+)");
    /** BaseMod卡牌颜色标记起始正则表达式 */
    private static final Pattern CARD_COLOR_START_PATTERN = Pattern.compile("\\[#(?:[0-9a-fA-F]{6}|[0-9a-fA-F]{8})\\]");
    /** 旧版卡牌颜色标记前缀正则表达式 */
    private static final Pattern LEGACY_CARD_COLOR_PREFIX_PATTERN = Pattern.compile("(?<!\\[)#([ybrgp])(?=\\S)");

    /** 生成Mod内唯一的ID */
    protected static String makeID(String name) { return BasicMod.makeID(name); }
    /** 卡牌本地化字符串 */
    protected CardStrings cardStrings;

    /** 是否升级描述 */
    protected boolean upgradesDescription;

    /** 基础费用 */
    protected int baseCost;

    /** 是否升级费用 */
    protected boolean upgradeCost;
    /** 费用升级值 */
    protected int costUpgrade;

    /** 是否升级伤害 */
    protected boolean upgradeDamage;
    /** 是否升级格挡 */
    protected boolean upgradeBlock;
    /** 是否升级魔法值 */
    protected boolean upgradeMagic;

    /** 伤害升级值 */
    protected int damageUpgrade;
    /** 格挡升级值 */
    protected int blockUpgrade;
    /** 魔法值升级值 */
    protected int magicUpgrade;

    /** 基础是否消耗 */
    protected boolean baseExhaust = false;
    /** 升级后是否消耗 */
    protected boolean upgExhaust = false;
    /** 基础是否虚无 */
    protected boolean baseEthereal = false;
    /** 升级后是否虚无 */
    protected boolean upgEthereal = false;
    /** 基础是否固有 */
    protected boolean baseInnate = false;
    /** 升级后是否固有 */
    protected boolean upgInnate = false;
    /** 基础是否保留 */
    protected boolean baseRetain = false;
    /** 升级后是否保留 */
    protected boolean upgRetain = false;

    /** 自定义变量映射表 */
    final protected Map<String, LocalVarInfo> cardVariables = new HashMap<>();

    /**
     * 使用CardStats构造卡牌。
     *
     * @param ID 卡牌ID
     * @param info 卡牌属性信息
     */
    public BaseCard(String ID, CardStats info) {
        this(ID, info, getCardTextureString(removePrefix(ID), info.cardType));
    }
    /**
     * 使用CardStats和自定义图片路径构造卡牌。
     *
     * @param ID 卡牌ID
     * @param info 卡牌属性信息
     * @param cardImage 卡牌图片路径
     */
    public BaseCard(String ID, CardStats info, String cardImage) {
        this(ID, info.baseCost, info.cardType, info.cardTarget, info.cardRarity, info.cardColor, cardImage);
    }
    /**
     * 使用基本属性构造卡牌。
     *
     * @param ID 卡牌ID
     * @param cost 费用
     * @param cardType 卡牌类型
     * @param target 卡牌目标
     * @param rarity 稀有度
     * @param color 卡牌颜色
     */
    public BaseCard(String ID, int cost, CardType cardType, CardTarget target, CardRarity rarity, CardColor color) {
        this(ID, cost, cardType, target, rarity, color, getCardTextureString(removePrefix(ID), cardType));
    }
    /**
     * 完整构造函数。
     *
     * @param ID 卡牌ID
     * @param cost 费用
     * @param cardType 卡牌类型
     * @param target 卡牌目标
     * @param rarity 稀有度
     * @param color 卡牌颜色
     * @param cardImage 卡牌图片路径
     */
    public BaseCard(String ID, int cost, CardType cardType, CardTarget target, CardRarity rarity, CardColor color, String cardImage)
    {
        super(ID, getName(ID), cardImage, cost, getInitialDescription(ID), cardType, color, rarity, target);
        this.cardStrings = CardCrawlGame.languagePack.getCardStrings(cardID);
        if (this.cardStrings != null) {
            this.originalName = cardStrings.NAME;
            this.upgradesDescription = cardStrings.UPGRADE_DESCRIPTION != null;
        } else {
            this.originalName = "[缺失文本描述]";
            this.upgradesDescription = false;
        }
        this.upgradeCost = false;
        this.upgradeDamage = false;
        this.upgradeBlock = false;
        this.upgradeMagic = false;

        this.costUpgrade = cost;
        this.damageUpgrade = 0;
        this.blockUpgrade = 0;
        this.magicUpgrade = 0;
    }

    private static String getName(String ID) {
        CardStrings s = CardCrawlGame.languagePack.getCardStrings(ID);
        if (s == null) {
            BasicMod.logger.error("CardStrings for ID " + ID + " is null!");
            return "[缺失文本描述]";
        }
        return s.NAME;
    }
    private static String getInitialDescription(String ID) {
        CardStrings s = CardCrawlGame.languagePack.getCardStrings(ID);
        if (s == null) {
            return "[缺失文本描述]";
        }
        return normalizeCardDescription(s.DESCRIPTION);
    }

    /**
     * 将遗物/能力常见的 #y/#b/#r/#g/#p 颜色标记转换为卡片描述可稳定识别的 [#xxxxxx]...[] 语法。
     */
    public static String normalizeCardDescriptionColors(String raw) {
        if (raw == null || raw.indexOf('#') < 0) {
            return raw;
        }

        Matcher matcher = LEGACY_CARD_COLOR_PATTERN.matcher(raw);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String hex = colorCodeToHex(matcher.group(1).charAt(0));
            if (hex == null) {
                continue;
            }
            String token = matcher.group(2);
            String replacement = "[#" + hex + "]" + token + "[]";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String normalizeCardDescription(String raw) {
        if (isTraditionalChineseLanguage()) {
            return removeTraditionalChineseCardColorMarkers(raw);
        }
        return normalizeCardDescriptionColors(raw);
    }

    /**
     * 判断当前游戏语言是否为繁体中文。
     *
     * @return 当前语言为繁体中文时返回true
     */
    private static boolean isTraditionalChineseLanguage() {
        return Settings.language != null && "ZHT".equalsIgnoreCase(Settings.language.name());
    }

    /**
     * 清理繁体中文卡牌描述中的颜色标记，保留原始文字内容。
     *
     * @param raw 原始卡牌描述
     * @return 去除颜色标记后的卡牌描述
     */
    private static String removeTraditionalChineseCardColorMarkers(String raw) {
        if (raw == null) {
            return null;
        }
        return LEGACY_CARD_COLOR_PREFIX_PATTERN.matcher(CARD_COLOR_START_PATTERN.matcher(raw).replaceAll(""))
                .replaceAll("")
                .replace("[]", "");
    }

    private static boolean isCustomKeywordBoundary(char c) {
        return Character.isWhitespace(c)
                || c == '。'
                || c == '，'
                || c == '、'
                || c == '；'
                || c == '：'
                || c == ','
                || c == '.'
                || c == '!'
                || c == '?'
                || c == '('
                || c == ')'
                || c == '（'
                || c == '）'
                || c == '['
                || c == ']'
                || c == '{'
                || c == '}';
    }

    private static String colorCodeToHex(char code) {
        switch (code) {
            case 'y':
                return "efc100";
            case 'b':
                return "71b1d1";
            case 'r':
                return "ff6563";
            case 'g':
                return "7fff00";
            case 'p':
                return "c996ff";
            default:
                return null;
        }
    }

    @Override
    public void initializeDescription() {
        this.rawDescription = normalizeCardDescription(this.rawDescription);
        try {
            super.initializeDescription();
        } catch (Exception e) {
            BasicMod.logger.error("卡牌描述初始化异常, cardID=" + this.cardID + ", rawDescription=" + this.rawDescription, e);
        }
    }

    @Override
    public List<TooltipInfo> getCustomTooltipsTop() {
        return buildCustomKeywordTooltips();
    }

    private List<TooltipInfo> buildCustomKeywordTooltips() {
        if (this.rawDescription == null) {
            return null;
        }

        if (BasicMod.keywords.isEmpty()) {
            return null;
        }

        ArrayList<TooltipInfo> tooltips = new ArrayList<>();
        String searchText = getKeywordSearchText(this.rawDescription);
        for (KeywordInfo keyword : BasicMod.keywords.values()) {
            if (containsCustomKeywordName(searchText, keyword)) {
                addCustomKeywordTooltip(tooltips, keyword);
            }
        }
        return tooltips.isEmpty() ? null : tooltips;
    }

    private void addCustomKeywordTooltip(ArrayList<TooltipInfo> tooltips, KeywordInfo keyword) {
        if (keyword == null || keyword.PROPER_NAME == null || keyword.DESCRIPTION == null) {
            return;
        }

        if (!hasTooltip(tooltips, keyword.PROPER_NAME)) {
            tooltips.add(new TooltipInfo(keyword.PROPER_NAME, keyword.DESCRIPTION));
        }
        if (keyword.EXTRA == null) {
            return;
        }
        for (String extraID : keyword.EXTRA) {
            addCustomKeywordTooltip(tooltips, BasicMod.keywords.get(extraID));
        }
    }

    private static boolean hasTooltip(ArrayList<TooltipInfo> tooltips, String title) {
        for (TooltipInfo tooltip : tooltips) {
            if (tooltip != null && tooltip.title != null && tooltip.title.equals(title)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsCustomKeywordName(String searchText, KeywordInfo keyword) {
        if (keyword == null) {
            return false;
        }
        if (containsKeywordName(searchText, keyword.PROPER_NAME)) {
            return true;
        }
        if (keyword.NAMES == null) {
            return false;
        }
        for (String name : keyword.NAMES) {
            if (containsKeywordName(searchText, name)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsKeywordName(String searchText, String keywordName) {
        if (searchText == null || keywordName == null || keywordName.isEmpty()) {
            return false;
        }

        if (containsCjk(keywordName)) {
            return searchText.toLowerCase().contains(keywordName.toLowerCase());
        }

        int index = 0;
        while (index <= searchText.length() - keywordName.length()) {
            int foundIndex = searchText.toLowerCase().indexOf(keywordName.toLowerCase(), index);
            if (foundIndex < 0) {
                return false;
            }
            int endIndex = foundIndex + keywordName.length();
            boolean validStart = foundIndex == 0 || isCustomKeywordBoundary(searchText.charAt(foundIndex - 1));
            boolean validEnd = endIndex >= searchText.length() || isCustomKeywordBoundary(searchText.charAt(endIndex));
            if (validStart && validEnd) {
                return true;
            }
            index = foundIndex + 1;
        }
        return false;
    }

    private static String getKeywordSearchText(String raw) {
        return raw.replace(BasicMod.modID + ":", "")
                .replace(BasicMod.modID.toLowerCase() + ":", "")
                .replace("*", "")
                .replaceAll("\\[#(?:[0-9a-fA-F]{6}|[0-9a-fA-F]{8})\\]", "")
                .replace("[]", "")
                .replace("#y", "")
                .replace("#b", "")
                .replace("#r", "")
                .replace("#g", "")
                .replace("#p", "");
    }

    private static boolean containsCjk(String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= '\u4e00' && c <= '\u9fff') {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断阿福连招是否激活。
     * 本回合除当前牌外，是否已经打出过带有afu标签的牌。
     *
     * @return 如果连招激活返回true
     */
    public boolean isAfuComboActive() {
        if (AbstractDungeon.actionManager == null) return false;
        
        // 判定条件：本回合除当前牌外，是否已经打出过带有 afu 标签的牌
        for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisTurn) {
            if (c != this && c.tags.contains(CustomTags.afu)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 设置伤害值。
     *
     * @param damage 基础伤害值
     */
    protected final void setDamage(int damage)
    {
        this.setDamage(damage, 0);
    }
    /**
     * 设置伤害值和升级伤害值。
     *
     * @param damage 基础伤害值
     * @param damageUpgrade 升级时增加的伤害值
     */
    protected final void setDamage(int damage, int damageUpgrade)
    {
        this.baseDamage = this.damage = damage;
        if (damageUpgrade != 0)
        {
            this.upgradeDamage = true;
            this.damageUpgrade = damageUpgrade;
        }
    }

    /**
     * 设置格挡值。
     *
     * @param block 基础格挡值
     */
    protected final void setBlock(int block)
    {
        this.setBlock(block, 0);
    }
    /**
     * 设置格挡值和升级格挡值。
     *
     * @param block 基础格挡值
     * @param blockUpgrade 升级时增加的格挡值
     */
    protected final void setBlock(int block, int blockUpgrade)
    {
        this.baseBlock = this.block = block;
        if (blockUpgrade != 0)
        {
            this.upgradeBlock = true;
            this.blockUpgrade = blockUpgrade;
        }
    }

    /**
     * 设置魔法值。
     *
     * @param magic 基础魔法值
     */
    protected final void setMagic(int magic)
    {
        this.setMagic(magic, 0);
    }
    /**
     * 设置魔法值和升级魔法值。
     *
     * @param magic 基础魔法值
     * @param magicUpgrade 升级时增加的魔法值
     */
    protected final void setMagic(int magic, int magicUpgrade)
    {
        this.baseMagicNumber = this.magicNumber = magic;
        if (magicUpgrade != 0)
        {
            this.upgradeMagic = true;
            this.magicUpgrade = magicUpgrade;
        }
    }



    protected final void setCustomVar(String key, int base) {
        this.setCustomVar(key, base, 0);
    }
    protected final void setCustomVar(String key, int base, int upgrade) {
        setCustomVarValue(key, base, upgrade);

        if (!customVars.containsKey(key)) {
            QuickDynamicVariable var = new QuickDynamicVariable(key);
            customVars.put(key, var);
            BaseMod.addDynamicVariable(var);
            initializeDescription();
        }
    }

    protected enum VariableType {
        DAMAGE,
        BLOCK,
        MAGIC
    }
    protected final void setCustomVar(String key, VariableType type, int base) {
        setCustomVar(key, type, base, 0);
    }
    protected final void setCustomVar(String key, VariableType type, int base, int upgrade) {
        setCustomVarValue(key, base, upgrade);

        switch (type) {
            case DAMAGE:
                calculateVarAsDamage(key);
                break;
            case BLOCK:
                calculateVarAsBlock(key);
                break;
        }

        if (!customVars.containsKey(key)) {
            QuickDynamicVariable var = new QuickDynamicVariable(key);
            customVars.put(key, var);
            BaseMod.addDynamicVariable(var);
            initializeDescription();
        }
    }
    protected final void setCustomVar(String key, VariableType type, int base, TriFunction<BaseCard, AbstractMonster, Integer, Integer> preCalc) {
        setCustomVar(key, type, base, 0, preCalc);
    }
    protected final void setCustomVar(String key, VariableType type, int base, int upgrade, TriFunction<BaseCard, AbstractMonster, Integer, Integer> preCalc) {
        setCustomVar(key, type, base, upgrade, preCalc, LocalVarInfo::noCalc);
    }
    protected final void setCustomVar(String key, VariableType type, int base, TriFunction<BaseCard, AbstractMonster, Integer, Integer> preCalc, TriFunction<BaseCard, AbstractMonster, Integer, Integer> postCalc) {
        setCustomVar(key, type, base, 0, preCalc, postCalc);
    }
    protected final void setCustomVar(String key, VariableType type, int base, int upgrade, TriFunction<BaseCard, AbstractMonster, Integer, Integer> preCalc, TriFunction<BaseCard, AbstractMonster, Integer, Integer> postCalc) {
        setCustomVarValue(key, base, upgrade);

        switch (type) {
            case DAMAGE:
                setVarCalculation(key, (c, m, baseVal)->{
                    boolean wasMultiDamage = c.isMultiDamage;
                    c.isMultiDamage = false;

                    int origBase = c.baseDamage, origVal = c.damage;

                    c.baseDamage = preCalc.apply(c, m, baseVal);

                    if (m != null)
                        c.calculateCardDamage(m);
                    else
                        c.applyPowers();

                    c.damage = postCalc.apply(c, m, c.damage);

                    c.baseDamage = origBase;
                    c.isMultiDamage = wasMultiDamage;

                    int result = c.damage;
                    c.damage = origVal;

                    return result;
                });
                break;
            case BLOCK:
                setVarCalculation(key, (c, m, baseVal)->{
                    int origBase = c.baseBlock, origVal = c.block;

                    c.baseBlock = preCalc.apply(c, m, baseVal);

                    if (m != null)
                        c.calculateCardDamage(m);
                    else
                        c.applyPowers();

                    c.block = postCalc.apply(c, m, c.block);

                    c.baseBlock = origBase;
                    int result = c.block;
                    c.block = origVal;
                    return result;
                });
                break;
            default:
                setVarCalculation(key, (c, m, baseVal)->{
                    int tmp = baseVal;

                    tmp = preCalc.apply(c, m, tmp);
                    tmp = postCalc.apply(c, m, tmp);

                    return tmp;
                });
                break;
        }

        if (!customVars.containsKey(key)) {
            QuickDynamicVariable var = new QuickDynamicVariable(key);
            customVars.put(key, var);
            BaseMod.addDynamicVariable(var);
            initializeDescription();
        }
    }

    private void setCustomVarValue(String key, int base, int upg) {
        cardVariables.compute(key, (k, old)->{
            if (old == null) {
                return new LocalVarInfo(base, upg);
            }
            else {
                old.base = base;
                old.upgrade = upg;
                return old;
            }
        });
    }

    protected final void colorCustomVar(String key, Color normalColor) {
        colorCustomVar(key, normalColor, Settings.GREEN_TEXT_COLOR, Settings.RED_TEXT_COLOR, Settings.GREEN_TEXT_COLOR);
    }
    protected final void colorCustomVar(String key, Color normalColor, Color increasedColor, Color decreasedColor) {
        colorCustomVar(key, normalColor, increasedColor, decreasedColor, increasedColor);
    }
    protected final void colorCustomVar(String key, Color normalColor, Color increasedColor, Color decreasedColor, Color upgradedColor) {
        LocalVarInfo var = getCustomVar(key);
        if (var == null) {
            throw new IllegalArgumentException("Attempted to set color of variable that hasn't been registered.");
        }

        var.normalColor = normalColor;
        var.increasedColor = increasedColor;
        var.decreasedColor = decreasedColor;
        var.upgradedColor = upgradedColor;
    }


    private LocalVarInfo getCustomVar(String key) {
        return cardVariables.get(key);
    }

    protected void calculateVarAsDamage(String key) {
        setVarCalculation(key, (c, m, base)->{
            boolean wasMultiDamage = c.isMultiDamage;
            c.isMultiDamage = false;

            int origBase = c.baseDamage, origVal = c.damage;

            c.baseDamage = base;
            if (m != null)
                c.calculateCardDamage(m);
            else
                c.applyPowers();

            c.baseDamage = origBase;
            c.isMultiDamage = wasMultiDamage;

            int result = c.damage;
            c.damage = origVal;

            return result;
        });
    }
    protected void calculateVarAsBlock(String key) {
        setVarCalculation(key, (c, m, base)->{
            int origBase = c.baseBlock, origVal = c.block;

            c.baseBlock = base;
            if (m != null)
                c.calculateCardDamage(m);
            else
                c.applyPowers();

            c.baseBlock = origBase;
            int result = c.block;
            c.block = origVal;
            return result;
        });
    }
    protected void setVarCalculation(String key, TriFunction<BaseCard, AbstractMonster, Integer, Integer> calculation) {
        cardVariables.get(key).calculation = calculation;
    }

    public int customVarBase(String key) {
        LocalVarInfo var = cardVariables.get(key);
        if (var == null)
            return -1;
        return var.base;
    }
    public int customVar(String key) {
        LocalVarInfo var = cardVariables == null ? null : cardVariables.get(key); //Prevents crashing when used with dynamic text
        if (var == null)
            return -1;
        return var.value;
    }
    public int[] customVarMulti(String key) {
        LocalVarInfo var = cardVariables.get(key);
        if (var == null)
            return null;
        return var.aoeValue;
    }
    public boolean isCustomVarModified(String key) {
        LocalVarInfo var = cardVariables.get(key);
        if (var == null)
            return false;
        return var.isModified();
    }
    public boolean customVarUpgraded(String key) {
        LocalVarInfo var = cardVariables.get(key);
        if (var == null)
            return false;
        return var.upgraded;
    }


    protected final void setCostUpgrade(int costUpgrade)
    {
        this.costUpgrade = costUpgrade;
        this.upgradeCost = true;
    }
    protected final void setExhaust(boolean exhaust) { this.setExhaust(exhaust, exhaust); }
    protected final void setEthereal(boolean ethereal) { this.setEthereal(ethereal, ethereal); }
    protected final void setInnate(boolean innate) {this.setInnate(innate, innate); }
    protected final void setSelfRetain(boolean retain) {this.setSelfRetain(retain, retain); }
    protected final void setExhaust(boolean baseExhaust, boolean upgExhaust)
    {
        this.baseExhaust = baseExhaust;
        this.upgExhaust = upgExhaust;
        this.exhaust = baseExhaust;
    }
    protected final void setEthereal(boolean baseEthereal, boolean upgEthereal)
    {
        this.baseEthereal = baseEthereal;
        this.upgEthereal = upgEthereal;
        this.isEthereal = baseEthereal;
    }
    protected void setInnate(boolean baseInnate, boolean upgInnate)
    {
        this.baseInnate = baseInnate;
        this.upgInnate = upgInnate;
        this.isInnate = baseInnate;
    }
    protected void setSelfRetain(boolean baseRetain, boolean upgRetain)
    {
        this.baseRetain = baseRetain;
        this.upgRetain = upgRetain;
        this.selfRetain = baseRetain;
    }


    @Override
    public AbstractCard makeStatEquivalentCopy() {
        AbstractCard candidate = super.makeStatEquivalentCopy();

        if (candidate instanceof BaseCard) {
            BaseCard card = (BaseCard) candidate;
            card.rawDescription = this.rawDescription;
            card.upgradesDescription = this.upgradesDescription;

            card.baseCost = this.baseCost;

            card.upgradeCost = this.upgradeCost;
            card.upgradeDamage = this.upgradeDamage;
            card.upgradeBlock = this.upgradeBlock;
            card.upgradeMagic = this.upgradeMagic;

            card.costUpgrade = this.costUpgrade;
            card.damageUpgrade = this.damageUpgrade;
            card.blockUpgrade = this.blockUpgrade;
            card.magicUpgrade = this.magicUpgrade;

            card.baseExhaust = this.baseExhaust;
            card.upgExhaust = this.upgExhaust;
            card.baseEthereal = this.baseEthereal;
            card.upgEthereal = this.upgEthereal;
            card.baseInnate = this.baseInnate;
            card.upgInnate = this.upgInnate;
            card.baseRetain = this.baseRetain;
            card.upgRetain = this.upgRetain;

            for (Map.Entry<String, LocalVarInfo> varEntry : cardVariables.entrySet()) {
                LocalVarInfo target = card.getCustomVar(varEntry.getKey()),
                        current = varEntry.getValue();
                if (target == null) {
                    card.setCustomVar(varEntry.getKey(), current.base, current.upgrade);
                    target = card.getCustomVar(varEntry.getKey());
                }
                target.base = current.base;
                target.value = current.value;
                target.aoeValue = current.aoeValue;
                target.upgrade = current.upgrade;
                target.calculation = current.calculation;
            }
        }

        return candidate;
    }

    @Override
    public void upgrade()
    {
        if (!upgraded)
        {
            this.upgradeName();

            if (this.upgradesDescription)
            {
                if (cardStrings.UPGRADE_DESCRIPTION == null)
                {
                    BasicMod.logger.error("Card " + cardID + " upgrades description and has null upgrade description.");
                }
                else
                {
                    this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
                }
            }

            if (upgradeCost)
            {
                if (isCostModified && this.cost < this.baseCost && this.cost >= 0) {
                    int diff = this.costUpgrade - this.baseCost; //how the upgrade alters cost
                    this.upgradeBaseCost(this.cost + diff);
                    if (this.cost < 0)
                        this.cost = 0;
                }
                else {
                    upgradeBaseCost(costUpgrade);
                }
            }

            if (upgradeDamage)
                this.upgradeDamage(damageUpgrade);

            if (upgradeBlock)
                this.upgradeBlock(blockUpgrade);

            if (upgradeMagic)
                this.upgradeMagicNumber(magicUpgrade);

            for (LocalVarInfo var : cardVariables.values()) {
                upgradeCustomVar(var);
            }

            if (baseExhaust ^ upgExhaust)
                this.exhaust = upgExhaust;

            if (baseInnate ^ upgInnate)
                this.isInnate = upgInnate;

            if (baseEthereal ^ upgEthereal)
                this.isEthereal = upgEthereal;

            if (baseRetain ^ upgRetain)
                this.selfRetain = upgRetain;


            this.initializeDescription();
        }
    }

    protected void upgradeCustomVar(String key) {
        LocalVarInfo var = cardVariables.get(key);
        if (var == null) {
            throw new NullPointerException("Custom variable with key " + key + " does not exist in " + getClass().getName());
        }
        upgradeCustomVar(var, var.upgrade);
    }

    protected void upgradeCustomVar(String key, int amount) {
        LocalVarInfo var = cardVariables.get(key);
        if (var == null) {
            throw new NullPointerException("Custom variable with key " + key + " does not exist in " + getClass().getName());
        }
        upgradeCustomVar(var, amount);
    }

    protected void upgradeCustomVar(LocalVarInfo var) {
        upgradeCustomVar(var, var.upgrade);
    }

    protected void upgradeCustomVar(LocalVarInfo var, int amt) {
        if (amt != 0) {
            var.base += amt;
            var.value = var.base;
            var.upgraded = true;
        }
    }

    boolean inCalc = false;
    @Override
    public void applyPowers() {
        if (!inCalc) {
            inCalc = true;
            for (LocalVarInfo var : cardVariables.values()) {
                var.value = var.calculation.apply(this, null, var.base);
            }
            if (isMultiDamage) {
                ArrayList<AbstractMonster> monsters = AbstractDungeon.getCurrRoom().monsters.monsters;
                AbstractMonster m;
                for (LocalVarInfo var : cardVariables.values()) {
                    if (var.aoeValue == null || var.aoeValue.length != monsters.size())
                        var.aoeValue = new int[monsters.size()];

                    for (int i = 0; i < monsters.size(); ++i) {
                        m = monsters.get(i);
                        var.aoeValue[i] = var.calculation.apply(this, m, var.base);
                    }
                }
            }
            inCalc = false;
        }

        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        if (!inCalc) {
            inCalc = true;
            for (LocalVarInfo var : cardVariables.values()) {
                var.value = var.calculation.apply(this, m, var.base);
            }
            if (isMultiDamage) {
                ArrayList<AbstractMonster> monsters = AbstractDungeon.getCurrRoom().monsters.monsters;
                for (LocalVarInfo var : cardVariables.values()) {
                    if (var.aoeValue == null || var.aoeValue.length != monsters.size())
                        var.aoeValue = new int[monsters.size()];

                    for (int i = 0; i < monsters.size(); ++i) {
                        m = monsters.get(i);
                        var.aoeValue[i] = var.calculation.apply(this, m, var.base);
                    }
                }
            }
            inCalc = false;
        }

        super.calculateCardDamage(m);
    }

    @Override
    public void resetAttributes() {
        super.resetAttributes();

        for (LocalVarInfo var : cardVariables.values()) {
            var.value = var.base;
        }
    }

    private static class QuickDynamicVariable extends DynamicVariable {
        final String localKey, key;

        private BaseCard current = null;

        public QuickDynamicVariable(String key) {
            this.localKey = key;
            this.key = makeID(key);
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public void setIsModified(AbstractCard c, boolean v) {
            if (c instanceof BaseCard) {
                LocalVarInfo var = ((BaseCard) c).getCustomVar(localKey);
                if (var != null)
                    var.forceModified = v;
            }
        }

        @Override
        public boolean isModified(AbstractCard c) {
            return c instanceof BaseCard && (current = (BaseCard) c).isCustomVarModified(localKey);
        }

        @Override
        public int value(AbstractCard c) {
            return c instanceof BaseCard ? ((BaseCard) c).customVar(localKey) : 0;
        }

        @Override
        public int baseValue(AbstractCard c) {
            return c instanceof BaseCard ? ((BaseCard) c).customVarBase(localKey) : 0;
        }

        @Override
        public boolean upgraded(AbstractCard c) {
            return c instanceof BaseCard && ((BaseCard) c).customVarUpgraded(localKey);
        }

        public Color getNormalColor() {
            LocalVarInfo var;
            if (current == null || (var = current.getCustomVar(localKey)) == null)
                return Settings.CREAM_COLOR;

            return var.normalColor;
        }

        public Color getUpgradedColor() {
            LocalVarInfo var;
            if (current == null || (var = current.getCustomVar(localKey)) == null)
                return Settings.GREEN_TEXT_COLOR;

            return var.upgradedColor;
        }

        public Color getIncreasedValueColor() {
            LocalVarInfo var;
            if (current == null || (var = current.getCustomVar(localKey)) == null)
                return Settings.GREEN_TEXT_COLOR;

            return var.increasedColor;
        }

        public Color getDecreasedValueColor() {
            LocalVarInfo var;
            if (current == null || (var = current.getCustomVar(localKey)) == null)
                return Settings.RED_TEXT_COLOR;

            return var.decreasedColor;
        }
    }

    protected static class LocalVarInfo {
        int base, value, upgrade;
        int[] aoeValue = null;
        boolean upgraded = false;
        boolean forceModified = false;
        Color normalColor = Settings.CREAM_COLOR;
        Color upgradedColor = Settings.GREEN_TEXT_COLOR;
        Color increasedColor = Settings.GREEN_TEXT_COLOR;
        Color decreasedColor = Settings.RED_TEXT_COLOR;

        TriFunction<BaseCard, AbstractMonster, Integer, Integer> calculation = LocalVarInfo::noCalc;

        public LocalVarInfo(int base, int upgrade) {
            this.base = this.value = base;
            this.upgrade = upgrade;
        }

        private static int noCalc(BaseCard c, AbstractMonster m, int base) {
            return base;
        }

        public boolean isModified() {
            return forceModified || base != value;
        }
    }
}
