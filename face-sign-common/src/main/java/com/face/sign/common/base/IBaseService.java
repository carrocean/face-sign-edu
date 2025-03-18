package com.face.sign.common.base;

import java.util.List;

/**
 * 业务服务基类
 *
 */
public interface IBaseService<Entity> {
	int save(Entity entity);
	int deleteById(Long id);
	int update(Entity entity);
	Entity getById(Long id);
	List<Entity> getAll();

}
