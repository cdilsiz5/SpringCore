package com.epam.springcore.logging;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

//@Component
public class MDCTransactionFilter implements Filter {

    private static final String TRANSACTION_ID = "transactionId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            String transactionId = UUID.randomUUID().toString();
            MDC.put(TRANSACTION_ID, transactionId);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRANSACTION_ID);
        }
    }
}
