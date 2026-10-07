package com.bahar.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 安全中心配置
 *
 * CopyRight https://www.bahar.cn
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    /**
     * 配置安全过滤链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF禁用，因为使用token模式
                .csrf().disable()
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 过滤请求
                .authorizeHttpRequests(authz -> authz
                        // 业务API由自定义拦截器鉴权
                        .antMatchers("/clientApi/**", "/backendApi/**", "/merchantApi/**").permitAll()
                        .antMatchers(HttpMethod.GET,
                                "/",
                                "/static/**",
                                "/*.html",
                                "/**/*.html",
                                "/**/*.css",
                                "/**/*.js",
                                // 静态资源：字体与图片。缺少这些规则时，
                                // .woff/.ttf/.png 等会被 anyRequest().authenticated() 拦成 403，
                                // 表现为后台图标全部不显示。
                                "/**/*.woff",
                                "/**/*.woff2",
                                "/**/*.ttf",
                                "/**/*.eot",
                                "/**/*.svg",
                                "/**/*.png",
                                "/**/*.jpg",
                                "/**/*.jpeg",
                                "/**/*.gif",
                                "/**/*.ico",
                                "/profile/**"
                        ).permitAll()
                        .antMatchers("/swagger-ui.html").permitAll()
                        .antMatchers("/swagger-ui/**").permitAll()
                        .antMatchers("/swagger-resources/**").permitAll()
                        .antMatchers("/webjars/**").permitAll()
                        .antMatchers("/v3/api-docs/**").permitAll()
                        .antMatchers("/druid/**").permitAll()
                        // 除上面外的所有请求全部需要鉴权认证
                        .anyRequest().authenticated()
                )
                // 安全响应头
                // 注意：这里不要再加 contentSecurityPolicy("script-src 'self'")。
                // 后台 Admin 是 Vue 2 运行时编译版，组件模板靠 new Function 现场编译，
                // 而 script-src 'self' 既不给 'unsafe-eval' 也不给 'unsafe-inline'：
                //   - index.html 里那段内联的主题脚本会被直接拦掉
                //   - new Function 被禁后 Vue 的 createFunction 静默返回 noop
                //     （vue.min.js 是生产版，连告警都不打）
                // 结果就是所有组件 render 变空、整页白屏，控制台只留一条
                // "Executing inline script violates CSP" —— 极难往白屏上联想。
                // 真要上 CSP，得先改成运行时预编译模板（vue-loader 构建产物）再谈。
                .headers(headers -> headers
                        .frameOptions().deny()
                        .xssProtection().and()
                );

        return http.build();
    }

    /**
     * 强散列哈希加密实现
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
