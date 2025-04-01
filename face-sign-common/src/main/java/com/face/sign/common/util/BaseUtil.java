package com.face.sign.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class BaseUtil {

    private static final Logger logger = LoggerFactory.getLogger(BaseUtil.class);

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

    /**
     * 类型转换: Entity - Vo转换
     * @param source
     * @param target
     * @return T
     * @param <T>
     */
    public static <T> T entityToVo(Object source, Class<T> target) {
        if (source == null) {
            return null;
        }
        T targetObject = null;
        try {
            targetObject = target.newInstance();
            BeanUtils.copyProperties(source, targetObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetObject;
    }

    /**
     * 类型转换: List<Entity> -> List<Vo>转换
     * @param sourceList
     * @param target
     * @return
     * @param <T>
     */
    public static <T> List<T> entityToVoList(Collection<?> sourceList, Class<T> target) {
        if (sourceList == null) {
            return null;
        }
        List<T> targetList = new ArrayList<>(sourceList.size());

        try {
            for (Object source : sourceList) {
                T targetObject = target.newInstance();
                BeanUtils.copyProperties(source, targetObject);
                targetList.add(targetObject);
            }
        } catch (Exception e) {
            logger.error("convert error ", e);
        }
        return targetList;
    }
}
