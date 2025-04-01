package com.face.sign.common.util.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.face.sign.common.util.BaseUtil;

import java.util.Map;

import static com.face.sign.common.util.BaseUtil.isValueValid;

public class QueryWrapperUtils {

    /**
     * 根据 Map 构造查询条件
     *
     * @param queryWrapper 查询包装器
     * @param conditions   查询条件（Map 类型，key 为列名，value 为查询值）
     * @param <T>          泛型
     */
    public static <T> void buildQueryWrapper(QueryWrapper<T> queryWrapper, Map<String, Object> conditions) {
        if (conditions != null) {
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                String column = BaseUtil.camelToUnderline(entry.getKey());
                Object value = entry.getValue();
                if (isValueValid(value)) {
                    queryWrapper.eq(column, value);
                }
            }
        }
    }

    /**
     * 根据 Map 构造模糊查询条件
     *
     * @param queryWrapper 查询包装器
     * @param conditions   查询条件（Map 类型，key 为列名，value 为查询值）
     * @param <T>          泛型
     */
    public static <T> void buildLikeQueryWrapper(QueryWrapper<T> queryWrapper, Map<String, Object> conditions) {
        if (conditions != null) {
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                String column = BaseUtil.camelToUnderline(entry.getKey());
                Object value = entry.getValue();
                if (isValueValid(value)) {
                    queryWrapper.like(column, value);
                }
            }
        }
    }

    /**
     * 根据 Map 构造更新条件
     *
     * @param updateWrapper 更新包装器
     * @param conditions    更新条件（Map 类型，key 为列名，value 为更新值）
     * @param <T>           泛型
     */
    public static <T> void buildUpdateWrapper(UpdateWrapper<T> updateWrapper, Map<String, Object> conditions) {
        if (conditions != null) {
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                String column = BaseUtil.camelToUnderline(entry.getKey());
                Object value = entry.getValue();
                if (isValueValid(value)) {
                    updateWrapper.set(column, value);
                }
            }
        }
    }
}