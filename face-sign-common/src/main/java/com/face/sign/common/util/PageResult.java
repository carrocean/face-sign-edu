package com.face.sign.common.util;

import lombok.Data;

import java.util.List;

/**
 * 分页结果工具类
 */
@Data
public class PageResult<T> {
    /**
     * 数据列表
     */
    private List<T> records;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 当前页码
     */
    private Long current;
    
    /**
     * 每页大小
     */
    private Long size;
    
    /**
     * 总页数
     */
    private Long pages;
    
    public PageResult(List<T> records, Long total) {
        this.records = records;
        this.total = total;
    }
    
    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (total + size - 1) / size;
    }
} 