package com.epam.gymcrm.security.loginattempt.impl;

import com.epam.gymcrm.security.loginattempt.LoginAttemptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@Service
public class InMemoryLoginAttemptService implements LoginAttemptService {

    private final int maxAttempts;
    private final long blockMillis;

    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> blockedUntil = new ConcurrentHashMap<>();

    public InMemoryLoginAttemptService(
            @Value("${security.bruteforce.max-attempts:3}") int maxAttempts,
            @Value("${security.bruteforce.block-seconds:300}") long blockSeconds
    ) {
        this.maxAttempts = maxAttempts;
        this.blockMillis = TimeUnit.SECONDS.toMillis(blockSeconds);
    }

    @Override
    public boolean isBlocked(String username) {
        String key = norm(username);
        Long until = blockedUntil.get(key);
        if (until == null) return false;

        if (System.currentTimeMillis() > until) {
            blockedUntil.remove(key);
            counters.remove(key);
            log.info("User '{}' is no longer blocked (block expired)", key);
            return false;
        }
        log.warn("User '{}' is currently blocked for {} more seconds", key, remainingBlockSeconds(username));
        return true;
    }

    @Override
    public void loginSucceeded(String username) {
        String key = norm(username);
        counters.remove(key);
        blockedUntil.remove(key);
        log.info("User '{}' login succeeded -> counters reset, block removed", key);
    }

    @Override
    public void loginFailed(String username) {
        String key = norm(username);
        int attempts = counters.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();

        if (attempts >= maxAttempts) {
            long until = System.currentTimeMillis() + blockMillis;
            blockedUntil.put(key, until);
            log.warn("User '{}' blocked for {} seconds due to {} failed attempts",
                    key, TimeUnit.MILLISECONDS.toSeconds(blockMillis), attempts);
        } else {
            log.info("Failed login for '{}', attempt {}/{}", key, attempts, maxAttempts);
        }
    }

    @Override
    public long remainingBlockSeconds(String username) {
        String key = norm(username);
        Long until = blockedUntil.get(key);
        if (until == null) return 0L;
        long diff = until - System.currentTimeMillis();
        return diff > 0 ? TimeUnit.MILLISECONDS.toSeconds(diff) : 0L;
    }

    private String norm(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }
}
