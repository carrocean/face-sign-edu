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
	public void save(Entity entity) {
		mapper.insert(entity);
	}

	@Override
	public void deleteById(Integer id) {
		mapper.deleteById(id);
	}

	@Override
	public void update(Entity entity) {
		mapper.updateById(entity);
	}

	@Override
	public Entity getById(Integer id) {
		return mapper.selectById(id);
	}

	@Override
	public List<Entity> getAll() {
		return mapper.selectAll();
	}


}
