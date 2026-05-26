package basicmod.util;

import basemod.helpers.KeywordColorInfo;

/**
 * 关键字信息类。
 * 存储自定义关键字的本地化信息。
 */
public class KeywordInfo {
    /** 关键字ID */
    public String ID = "";
    /** 关键字显示名称 */
    public String PROPER_NAME;
    /** 关键字描述 */
    public String DESCRIPTION;
    /** 关键字别名数组 */
    public String[] NAMES;
    /** 额外关联的关键字ID */
    public String[] EXTRA = new String[] {};
    /** 关键字颜色信息 */
    public KeywordColorInfo COLOR;

    /**
     * 构造函数。
     */
    public KeywordInfo() {
    }

    /**
     * 准备关键字数据，将别名转为小写。
     */
    public void prep() {
        for (int i = 0; i < NAMES.length; ++i)
        {
            NAMES[i] = NAMES[i].toLowerCase();
        }
    }
}