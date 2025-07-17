package com.epam.springcore.session;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionRegistry {

    private final ConcurrentHashMap<String, Boolean> activeUserMap = new ConcurrentHashMap<>();

    public void setActive(String username, boolean isActive) {
        activeUserMap.put(username, isActive);
    }

    public boolean isActive(String username) {
        return activeUserMap.getOrDefault(username, false);
    }

    public void removeUser(String username) {
        activeUserMap.remove(username);
    }
}
