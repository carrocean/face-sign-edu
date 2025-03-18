package com.face.sign.common.base;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 服务实现基类
 *
 */
public class IBaseServiceImpl<Entity extends BaseEntity, M extends IBaseMapper<Entity>> implements IBaseService<Entity> {

	protected M mapper;

	public void init(M mapper) {
		this.mapper = mapper;
	}

	@Override
	public int save(Entity entity) {
		return mapper.insert(entity);
	}

	@Override
	public int deleteById(Long id) {
		return mapper.deleteById(id);
	}

	@Override
	public int update(Entity entity) {
		return mapper.updateById(entity);
	}

	@Override
	public Entity getById(Long id) {
		return mapper.selectById(id);
	}

	@Override
	public List<Entity> getAll() {
		return mapper.selectAll();
	}


}
