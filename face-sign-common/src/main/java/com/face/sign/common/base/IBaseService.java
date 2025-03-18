package com.face.sign.common.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.List;

/**
 * 业务服务基类
 *
 */
public interface IBaseService<Entity> {
	void save(Entity entity);
	void deleteById(Integer id);
	void update(Entity entity);
	Entity getById(Integer id);
	List<Entity> getAll();

}
