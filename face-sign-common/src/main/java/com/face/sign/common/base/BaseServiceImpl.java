package com.face.sign.common.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.face.sign.common.util.mybatisplus.QueryWrapperUtils;
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
	public IPage<Entity> page(int page, int size, Map<String, Object> conditions) {
		QueryWrapper<Entity> queryWrapper = new QueryWrapper<>();
		QueryWrapperUtils.buildQueryWrapper(queryWrapper, conditions);
		return mapper.selectPage(new Page<>(page, size), queryWrapper);
	}


}
