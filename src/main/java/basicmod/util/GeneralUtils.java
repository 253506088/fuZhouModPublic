package basicmod.util;

/**
 * 通用工具类。
 * 提供数组转字符串、ID前缀移除等通用功能。
 */
public class GeneralUtils {
    /**
     * 将数组转换为逗号分隔的字符串。
     *
     * @param arr 要转换的数组
     * @return 逗号分隔的字符串
     */
    public static String arrToString(Object[] arr) {
        if (arr == null)
            return null;
        if (arr.length == 0)
            return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length - 1; ++i) {
            sb.append(arr[i]).append(", ");
        }
        sb.append(arr[arr.length - 1]);
        return sb.toString();
    }

    /**
     * 移除ID中的Mod前缀（冒号及之前的部分）。
     *
     * @param ID 完整ID
     * @return 移除前缀后的ID
     */
    public static String removePrefix(String ID) {
        return ID.substring(ID.indexOf(":") + 1);
    }
}
