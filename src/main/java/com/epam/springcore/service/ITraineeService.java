package com.epam.springcore.service;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import com.epam.springcore.request.trainer.TrainerUsernameRequest;
import com.epam.springcore.response.LoginCredentialsResponse;

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
    LoginCredentialsResponse createTrainee(CreateTraineeRequest request);

    /**
     * Retrieves a specific trainee by their username.
     *

     * @param username      the username of the trainee to retrieve
     * @return the trainee DTO
     */
    TraineeDto getTraineeByUsername( String username);

    /**
     * Retrieves all trainees in the system.
     *
     * @return a list of all trainee DTOs
     */
    List<TraineeDto> getAllTrainees();

    /**
     * Updates the information of an existing trainee.
     *
     * @param username      the username of the trainee to update
     * @param request       the update request payload
     * @return the updated trainee DTO
     */
    TraineeDto updateTrainee( String username, UpdateTraineeRequest request);

    /**
     * Deletes a trainee by their username.
     *
     * @param username      the username of the trainee to delete
     */
    void deleteTrainee( String username);

    /**
     * Toggles activation status of a trainee's user account.
     *
     * @param username      the username of the trainee to toggle activation for
     */
    void toggleActivation( String username);

    /**
     * Retrieves the training history for a trainee with optional filters.
     *
     * @param username         the trainee's username
     * @param from             optional start date
     * @param to               optional end date
     * @param trainerName      optional trainer first name filter
     * @param trainerLastName  optional trainer last name filter
     * @return list of training DTOs matching the filter
     */
    List<TrainingDto> getTrainingHistory( String username,
                                         LocalDate from, LocalDate to,
                                         String trainerName, String trainerLastName);

    /**
     * Fetches the trainee entity by its ID.
     *
     * @param traineeId the ID of the trainee
     * @return the Trainee entity
     */
    Trainee getTraineeById(Long traineeId);

    /**
     * Retrieves a list of trainers that are not yet assigned to a given trainee.
     * @param authUsername authenticated username of the trainee
     * @return list of unassigned trainers
     */
    List<TrainerDto> getUnassignedTrainers(String authUsername);

    /**
     * Updates the list of trainers assigned to a given trainee.
     *
     * @param username the trainee's username
     * @param requestList list of trainer usernames to be assigned
     * @return list of assigned trainers as DTOs
     */
    List<TrainerDto> updateTrainerList(String username, List<TrainerUsernameRequest> requestList);

}
