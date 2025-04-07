package com.face.sign.common.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
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

    @PostMapping("/list")
    public JsonMsgDataBean getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean fuzzySearch,
            @RequestBody(required = false) Map<String, Object> searchForm) {
        IPage<E> pageResult = baseService.page(page, size, fuzzySearch, searchForm);
        return JsonMsgDataBean.buildSuccess(pageResult);
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