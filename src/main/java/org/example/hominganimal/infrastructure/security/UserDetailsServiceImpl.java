package org.example.hominganimal.infrastructure.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.hominganimal.infrastructure.persistence.dataobject.SysUserDO;
import org.example.hominganimal.infrastructure.persistence.mapper.SysUserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final SysUserMapper userMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUserDO user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUserDO>()
                        .eq(SysUserDO::getUsername, username)
                        .eq(SysUserDO::getStatus, 1)
        );
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在"+username);
        }
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }
}
