package com.epam.springcore.util;


import org.slf4j.MDC;

public class LogUtil {

    private static final String TRANSACTION_ID_KEY = "transactionId";

    private LogUtil() {
    }

    public static String getTransactionId() {
        Object txId = MDC.get(TRANSACTION_ID_KEY);
        return txId != null ? txId.toString() : "NO_TX_ID";
    }
}
