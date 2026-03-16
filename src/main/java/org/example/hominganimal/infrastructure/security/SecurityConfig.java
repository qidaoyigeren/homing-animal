package org.example.hominganimal.infrastructure.security;


import lombok.RequiredArgsConstructor;
import org.example.hominganimal.interfaces.dto.response.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;
    //TODO:这里分配过滤器,还有就是因为可能版本原因导入不了原本的ObjectMapper包，后面如果有bug可以来看看
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 关闭 CSRF（前后端分离 + JWT 不需要）
                .csrf(csrf -> csrf.disable())

                // 无状态 Session（JWT 方案不使用 Session）
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 路由权限配置
                .authorizeHttpRequests(auth -> auth
                        // 公开接口：注册、登录、萤石回调、WebSocket
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/ezviz/callback").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        // 其余接口需要认证
                        .anyRequest().authenticated()
                )
                // 未认证时返回 JSON（而非重定向）
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(401);
                            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            res.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            res.getWriter().write(
                                    objectMapper.writeValueAsString(ApiResponse.fail(401, "未登录或Token已过期"))
                            );
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(403);
                            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            res.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            res.getWriter().write(
                                    objectMapper.writeValueAsString(ApiResponse.fail(403, "权限不足"))
                            );
                        })
                )

                // 在 UsernamePasswordAuthenticationFilter 之前插入 JWT 过滤器
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
