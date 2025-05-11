package com.face.sign.common.util.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class JwtFilter implements HandlerInterceptor {

    private static final String AUTH_HEADER = "token";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 因为第一次请求是OPTIONS的请求,所以根本就没有token存在，我们就让他通过，不管他
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        // 从请求头中获取JWT
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "无效的Token");
            return false;
        }

        String jwtToken = authHeader;
        try {
            JwtUtils.veifyJwtToken(jwtToken);
            JwtUser.setUser(JwtUtils.getJwtUser(jwtToken));
            return true; // 验证通过，继续处理请求
        } catch (ExpiredJwtException e) {
            log.error(e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token已过期");
        } catch (JwtException e) {
            log.error(e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "无效的Token");
        }
        return false;
    }
}