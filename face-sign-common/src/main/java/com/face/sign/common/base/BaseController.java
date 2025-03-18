package com.face.sign.common.base;

import com.face.sign.common.util.JsonMsgDataBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class BaseController<E extends BaseEntity, S extends IBaseService<E>> {
    protected S baseService;

    public void init(S baseService) {
        this.baseService = baseService;
    }

    @PostMapping("/save")
    public JsonMsgDataBean save(@RequestBody E entity) {
        baseService.save(entity);
        return JsonMsgDataBean.buildSuccess();
    }

    @DeleteMapping("/delete/{id}")
    public JsonMsgDataBean deleteById(@PathVariable Integer id) {
        baseService.deleteById(id);
        return JsonMsgDataBean.buildSuccess();
    }

    @PutMapping("/update")
    public JsonMsgDataBean update(@RequestBody E entity) {
        baseService.update(entity);
        return JsonMsgDataBean.buildSuccess();
    }

    @GetMapping("/getById/{id}")
    public JsonMsgDataBean getById(@PathVariable Integer id) {
        E entity = baseService.getById(id);
        return JsonMsgDataBean.buildSuccess();
    }

    @GetMapping("/list")
    public JsonMsgDataBean getAll() {
        List<E> entities = baseService.getAll();
        return JsonMsgDataBean.buildSuccess(entities);
    }
}