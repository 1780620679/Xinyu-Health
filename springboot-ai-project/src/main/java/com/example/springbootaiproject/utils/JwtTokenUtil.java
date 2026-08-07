package com.example.springbootaiproject.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.springbootaiproject.config.JwtConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

@Component
public class JwtTokenUtil implements ApplicationContextAware {
    // 实现ApplicationContextAware接口，
    // 主要为了把JwtTokenUtil类设置为Spring管理的Bean，方便在静态方法中使用Spring容器管理的Bean
    // 把JwtTokenUtil类设置到上下文当中，便于其他类调用！！！！！！！！！！
    //这个类必须在 Spring 启动时完成初始化，因为它用了 ApplicationContextAware：

    // 自定义签发者
    private static final String ISSUER = "xiao-hao";

    private static ApplicationContext applicationContext;
    // 用于再静态工具类中获取Spring容器管理的Bean
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) { // 重写ApplicationContextAware接口的方法，设置上下文
        JwtTokenUtil.applicationContext = applicationContext;
    }

    private static JwtConfig getJwtConfig() {
        return applicationContext.getBean(JwtConfig.class);
    }; // 获取JwtConfig的实例

    // 生成token的方法
    public static String generateToken(Long userId, String username, Integer roleType) {
        try {
            // 获取jwt的配置
            JwtConfig jwtConfig = getJwtConfig();
            // 生成签名的算法
            Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
            // 生成过期时间
            Date expiration = new Date(System.currentTimeMillis() + jwtConfig.getExpiration());

            String token = JWT.create()
                    .withClaim("userId", userId)
                    .withClaim("username", username)
                    .withClaim("roleType", roleType)
                    .withExpiresAt(expiration) // 设置过期时间
                    .withIssuedAt(new Date()) // 设置签发时间
                    .withIssuer(ISSUER) // 设置签发者
                    .sign(algorithm);
            return token;
        } catch (Exception e) {
            throw new RuntimeException("生成token 失败: " + e);
        }
    }

    // 提取token
    public static String extractTokenFromRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String tokenHeader = request.getHeader("token");//这是token是与前端协议好的名字
        if (StringUtils.hasText(tokenHeader)) {//判断是否存在
            return tokenHeader;
        }
        return null;
    }

    // 获取当前token
    public static String getCurrentToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();//获取当前请求的属性
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String token = (String) request.getAttribute("jwtToken");//从请求属性中获取token，这是在JwtAuthticationFilter中设置的
            if (token != null) {
                return token;
            }

            // 备用方案： 从请求头直接获取
            String headerToken =  extractTokenFromRequest(request);
            return headerToken;
        }
        return null;
    }

    // 验证token，获取用户信息
    public static TokenVerificationResult validateToken(String token) {
        // 验证token有效性
        DecodedJWT jwt = verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();
        String username = jwt.getClaim("username").asString();

        // 角色类型，处理 1 或 '1' 的情况
        Integer roleType = null;
        try {
            roleType = jwt.getClaim("roleType").asInt();
        } catch (Exception e) {
            String roleTypeStr = jwt.getClaim("roleType").asString();
            if (StringUtils.hasText(roleTypeStr)) {
                roleType = Integer.valueOf(roleTypeStr);
            }
        }
        if (userId != null && StringUtils.hasText(username) && roleType != null) {
            return new TokenVerificationResult(userId, username, roleType, true);
        }
        return null;
    }

    // 验证token有效性（解码token，检查签名和过期时间）返回值是jwt插件的Decoded对象
    public static DecodedJWT verifyToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new JWTVerificationException("Token不能为空");
        }
        // token解码
        JwtConfig jwtConfig = getJwtConfig();
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        JWTVerifier verifier =  JWT.require(algorithm)// 验证器
                .withIssuer(ISSUER)
                .build();
        return  verifier.verify(token);
    }

    // Token验证结果封装类
    @Getter
    public static class TokenVerificationResult {
        private final Long userId;
        private final String username;
        private final Integer roleType;
        private final boolean valid;

        public TokenVerificationResult(Long userId, String username, Integer roleType, boolean valid) {
            this.userId = userId;
            this.username = username;
            this.roleType = roleType;
            this.valid = valid;
        }
    }
}

