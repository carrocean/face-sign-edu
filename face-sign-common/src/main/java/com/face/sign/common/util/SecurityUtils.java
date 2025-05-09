package com.face.sign.common.util;

import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/**
 * 安全服务工具类
 */

@Component
public class SecurityUtils {

    /**
     * 生成BCryptPasswordEncoder密码
     * @param password 密码
     * @return 加密字符串
     */
    public static String encodePassword(String password)
    {
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }

    /**
     * 判断密码是否相同
     * @param rawPassword 真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword)
    {
        return encodedPassword.equals(DigestUtils.md5DigestAsHex(rawPassword.getBytes()));
    }
}
