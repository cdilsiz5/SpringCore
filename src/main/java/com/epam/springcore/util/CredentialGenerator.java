package com.epam.springcore.util;

import java.util.Collection;

public class CredentialGenerator {

    public static String generateUsername(String firstName, String lastName, Collection<?> existingEntities) {
        String baseUsername = firstName + "." + lastName;
        int counter = 1;

        String username = baseUsername;
        while (usernameExists(username, existingEntities)) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }

    private static boolean usernameExists(String username, Collection<?> entities) {
        return entities.stream().anyMatch(e -> {
            try {
                var method = e.getClass().getMethod("getUsername");
                String existingUsername = (String) method.invoke(e);
                return username.equalsIgnoreCase(existingUsername);
            } catch (Exception ex) {
                return false;
            }
        });
    }

    public static String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}
