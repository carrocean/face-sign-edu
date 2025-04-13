package com.face.sign.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.common.util.exception.BizException;
import com.face.sign.user.entity.AdministratorEntity;
import com.face.sign.user.entity.UserEntity;
import com.face.sign.user.mapper.AdministratorMapper;
import com.face.sign.user.service.IAdministratorService;
import com.face.sign.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdministratorServiceImpl extends BaseServiceImpl<AdministratorEntity, AdministratorMapper> implements IAdministratorService {

    @Autowired
    private AdministratorMapper administratorMapper;

    @Autowired
    private IUserService userService;

    @Override
    public AdministratorEntity getByUserId(Long id) {
        return getOne(Wrappers.<AdministratorEntity>lambdaQuery().eq(AdministratorEntity::getUserId, id));
    }

    @Override
    public void addAdministrator(AdministratorEntity administrator) {
        if(administrator.getName().isEmpty()) {
            throw new BizException("管理员姓名不可为空");
        }

        UserEntity user = new UserEntity();
        user.setAccount(administrator.getName());
        user.setPassword(administrator.getName());
        user.setRole("admin");
        user.setStatus(1);
        userService.register(user);

        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", user.getAccount());
        UserEntity registeredUser = userService.getOne(queryWrapper);
        
        if(registeredUser != null) {
            administrator.setUserId(registeredUser.getId());
            administratorMapper.insert(administrator);
        } else {
            throw new BizException("用户注册失败");
        }
    }
}