package com.example.library_management_api.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();

    public static void setCurrentUserId(Long userId) {
        CURRENT_USER.set(userId);
    }

//    public static Long getCurrentUserId() {
//        return CURRENT_USER.get();
//    }

    public static void clear() {
        CURRENT_USER.remove();
    }

    public static Long getCurrentUserId() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            return null;
        }

        return (Long) auth.getPrincipal();
    }

    public static String getCurrentType() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        return (String) auth.getCredentials(); // USER | READER
    }
}

