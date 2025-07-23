// === ActiveUserFilter.java ===
package com.epam.springcore.config;

import com.epam.springcore.exception.UnauthorizedException;
import com.epam.springcore.session.UserSessionRegistry;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ActiveUserFilter implements Filter {

    private static UserSessionRegistry userSessionRegistry;

    public static void setUserSessionRegistry(UserSessionRegistry registry) {
        userSessionRegistry = registry;
    }

    private static final String[] WHITELIST = {
            "/api/epam/v1/users/trainee",
            "/api/epam/v1/users/trainer",
            "/api/epam/v1/users/login",
            "/swagger-ui",
            "/v3/api-docs",
            "/favicon.ico"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        String method = httpRequest.getMethod();
        String username = httpRequest.getHeader("X-Username");

        System.out.println("\uD83D\uDD12 Filter aktif: " + path + " [" + method + "]");

        if (!isWhitelisted(path, method)) {
            if (username == null || !userSessionRegistry.isActive(username)) {
                System.out.println("⛔ Engellendi: Kullanıcı yok veya aktif değil → " + username);
                throw new UnauthorizedException("Unauthorized: user is not active or missing.");
            } else {
                System.out.println("✅ Kullanıcı aktif: " + username);
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isWhitelisted(String path, String method) {
        if ("/api/epam/v1/users/trainee".equals(path) && "POST".equalsIgnoreCase(method)) return true;
        if ("/api/epam/v1/users/trainer".equals(path) && "POST".equalsIgnoreCase(method)) return true;
        if ("/api/epam/v1/users/login".equals(path) && "POST".equalsIgnoreCase(method)) return true;

        for (String whitelisted : WHITELIST) {
            if (path.startsWith(whitelisted)) return true;
        }
        return false;
    }
}
