package com.face.sign.common.util;

import org.springframework.stereotype.Component;

@Component
public class BaseUtil {

    /**
     * 将驼峰命名转换为下划线命名
     * 例如：userName -> user_name
     * @param camelCase 驼峰命名的字符串
     * @return 下划线命名的字符串
     */
    public static String camelToUnderline(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }
        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(camelCase.charAt(0)));
        for (int i = 1; i < camelCase.length(); i++) {
            char ch = camelCase.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_').append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    /**
     * 判断值是否有效（非 null 且非空字符串）
     *
     * @param value 值
     * @return 是否有效
     */
    public static boolean isValueValid(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof String && ((String) value).isEmpty()) {
            return false;
        }
        return true;
    }
}
