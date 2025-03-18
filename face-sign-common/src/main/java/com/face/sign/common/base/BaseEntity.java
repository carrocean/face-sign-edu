package com.face.sign.common.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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

	private Date deleteTime; // 删除时间

	private Date updateTime; // 更新时间

	private int hasDelete; // 是否删除(1删除|0正常|null正常)
}
