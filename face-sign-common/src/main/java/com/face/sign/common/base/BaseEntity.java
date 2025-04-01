package com.face.sign.common.base;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 实体对象基类,定义基本属性
 *
 */
@Data
public class BaseEntity implements Serializable {

	private Long id; // 唯一ID号

	private Date addTime; // 添加时间

	private Date updateTime; // 更新时间
}
