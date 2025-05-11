package com.face.sign.common.util.jwt;

import lombok.Data;

/**
 * JwtUser
 */
@Data
public class JwtUser {

    private static ThreadLocal<JwtUser> jwtUserThreadLocal = new ThreadLocal<>();

    private String id;

    private String userName;

    public static void setUser(JwtUser user) {
        jwtUserThreadLocal.set(user);
    }

    public static JwtUser getUser() {
        return jwtUserThreadLocal.get();
    }

    public static void clear() {
        jwtUserThreadLocal.remove();
    }
}
