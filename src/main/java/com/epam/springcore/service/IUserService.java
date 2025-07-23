package com.epam.springcore.service;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.UserDto;
import com.epam.springcore.model.User;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainer.CreateTrainerRequest;
import com.epam.springcore.request.user.UpdatePasswordRequest;
import com.epam.springcore.request.user.LoginRequest;

import java.util.List;

/**
 * Service interface for managing User entities and authentication logic.
 */
public interface IUserService {



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

    /**
     * Creates a new trainee along with associated user account.
     *
     * @param request trainee creation request
     * @return created TraineeDto
     */
    TraineeDto createTrainee(CreateTraineeRequest request);

    /**
     * Creates a new trainer along with associated user account.
     *
     * @param request trainer creation request
     * @return created TrainerDto
     */
    TrainerDto createTrainer(CreateTrainerRequest request);


    User getUserEntityByUsername(String username);

    /**
     * Toggles active/passive status of a trainee.
     *
     * @param username the username of the trainee
     */
     void activateOrDeactivate(String username);

    /**
     * Logs out the user by removing them from the session registry.
     *
     * @param username the username to log out
     */
     void logout(String username) ;


    }
