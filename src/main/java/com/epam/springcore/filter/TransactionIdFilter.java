package com.epam.springcore.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TransactionIdFilter implements Filter {

    private static final String TRANSACTION_ID = "transactionId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String transactionId = "txn-" + UUID.randomUUID();
        MDC.put(TRANSACTION_ID, transactionId);

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();

        log.info("[{}] CONTROLLER layer → {} {}", transactionId, method, uri);

        try {
            chain.doFilter(request, response);
        } finally {
            log.info("[{}] CONTROLLER layer → {} {}", transactionId, method, uri);
            MDC.clear();
        }
    }
}
