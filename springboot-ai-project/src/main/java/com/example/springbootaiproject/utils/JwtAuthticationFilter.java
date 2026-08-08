package com.example.springbootaiproject.utils;

import cn.hutool.json.JSONUtil;
import com.example.springbootaiproject.DTO.response.UserLoginResponseDTO;
import com.example.springbootaiproject.common.ResultCode;
import com.example.springbootaiproject.config.SecurityConfig;
import com.example.springbootaiproject.enumClass.UserStatus;
import com.example.springbootaiproject.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
//这个类必须在 Spring 启动时完成初始化，因为它用了 ApplicationContextAware：
public class JwtAuthticationFilter extends OncePerRequestFilter {


    @Resource
    private UserService userService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        //检查是否为公开路径
        return SecurityConfig.isPublicPATH(requestURI);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 获取请求的URI和方法
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
//        System.out.println(requestUri);
//        System.out.println(method);
        // 1. 提取token
        String token = JwtTokenUtil.extractTokenFromRequest(request);
        if(StringUtils.hasText(token)) {// 检查token是否有效
            // 2.验证token，获取用户信息
            JwtTokenUtil.TokenVerificationResult validationResult = JwtTokenUtil.validateToken(token);
            if (validationResult != null && validationResult.isValid()) {
                // 3. 查询用户信息验证用户的状态
                UserLoginResponseDTO.UserDetailResponseDTO user = userService.getUserById(validationResult.getUserId());
//                System.out.println(JSONUtil.parseObj(user));
                if (user != null && UserStatus.NORMAL.getCode().equals(user.getStatus())) {
                    // 4. 创建Spring Security认证对象，权限列表(固定写法)
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(//此方法固定写法，创建一个单元素的列表
                            new SimpleGrantedAuthority("ROLE_" + validationResult.getRoleType())
                    );

                    // 创建UsernamePasswordAuthenticationToken对象
                    UsernamePasswordAuthenticationToken authcation = new UsernamePasswordAuthenticationToken(
                            validationResult.getUsername(), // 用户名作为主体
                            null,
                            authorities
                    );

                    // 设置认证信息到Spring Securtity上下文---------------------------很重要
                    SecurityContextHolder.getContext().setAuthentication(authcation);

                    // 将token存储到请求属性中
                    request.setAttribute("jwtToken", token);//到此才正式认证成功，后续的请求会根据此token进行权限校验 （eg 获取当前用户/流式接口/知识分类/情绪日志等都会从request中获取这个定义的jwtToken）
                } else {
                    clearSecurityContext();
                    ResponseUtil.writeError(response, ResultCode.TOKEN_ACCESS_FORBIDDEN);
                    return;
                }
            } else {
                clearSecurityContext();
                ResponseUtil.writeError(response, ResultCode.TOKEN_INVALID);
                return;
            }
        }else {
            // 清理上下文   Filter 层拿到的是原始的 HttpServletResponse 对象，可以直接往里面写数据，不需要像 Controller 那样通过返回值让 Spring 帮你序列化。 所以说，在 Filter 层可以手动设置响应状态码、响应头、响应体等。直接return终止后续处理。即可
            clearSecurityContext();
            ResponseUtil.writeError(response, ResultCode.ACCESS_UNAUTHORIZED);
            return;
        }
        // 继续过滤器链 (让代码正常进入controller层，执行逻辑)
        filterChain.doFilter(request, response);
    }
    // 清理Spring Security上下文
    private void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
