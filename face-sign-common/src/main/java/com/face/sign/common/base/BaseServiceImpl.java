package com.face.sign.common.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 服务实现基类
 *
 */
public class BaseServiceImpl<Entity extends BaseEntity, M extends IBaseMapper<Entity>> extends ServiceImpl<M, Entity>
		implements IBaseService<Entity> {

	@Autowired
	protected M mapper;


	@Override
	public IPage<Entity> page(int page, int size) {
		return mapper.selectPage(new Page<>(page, size), null);
	}

}
