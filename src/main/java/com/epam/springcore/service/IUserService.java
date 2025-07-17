package com.epam.springcore.service;

import com.epam.springcore.dto.UserDto;
import com.epam.springcore.request.user.CreateUserRequest;
import com.epam.springcore.request.user.UpdatePasswordRequest;
import com.epam.springcore.request.user.LoginRequest;

import java.util.List;

/**
 * Service interface for managing User entities and authentication logic.
 */
public interface IUserService {

    /**
     * Creates a new user.
     *
     * @param request user creation request
     * @return created UserDto
     */
    UserDto createUser(CreateUserRequest request);

    /**
     * Retrieves a user by username.
     *
     * @param username username of the user
     * @return UserDto if found
     */
    UserDto getUserByUsername(String username);

    /**
     * Returns all registered users.
     *
     * @return list of UserDto
     */
    List<UserDto> getAllUsers();

    /**
     * Deletes a user by their username.
     *
     * @param username username of the user
     */
    void deleteUser(String username);

    /**
     * Updates the user password.
     *
     * @param username user's username
     * @param request  request containing the new password
     * @return updated UserDto
     */
    UserDto updatePassword(String username, UpdatePasswordRequest request);

    /**
     * Authenticates a user with provided credentials.
     *
     * @param request login credentials
     * @return true if credentials are valid, false otherwise
     */
    boolean login(LoginRequest request);
}
