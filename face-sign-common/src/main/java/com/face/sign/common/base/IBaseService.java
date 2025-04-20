package com.face.sign.common.base;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 业务服务基类
 *
 */
public interface IBaseService<E extends BaseEntity> extends IService<E> {
	
	/**
	 * 条件分页查询
	 *
	 * @param currentPage 页码
	 * @param pageSize    每页大小
	 * @param conditions  查询条件
	 * @return 分页结果
	 */
	Object page(int currentPage, int pageSize, boolean pageSearch, boolean fuzzySearch, Map<String, Object> conditions);
}
