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

    @Autowired
    private IUserService userService;


    /**
     * 用户登录
     *
     * @param user 用户登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public JsonMsgDataBean login(@RequestBody UserEntity user) {
        UserEntity loginUser = userService.login(user);
        if (loginUser != null) {
            String token = JwtUtils.getJwtToken(loginUser.getAccount(), loginUser.getId().toString(), loginUser.getAccount());
            UserVo userVo = ConvertUtil.entityToVo(loginUser, UserVo.class);
            userVo.setToken(token);
            return JsonMsgDataBean.buildSuccess(userVo);
        }
        return JsonMsgDataBean.buildFail("账号或密码错误");
    }

    /**
     * 用户注册
     *
     * @param user 用户注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public JsonMsgDataBean register(@RequestBody UserEntity user) {
        int result = userService.register(user);
        if (result > 0) {
            return JsonMsgDataBean.buildSuccess();
        }
        return JsonMsgDataBean.buildFail("注册失败");
    }

}





























