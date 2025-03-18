package com.face.sign.user.controller;

import com.face.sign.common.base.BaseController;
import com.face.sign.common.util.ConvertUtil;
import com.face.sign.common.util.JsonMsgDataBean;
import com.face.sign.common.util.jwt.JwtUtils;
import com.face.sign.user.entity.UserEntity;
import com.face.sign.user.entity.vo.UserVo;
import com.face.sign.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/face/sign/user")
public class UserController extends BaseController<UserEntity, IUserService> {

    IUserService userService;

    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
        init(userService);
    }

    @Autowired
    JwtUtils jwtUtils;


    /**
     * 用户登录
     *
     * @param user 用户登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public JsonMsgDataBean login(@RequestBody UserEntity user) {
        UserEntity loginUser = userService.login(user.getUserName(), user.getPassword(), user.getLastLoginIp());
        if (loginUser != null) {
            String token = jwtUtils.getJwtToken(loginUser.getUserName(), loginUser.getId().toString(), loginUser.getUserName());
            UserVo userVo = ConvertUtil.entityToVo(loginUser, UserVo.class);
            userVo.setToken(token);
            return JsonMsgDataBean.buildSuccess(userVo);
        }
        return JsonMsgDataBean.buildFail("用户名或密码错误");
    }

    /**
     * 用户注册
     *
     * @param user 用户注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public JsonMsgDataBean register(@RequestBody UserEntity user) {
        userService.register(user);
        return JsonMsgDataBean.buildSuccess();
    }

    /**
     * 重置用户密码
     *
     * @param userId      用户 ID
     * @param newPassword 新密码
     * @return 重置结果
     */
    @PutMapping("/resetPassword")
    public JsonMsgDataBean resetPassword(@RequestParam Long userId, @RequestParam String newPassword) {
        userService.resetPassword(userId, newPassword);
        return JsonMsgDataBean.buildSuccess();
    }

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 用户状态
     * @return 操作结果
     */
    @PutMapping("/status/{userId}")
    public JsonMsgDataBean updateUserStatus(@PathVariable Long userId, @RequestParam Integer status) {
        userService.updateUserStatus(userId, status);
        return JsonMsgDataBean.buildSuccess();
    }


}





























