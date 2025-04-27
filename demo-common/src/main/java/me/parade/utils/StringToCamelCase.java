package me.parade.utils;

public class StringToCamelCase {
    /**
     * 将分隔符分割的字符串转为驼峰格式
     * 例如：access-token-expiration -> accessTokenExpiration
     *
     * @param str 原始字符串
     * @return 驼峰格式字符串
     */
    public static String toCamelCase(String str) {
        return toCamelCase(str, '-');
    }

    /**
     * 将指定分隔符分割的字符串转为驼峰格式
     *
     * @param str 原始字符串
     * @param separator 分隔符
     * @return 驼峰格式字符串
     */
    public static String toCamelCase(String str, char separator) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (char c : str.toCharArray()) {
            if (c == separator) {
                upperCase = true;
            } else {
                sb.append(upperCase ? Character.toUpperCase(c) : c);
                upperCase = false;
            }
        }
        return sb.toString();
    }
}
