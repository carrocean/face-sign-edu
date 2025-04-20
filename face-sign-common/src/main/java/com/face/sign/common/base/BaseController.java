package com.face.sign.common.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.face.sign.common.util.JsonMsgDataBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public abstract class BaseController<E extends BaseEntity, S extends IBaseService<E>> {

    @Autowired
    protected  S baseService;


    @PostMapping("/save")
    public JsonMsgDataBean save(@RequestBody E entity) {
        baseService.save(entity);
        return JsonMsgDataBean.buildSuccess();
    }

    @DeleteMapping("/delete/{id}")
    public JsonMsgDataBean deleteById(@PathVariable Long id) {
        baseService.removeById(id);
        return JsonMsgDataBean.buildSuccess();
    }

    @PutMapping("/update")
    public JsonMsgDataBean update(@RequestBody E entity) {
        baseService.updateById(entity);
        return JsonMsgDataBean.buildSuccess();
    }

    @GetMapping("/getById/{id}")
    public JsonMsgDataBean getById(@PathVariable Long id) {
        E entity = baseService.getById(id);
        return JsonMsgDataBean.buildSuccess(entity);
    }

    @PostMapping("/page-list")
    public JsonMsgDataBean getPageAll(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "true") boolean fuzzySearch,
            @RequestParam(defaultValue = "true") boolean pageSearch,
            @RequestBody(required = false) Map<String, Object> searchForm) {
        Object pageResult = baseService.page(currentPage, pageSize, pageSearch, fuzzySearch, searchForm);
        return JsonMsgDataBean.buildSuccess(pageResult);
    }

    @GetMapping("/list")
    public JsonMsgDataBean getAll() {
        try {
            QueryWrapper<E> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            List<E> list = baseService.list(queryWrapper);
            return JsonMsgDataBean.buildSuccess(list);
        } catch (Exception e) {
            return JsonMsgDataBean.buildFail("查询列表失败");
        }
    }

    @DeleteMapping("/delete/batch")
    public JsonMsgDataBean deleteBatch(@RequestBody List<Long> ids) {
        baseService.removeByIds(ids);
        return JsonMsgDataBean.buildSuccess();
    }

    @PutMapping("/update/batch")
    public JsonMsgDataBean updateBatch(@RequestBody List<E> entities) {
        baseService.updateBatchById(entities);
        return JsonMsgDataBean.buildSuccess();
    }
}