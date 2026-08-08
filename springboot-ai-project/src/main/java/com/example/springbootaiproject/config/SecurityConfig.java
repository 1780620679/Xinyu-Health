package com.example.springbootaiproject.config;

import cn.hutool.core.text.AntPathMatcher;
import com.example.springbootaiproject.utils.JwtAuthticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

@Configuration//标注这是一个配置类，用于配置Spring Security的Web安全配置
@EnableWebSecurity//标注开启Web安全配置
@EnableMethodSecurity//标注开启方法安全配置 ，用于配置方法级别的权限
public class SecurityConfig {

    //定义一些不需要登录即可访问的路径
    private static final String[] PUBLIC_PATHS = {
            "/",
            "/api/user/login",
            "/api/user/register",
            "/api/user/add",
            "/api/user/logout",
            // 知识文章浏览（前台）
            "/api/knowledge/article/page",
            "/api/knowledge/article/*",
            // 知识分类
            "/api/knowledge/category/tree",
            // 文件访问
            "/files/**"
    };

    //定义一个antPathMatcher对象，用于匹配路由路径
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    //判断请求路径是否在公开路径列表中
    public static Boolean isPublicPATH(String requestUri) {
        for (String publicPath : PUBLIC_PATHS) {
            if (antPathMatcher.match(publicPath, requestUri)) {
                return true;
            }
        }
        return false;
    }

    //这个过滤器会在每个请求进入Spring Security之前被调用，用于验证请求头中的JWT
    @Bean
    public JwtAuthticationFilter jwtAuthticationFilter() {
        return new JwtAuthticationFilter();
    }//所有安全相关的 Bean（过滤器、处理器等）都集中在这里声明，一目了然。如果散落到各个类上用 @Component，配置分散后不易维护。



    //配置Spring Security的过滤链，一般都是固定这样写的
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //禁用CSRF保护(API服务不需要) 这就是默认网页会有一个登录表单
                .csrf(AbstractHttpConfigurer::disable)
                //配置会话管理为无状态会话 （会话管理会通过JWT来实现）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 将安全上下文（包含Authentication）保存到 request 属性中（而非 session），保证 SSE/流式接口的异步分派(async dispatch)阶段
                // 仍能拿到 Authentication，避免流式结束时报 AuthorizationDeniedException: Access Denied
                .securityContext(context -> context
                        .securityContextRepository(new RequestAttributeSecurityContextRepository())
                )
                //配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        //设置公开的路径，不需要登录即可访问
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        //其他请求都需要登录
                        .anyRequest().authenticated()
                )
                //添加JWT认证过滤器，用于在请求头中提取JWT并验证   addFilterBefore(A, B) 的意思是 "把 A 过滤器排在 B 过滤器前面执行"。这样每个请求进来，先经过 JwtAuthticationFilter 验证 token，验证通过后才到 Spring Security 的默认认证过滤器。
                .addFilterBefore(jwtAuthticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
