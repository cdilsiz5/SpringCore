package com.epam.springcore.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("➡Incoming Request: [{}] {} | transactionId={}",
                request.getMethod(),
                request.getRequestURI(),
                MDC.get("transactionId"));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex != null) {
            log.error("Request Failed: [{}] {} | status={} | error={} | transactionId={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    ex.getMessage(),
                    MDC.get("transactionId"));
        } else {
            log.info("Request Completed: [{}] {} | status={} | transactionId={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    MDC.get("transactionId"));
        }
    }
}
