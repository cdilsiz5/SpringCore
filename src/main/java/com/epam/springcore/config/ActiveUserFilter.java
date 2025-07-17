package com.epam.springcore.config;

import com.epam.springcore.exception.UnauthorizedException;
import com.epam.springcore.session.UserSessionRegistry;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor

public class ActiveUserFilter implements Filter {

    private static final String[] WHITELIST = {
            "/api/epam/v1/trainee",   // POST: CreateTrainee
            "/api/epam/v1/trainer"    // POST: CreateTrainer
    };

     @Autowired
     private  UserSessionRegistry userSessionRegistry;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        String username = httpRequest.getHeader("X-Username");

        // Whitelist check
        boolean isWhitelisted = ("/api/epam/v1/trainee".equals(path) && "POST".equalsIgnoreCase(method))
                || ("/api/epam/v1/trainer".equals(path) && "POST".equalsIgnoreCase(method));

        if (!isWhitelisted) {
            if (username == null || !userSessionRegistry.isActive(username)) {
                throw new UnauthorizedException("Unauthorized: user is not active or missing.");
            }
        }

        chain.doFilter(request, response);
    }
}
