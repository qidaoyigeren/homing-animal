package org.example.hominganimal.infrastructure.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hominganimal.infrastructure.persistence.dataobject.SysUserDO;
import org.example.hominganimal.infrastructure.persistence.mapper.SysUserMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtUtil jwtUtil;
    private final SysUserMapper userMapper;
    @Override
    protected void doFilterInternal
            (HttpServletRequest request,
             HttpServletResponse response,
             FilterChain filterChain)
            throws ServletException, IOException {
        String token=extractToken(request);
        if(StringUtils.hasText(token)&&jwtUtil.isValid(token)){
            Long userId=jwtUtil.getUserId(token);
            String username= jwtUtil.getUsername(token);
            SysUserDO user=userMapper.selectOne(
                    new LambdaQueryWrapper<SysUserDO>()
                            .eq(SysUserDO::getId,userId)
                            .eq(SysUserDO::getStatus,1)
            );
            if(user!=null){
                LoginUser loginUser=new LoginUser(
                        userId,username,"",
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
                UsernamePasswordAuthenticationToken authenticationToken=
                        new UsernamePasswordAuthenticationToken
                                (loginUser,null,loginUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if(StringUtils.hasText(header)&&header.startsWith(BEARER_PREFIX)){
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
