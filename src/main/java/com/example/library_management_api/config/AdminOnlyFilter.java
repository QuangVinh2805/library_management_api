//package com.example.library_management_api.config;
//
//import com.example.library_management_api.service.UserService;
//import com.example.library_management_api.util.SecurityUtil;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@Order(1)
//public class AdminOnlyFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private UserService userService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain chain)
//            throws IOException, ServletException {
//
//        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        if (!request.getRequestURI().startsWith("/api/admin")) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        Long userId = SecurityUtil.getCurrentUserId();
//        System.out.println("FILTER userId = " + userId);
//
//
//        if (userId == null || !userService.isAdmin(userId)) {
//            response.setStatus(HttpStatus.FORBIDDEN.value());
//            System.out.println("isAdmin = " + userService.isAdmin(userId));
//            response.getWriter().write("Admin only");
//            return;
//        }
//
//
//        chain.doFilter(request, response);
//    }
//}