package com.epam.springcore.service;

import com.epam.springcore.dto.UserDto;
import com.epam.springcore.model.User;
import com.epam.springcore.request.user.ChangePasswordRequest;
import com.epam.springcore.request.user.CreateUserRequest;
import com.epam.springcore.request.user.LoginRequest;

import java.util.List;

/**
 * Service interface for managing User entities and handling authentication logic.
 */
public interface IUserService {


    /**
     * Logs in the user by validating credentials and activating the session.
     *
     * @param request the login request containing username and password
     * @return true if login is successful
     */
    boolean login(LoginRequest request);

    /**
     * Logs out the user by setting their isActive flag to false.
     *
     * @param username the username of the user to be logged out
     */
    void logout(String username);

    /**
     * Checks if the user is authenticated (i.e., isActive == true).
     *
     * @param username the username to check
     * @return true if user is authenticated
     */
    boolean isAuthenticated(String username);

    /**
     * Changes the password of a user after validating the old password.
     *
     * @param request the change password request
     */
    void changePassword(ChangePasswordRequest request);


    /**
     * Retrieves a user by their username.
     * @param targetUsername the username of the user to retrieve
     * @return UserDto representing the requested user
     */
    UserDto getUserByUsername(String targetUsername);

    /**
     * Retrieves all users in the system.
     *
     * @return list of UserDto representing all users
     */
    List<UserDto> getAllUsers();

    /**
     * Deletes a user by their username.
     *
     * @param targetUsername the username of the user to be deleted
     */
    void deleteUser(String targetUsername);


    /**
     * Toggles the active/passive status of the specified user.
     * @param targetUsername the username of the user whose activation status will be toggled
     */
    void activateOrDeactivate(String targetUsername);

    /**
     * Logs out the currently authenticated user by removing them from the session registry.
     *
     * @param authUsername the authenticated user's username
     * @param authPassword the authenticated user's password
     */
    /**
     * Retrieves the full User entity by username.
     * Primarily used internally for accessing the user domain model.
     *
     * @param username the username to look up
     * @return User entity
     */
    User getUserEntityByUsername(String username);

    /**
     * Creates and persists a new User entity from a given request.
     * This is an internal helper used during trainer/trainee creation.
     *
     * @param request user creation request including names
     * @return the persisted User entity
     */
    User createUserEntity(CreateUserRequest request);
}
