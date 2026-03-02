package com.example.library_management_api.config;

import com.example.library_management_api.models.User;
import com.example.library_management_api.models.Reader;
import com.example.library_management_api.repository.UserRepository;
import com.example.library_management_api.repository.ReaderRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

@Component
@Order(0)
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

//        SecurityContextHolder.clearContext();

        String token = null;

        // 1️⃣ Header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 2️⃣ Query param
        if (token == null || token.isBlank()) {
            token = request.getParameter("token");
        }

        if (token != null && !token.isBlank()) {

            // ===== USER FIRST =====
            List<User> users = userRepository.findByTokenList(token);
            if (!users.isEmpty()) {
                User user = users.get(0); // 👈 luôn lấy bản ghi đầu

                Authentication auth =
                        new UsernamePasswordAuthenticationToken(
                                user.getId(),
                                "USER",
                                List.of()
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
//                filterChain.doFilter(request, response);
//                return;
            }

            // ===== READER =====
            List<Reader> readers = readerRepository.findByTokenList(token);
            if (!readers.isEmpty()) {
                Reader reader = readers.get(0);

                Authentication auth =
                        new UsernamePasswordAuthenticationToken(
                                reader.getId(),
                                "READER",
                                List.of()
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}

