package com.xbt.server.util;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 认证工具类 - 从请求中获取用户信息
 */
public class AuthUtils {

    private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);

    /**
     * 获取当前用户ID
     * 
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        Long userId = (Long) request.getSession().getAttribute("userId");
        logger.info("AuthUtils: Getting userId from session. Session ID: {}, userId: {}", request.getSession().getId(),
                userId);
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        return userId;
    }

    /**
     * 判断当前用户是否为教师
     * 
     * @return boolean
     */
    public static boolean isTeacher() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        Object roleObject = request.getSession().getAttribute("role");
        logger.info("AuthUtils: Checking for TEACHER role. Session ID: {}, Role from session: {}",
                request.getSession().getId(), roleObject);

        if (roleObject == null) {
            logger.warn("Role attribute is NULL in session.");
            return false;
        }

        String role = roleObject.toString();
        boolean isTeacher = "TEACHER".equals(role);
        logger.info("AuthUtils: Role check result for 'TEACHER': {}", isTeacher);
        return isTeacher;
    }
}