package com.face.sign.common.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 业务服务基类
 *
 */
public interface IBaseService<E extends BaseEntity> extends IService<E> {
	
	/**
	 * 条件分页查询
	 * @param page 页码
	 * @param size 每页大小
	 * @param conditions 查询条件
	 * @return 分页结果
	 */
	IPage<E> page(int page, int size, Map<String, Object> conditions);
}
