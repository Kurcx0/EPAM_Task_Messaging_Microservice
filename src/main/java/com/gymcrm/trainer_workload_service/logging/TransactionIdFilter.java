package com.gymcrm.trainer_workload_service.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionIdFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TransactionIdFilter.class);
    private static final String TRANSACTION_ID_HEADER = "X-Transaction-ID";
    private static final String TRANSACTION_ID_KEY = "transactionId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String transactionId = request.getHeader(TRANSACTION_ID_HEADER);

        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
            logger.debug("Generated new transaction ID: {}", transactionId);
        } else {
            logger.debug("Using existing transaction ID from request header: {}", transactionId);
        }

        MDC.put(TRANSACTION_ID_KEY, transactionId);
        response.setHeader(TRANSACTION_ID_HEADER, transactionId);
        request.setAttribute(TRANSACTION_ID_KEY, transactionId);

        try {
            logger.info("Transaction [{}] - {} request to {}", transactionId, request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
            logger.info("Transaction [{}] - Response status: {}", transactionId, response.getStatus());
        } finally {
            MDC.remove(TRANSACTION_ID_KEY);
        }
    }
}