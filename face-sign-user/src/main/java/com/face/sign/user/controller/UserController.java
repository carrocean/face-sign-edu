package com.face.sign.user.controller;

import com.face.sign.common.base.BaseController;
import com.face.sign.common.util.BaseUtil;
import com.face.sign.common.util.ExcelUtils;
import com.face.sign.common.util.IpUtils;
import com.face.sign.common.util.JsonMsgDataBean;
import com.face.sign.common.util.jwt.JwtUser;
import com.face.sign.common.util.jwt.JwtUtils;
import com.face.sign.system.service.ISystemLogService;
import com.face.sign.user.bean.dto.ExcelLogDto;
import com.face.sign.user.entity.UserEntity;
import com.face.sign.user.entity.vo.UserVo;
import com.face.sign.user.qo.UserQo;
import com.face.sign.user.service.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/face/sign/user")
public class UserController extends BaseController<UserEntity, IUserService> {

    @Autowired
    private IUserService userService;

    @Autowired
    ISystemLogService systemLogService;

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
            systemLogService.logOperation(loginUser.getId().toString(), "登录", IpUtils.getCurrentClientIp());
            UserVo userVo = BaseUtil.entityToVo(loginUser, UserVo.class);
            userVo.setToken(token);
            return JsonMsgDataBean.buildSuccess(userVo);
        }
        return JsonMsgDataBean.buildFail("账号或密码错误");
    }

    /**
     * 用户登出
     * @return
     */
    @PostMapping("/logout")
    public JsonMsgDataBean logout() {
        systemLogService.logOperation(JwtUser.getUser().getId(), "登出", IpUtils.getCurrentClientIp());
        return JsonMsgDataBean.buildSuccess();
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
        systemLogService.logOperation(JwtUser.getUser().getId(), "用户注册", IpUtils.getCurrentClientIp());
        if (result > 0) {
            return JsonMsgDataBean.buildSuccess();
        }
        return JsonMsgDataBean.buildFail("注册失败");
    }

    /**
     * 修改密码
     *
     * @param user 用户注册信息
     * @return 注册结果
     */
    @PostMapping("/resetPassword")
    public JsonMsgDataBean resetPassword(@RequestBody UserQo user, @RequestHeader String token) {
        userService.resetPassword(user, token);
        systemLogService.logOperation(JwtUser.getUser().getId(), "修改密码", IpUtils.getCurrentClientIp());
        return JsonMsgDataBean.buildSuccess();
    }

    @PostMapping("/export-log")
    public void exportLogs(HttpServletResponse response) {
        List<ExcelLogDto> records = userService.exportLogs();
        systemLogService.logOperation(JwtUser.getUser().getId(), "导出日志", IpUtils.getCurrentClientIp());
        ExcelUtils.exportExcel(response, "系统日志", ExcelLogDto.class, records);
    }

}





























