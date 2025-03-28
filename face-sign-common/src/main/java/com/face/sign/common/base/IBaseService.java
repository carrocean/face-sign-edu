package com.face.sign.common.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 业务服务基类
 *
 */
public interface IBaseService<Entity> extends IService<Entity> {

	/**
	 * 分页查询
	 * @param page 页码
	 * @param size 每页大小
	 * @return 分页结果
	 */
	IPage<Entity> page(int page, int size);
}
