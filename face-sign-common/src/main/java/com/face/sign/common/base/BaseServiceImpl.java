package com.face.sign.common.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 服务实现基类
 */
public class BaseServiceImpl<Entity extends BaseEntity, M extends IBaseMapper<Entity>> extends ServiceImpl<M, Entity>
		implements IBaseService<Entity> {

	@Autowired
	protected M mapper;

	@Override
	public IPage<Entity> page(int page, int size) {
		return mapper.selectPage(new Page<>(page, size), null);
	}

	@Override
	public IPage<Entity> pageByCondition(int page, int size, Map<String, Object> conditions) {
		QueryWrapper<Entity> queryWrapper = buildQueryWrapper(conditions);
		return mapper.selectPage(new Page<>(page, size), queryWrapper);
	}

	/**
	 * 构建查询条件
	 * @param conditions 查询条件Map
	 * @return QueryWrapper对象
	 */
	protected QueryWrapper<Entity> buildQueryWrapper(Map<String, Object> conditions) {
		QueryWrapper<Entity> queryWrapper = new QueryWrapper<>();
		if (conditions != null) {
			conditions.forEach((key, value) -> {
				if (value != null && !"".equals(value)) {
					String columnName = camelToUnderline(key);
					queryWrapper.eq(columnName, value);
				}
			});
		}
		return queryWrapper;
	}

	/**
	 * 将驼峰命名转换为下划线命名
	 * 例如：userName -> user_name
	 * @param camelCase 驼峰命名的字符串
	 * @return 下划线命名的字符串
	 */
	protected String camelToUnderline(String camelCase) {
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
}
