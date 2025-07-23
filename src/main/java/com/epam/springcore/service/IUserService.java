package com.epam.springcore.service;

import com.epam.springcore.dto.UserDto;
import com.epam.springcore.model.User;
import com.epam.springcore.request.user.CreateUserRequest;
import com.epam.springcore.request.user.UpdatePasswordRequest;

import java.util.List;

/**
 * Service interface for managing User entities and handling authentication logic.
 */
public interface IUserService {

    /**
     * Verifies the credentials of a user. Throws an exception if authentication fails.
     *
     * @param username the username provided in the request header
     * @param password the password provided in the request header
     * @throws com.epam.springcore.exception.UnauthorizedException if the credentials are invalid or user is not active
     */
    boolean authenticate(String username, String password);

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
     * Updates the password for the currently authenticated user.
     *
     * @param request the request body containing old and new password
     * @return UserDto with updated password information (masked in DTO)
     */
    UserDto updatePassword(String username, UpdatePasswordRequest request);

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
