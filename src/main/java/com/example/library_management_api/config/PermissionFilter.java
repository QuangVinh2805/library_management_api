package com.example.library_management_api.config;

import com.example.library_management_api.response.MyApiResponse;
import com.example.library_management_api.service.PermissionService;
import com.example.library_management_api.service.UserService;
import com.example.library_management_api.util.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Order(2)
public class PermissionFilter extends OncePerRequestFilter {

    @Autowired
    private PermissionService permissionService;

    private void writeJson(HttpServletResponse response,
                           HttpStatus status,
                           String message) throws IOException {

        if (response.isCommitted()) return;

        response.resetBuffer(); // ✅ CHỈ reset buffer
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = new ObjectMapper().writeValueAsString(
                MyApiResponse.error(status, message)
        );

        response.getWriter().write(json);
        response.getWriter().flush();
    }

    private static final List<String> PUBLIC_APIS = List.of(
            "/user/loginCms",
            "/user/loginReader",
            "/user/createReader",
            "/user/user/reset-password/send-link",
            "/user/user/reset-password/confirm",
            "/user/logout",
            "/user/readerLogout",
            "/health",
            "/swagger/**",
            "/v3/api-docs/**",
            "/uploads/image/**"
    );


    private boolean isPublicApi(String uri) {
        AntPathMatcher matcher = new AntPathMatcher();
        return PUBLIC_APIS.stream().anyMatch(p -> matcher.match(p, uri));
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {

        // OPTIONS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String uri = request.getRequestURI();
        String method = request.getMethod();

        // PUBLIC API
        if (isPublicApi(uri)) {
            chain.doFilter(request, response);
            return;
        }

        if ("GET".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            writeJson(response, HttpStatus.UNAUTHORIZED, "Bạn chưa đăng nhập");
            return;
        }

        if (!permissionService.checkPermission(userId, uri, method)) {
            writeJson(response, HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện tính năng này");
            return;
        }

        chain.doFilter(request, response);


    }



    }