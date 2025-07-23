package com.epam.springcore.service;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing Trainee operations.
 */
public interface ITraineeService {

    /**
     * Creates a new trainee along with the corresponding user.
     *
     * @param request the trainee creation request
     * @return the created trainee DTO
     */
    TraineeDto createTrainee(CreateTraineeRequest request);

    /**
     * Retrieves a specific trainee by their username.
     *
     * @param authUsername authenticated username
     * @param authPassword authenticated password
     * @param username      the username of the trainee to retrieve
     * @return the trainee DTO
     */
    TraineeDto getTraineeByUsername(String authUsername, String authPassword, String username);

    /**
     * Retrieves all trainees in the system.
     *
     * @param authUsername authenticated username
     * @param authPassword authenticated password
     * @return a list of all trainee DTOs
     */
    List<TraineeDto> getAllTrainees(String authUsername, String authPassword);

    /**
     * Updates the information of an existing trainee.
     *
     * @param authUsername authenticated username
     * @param authPassword authenticated password
     * @param username      the username of the trainee to update
     * @param request       the update request payload
     * @return the updated trainee DTO
     */
    TraineeDto updateTrainee(String authUsername, String authPassword, String username, UpdateTraineeRequest request);

    /**
     * Deletes a trainee by their username.
     *
     * @param authUsername authenticated username
     * @param authPassword authenticated password
     * @param username      the username of the trainee to delete
     */
    void deleteTrainee(String authUsername, String authPassword, String username);

    /**
     * Toggles activation status of a trainee's user account.
     *
     * @param authUsername authenticated username
     * @param authPassword authenticated password
     * @param username      the username of the trainee to toggle activation for
     */
    void toggleActivation(String authUsername, String authPassword, String username);

    /**
     * Retrieves the training history for a trainee with optional filters.
     *
     * @param authUsername     authenticated username
     * @param authPassword     authenticated password
     * @param username         the trainee's username
     * @param from             optional start date
     * @param to               optional end date
     * @param trainerName      optional trainer first name filter
     * @param trainerLastName  optional trainer last name filter
     * @return list of training DTOs matching the filter
     */
    List<TrainingDto> getTrainingHistory(String authUsername, String authPassword, String username,
                                         LocalDate from, LocalDate to,
                                         String trainerName, String trainerLastName);


    /**
     * Fetches the trainee entity by its ID.
     *
     * @param traineeId the ID of the trainee
     * @return the Trainee entity
     */
    Trainee getTraineeById(Long traineeId);
}
