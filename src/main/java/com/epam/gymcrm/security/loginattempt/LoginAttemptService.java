package com.epam.gymcrm.security.loginattempt;

/**
 * Service interface for tracking and controlling login attempts
 * in order to protect against brute-force attacks.
 * <p>
 * Implementations should count failed login attempts per username
 * and temporarily block authentication if the threshold is exceeded.
 */
public interface LoginAttemptService {

    /**
     * Checks if the given username is currently blocked
     * due to too many failed login attempts.
     *
     * @param username the username to check
     * @return {@code true} if blocked, {@code false} otherwise
     */
    boolean isBlocked(String username);

    /**
     * Marks a successful login for the given username.
     * This should reset any counters or remove any block entries.
     *
     * @param username the username that successfully authenticated
     */
    void loginSucceeded(String username);

    /**
     * Marks a failed login attempt for the given username.
     * If the maximum allowed attempts is reached, the user should
     * be blocked for a configured duration.
     *
     * @param username the username that failed authentication
     */
    void loginFailed(String username);

    /**
     * Returns the number of seconds remaining for which the given
     * username is blocked. If not blocked, returns 0.
     *
     * @param username the username to check
     * @return remaining block duration in seconds, or 0 if not blocked
     */
    long remainingBlockSeconds(String username);
}
