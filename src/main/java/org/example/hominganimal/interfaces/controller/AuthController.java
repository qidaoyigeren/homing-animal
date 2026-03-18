package org.example.hominganimal.interfaces.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hominganimal.domain.shared.exception.BusinessException;
import org.example.hominganimal.domain.shared.exception.ErrorCode;
import org.example.hominganimal.infrastructure.persistence.dataobject.SysUserDO;
import org.example.hominganimal.infrastructure.persistence.mapper.SysUserMapper;
import org.example.hominganimal.infrastructure.security.JwtProperties;
import org.example.hominganimal.infrastructure.security.JwtUtil;
import org.example.hominganimal.interfaces.dto.request.LoginRequest;
import org.example.hominganimal.interfaces.dto.request.RegisterRequest;
import org.example.hominganimal.interfaces.dto.response.ApiResponse;
import org.example.hominganimal.interfaces.dto.response.LoginVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    @PostMapping("/register")
    public ApiResponse<Void>register(@Valid@RequestBody RegisterRequest registRequest){
        Long cnt=userMapper.selectCount(
                new LambdaQueryWrapper<SysUserDO>()
                        .eq(SysUserDO::getUsername,registRequest.getUsername())
        );
        if(cnt>0){
            throw new BusinessException(ErrorCode.USER_ALREADY_EXIST);
        }
        SysUserDO user=new SysUserDO();
        user.setUsername(registRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registRequest.getPassword()));// BCrypt 加密
        user.setNickname
                (registRequest.getNickname()!=null?
                        registRequest.getNickname():registRequest.getUsername());//默认昵称为用户名
        user.setEmail(registRequest.getEmail());
        user.setPhone(registRequest.getPhone());
        user.setStatus(1);
        userMapper.insert(user);
        log.info("用户注册成功");
        return ApiResponse.ok();
    }
    @PostMapping("/login")
    public ApiResponse<LoginVO>login(@Valid@RequestBody LoginRequest loginRequest){
        SysUserDO userDO=userMapper.selectOne(
                new LambdaQueryWrapper<SysUserDO>()
                        .eq(SysUserDO::getUsername,loginRequest.getUsername())
                        .eq(SysUserDO::getStatus,1)
        );
        if(userDO==null){
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if(!passwordEncoder.matches(loginRequest.getPassword(),userDO.getPassword())){
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }
        String token=jwtUtil.generateToken(userDO.getId(),userDO.getUsername());
        long expiresIn = jwtProperties.getExpirationMs() / 1000;
        return ApiResponse.ok(new LoginVO(token,expiresIn,userDO.getId(),userDO.getUsername(),userDO.getNickname()));
    }
    @PostMapping("/refresh")
    public ApiResponse<LoginVO>refresh(@RequestHeader("Authorization") String bearerToken){
        String token = bearerToken.replace("Bearer ", "");
        Boolean isValid=jwtUtil.isValid(bearerToken);
        if(!isValid){
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        String username=jwtUtil.getUsername(token);
        Long userId = jwtUtil.getUserId(token);
        SysUserDO userDO = userMapper.selectOne(new LambdaQueryWrapper<SysUserDO>()
                .eq(SysUserDO::getId, userId)
                .eq(SysUserDO::getUsername, username));
        if(username==null||userDO.getStatus()!=1){
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户不存在或已被禁用");
        }
        String newToken = jwtUtil.generateToken(userId, username);
        long expiresIn = jwtProperties.getExpirationMs() / 1000;
        return ApiResponse.ok(new LoginVO(newToken, expiresIn, userId, username, userDO.getNickname()));
    }
}
