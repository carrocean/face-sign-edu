package com.face.sign.common.util.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @title: jwt鉴权实现模块
 */
@Component
public class JwtUtils {

    // token时效：30分钟
    private Integer jwtDefaultExpire = 1000 * 60 * 30;

    private String jwtDefaultSecret = "face-sign-eud-theauthenticationtokenlistfunctioncomponentof" +
            "thecomponentlistinformationoftheback-endtechnologyframeworkinthetechnologyassetlistofsinyattatechnologyco.," +
            "ltd.wasdevelopedbyzhfofthesinyattamicroserviceplatformoftherhu&dceuinteengrofsinyattatechnologyco.,ltd";

    // 签名哈希的密钥，标准规范中对各种加密算法的secretKey的长度有如下要求：
    //HS256：要求至少 256 bits (32 bytes)
    //HS384：要求至少384 bits (48 bytes)
    //HS512：要求至少512 bits (64 bytes)
    //RS256 and PS256：至少2048 bits
    //RS384 and PS384：至少3072 bits
    //RS512 and PS512：至少4096 bits
    //ES256：至少256 bits (32 bytes)
    //ES384：至少384 bits (48 bytes)
    //ES512：至少512 bits (64 bytes)

    /**
     * 根据用户id和昵称生成token
     *
     * @param content  签名主体信息
     * @param id       编号
     * @param userName 用户名
     * @return JWT规则生成的token
     */
    public String getJwtToken(String content, String id, String userName) {
        String JwtToken = Jwts.builder()
                .subject(content) // 设置主题（通常是用户标识）
                .issuedAt(new Date()) // 设置签发时间
                .expiration(new Date(System.currentTimeMillis() + jwtDefaultExpire)) // 设置过期时间
                .claim("id", id) // 添加自定义声明
                .claim("userName", userName)
                .signWith(Keys.hmacShaKeyFor(jwtDefaultSecret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256) // 设置签名密钥
                .compact(); // 生成紧凑的 JWT 字符串
        return JwtToken;
    }

    /**
     * 判断token是否存在与有效
     *
     * @param jwtToken token字符串
     * @return 如果token有效返回true，否则返回false
     */
    public Jws<Claims> veifyJwtToken(String jwtToken) {
        // 传入Key对象
        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtDefaultSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(jwtToken);
        return claimsJws;
    }

}
