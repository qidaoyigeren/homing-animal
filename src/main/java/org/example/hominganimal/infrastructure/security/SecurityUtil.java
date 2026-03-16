package org.example.hominganimal.infrastructure.security;

import org.example.hominganimal.domain.shared.exception.BusinessException;
import org.example.hominganimal.domain.shared.exception.ErrorCode;
import org.example.hominganimal.infrastructure.security.LoginUser;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 * 提供静态方法，方便在 Controller 中获取当前登录用户的 ID
 */
public class SecurityUtil {

    /**
     * 获取当前登录用户的 ID（未登录时抛出异常）
     */
    public static Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (principal instanceof LoginUser loginUser) {
            return loginUser.getUserId();
        }
        throw new BusinessException(ErrorCode.UNAUTHORIZED);
    }

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (principal instanceof LoginUser loginUser) {
            return loginUser.getUsername();
        }
        throw new BusinessException(ErrorCode.UNAUTHORIZED);
    }
}