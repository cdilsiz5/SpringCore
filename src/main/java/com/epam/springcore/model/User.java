package com.epam.springcore.model;

import com.epam.springcore.util.CredentialGenerator;

import java.util.Collection;

public class User {

    private static int counter = 0; // static ID counter

    protected String id;
    protected String firstName;
    protected String lastName;
    protected String username;
    protected String password;
    protected boolean isActive;

    public User(String firstName, String lastName, Collection<?> entities) {
        this.id = String.valueOf(++counter);
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = CredentialGenerator.generateUsername(firstName, lastName, entities);
        this.password = CredentialGenerator.generateRandomPassword();
        this.isActive = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
