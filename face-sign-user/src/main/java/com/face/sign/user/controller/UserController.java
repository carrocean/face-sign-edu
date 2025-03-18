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

import java.util.List;

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
        UserEntity loginUser = userService.login(user.getUsername(), user.getPassword(), user.getLastLoginIp());
        if (loginUser != null) {
            String token = jwtUtils.getJwtToken(loginUser.getUsername(), loginUser.getUserId().toString(), loginUser.getUsername());
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
        boolean result = userService.register(user);
        return JsonMsgDataBean.buildSuccess();
    }

    /**
     * 根据用户 ID 获取用户信息
     *
     * @param userId 用户 ID
     * @return 用户信息
     */
    @GetMapping("/getUserById/{userId}")
    public JsonMsgDataBean getUserById(@PathVariable Integer userId) {
        UserEntity user = userService.getUserById(userId);
        return JsonMsgDataBean.buildSuccess(user);

    }

    /**
     * 获取所有用户列表
     *
     * @return 用户列表
     */
    @PostMapping("/list")
    public JsonMsgDataBean getAllUsers(@RequestBody UserEntity user) {
        List<UserEntity> users = userService.getAllUsers();
        return JsonMsgDataBean.buildSuccess(users);
    }

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public JsonMsgDataBean updateUser(@RequestBody UserEntity user) {
        boolean result = userService.updateUser(user);
        return JsonMsgDataBean.buildSuccess();
    }

    /**
     * 删除用户
     *
     * @param userId 用户 ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{userId}")
    public JsonMsgDataBean deleteUser(@PathVariable Integer userId) {
        boolean result = userService.deleteUser(userId);
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
    public JsonMsgDataBean resetPassword(@RequestParam Integer userId, @RequestParam String newPassword) {
        boolean result = userService.resetPassword(userId, newPassword);
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
    public JsonMsgDataBean updateUserStatus(@PathVariable Integer userId, @RequestParam Integer status) {
        boolean result = userService.updateUserStatus(userId, status);
        return JsonMsgDataBean.buildSuccess();
    }


}





























