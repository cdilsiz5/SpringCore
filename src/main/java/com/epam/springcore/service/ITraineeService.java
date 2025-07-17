package com.epam.springcore.service;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeTrainerListRequest;

import java.util.List;

/**
 * Service interface for managing Trainee entities and related operations.
 */
public interface ITraineeService {

    /**
     * Creates a new trainee with the provided data.
     *
     * @param request the request containing trainee information
     * @return the created trainee as a DTO
     */
    TraineeDto createTrainee(CreateTraineeRequest request);

    /**
     * Retrieves a trainee by their username.
     *
     * @param username the trainee's username
     * @return the corresponding TraineeDto
     */
    TraineeDto getTraineeByUsername(String username);

    /**
     * Returns a list of all trainees in the system.
     *
     * @return a list of TraineeDto objects
     */
    List<TraineeDto> getAllTrainees();

    /**
     * Updates an existing trainee’s information.
     *
     * @param username the trainee’s username to update
     * @param request  the update request with new data
     * @return the updated TraineeDto
     */
    TraineeDto updateTrainee(String username, UpdateTraineeRequest request);

    /**
     * Deletes a trainee by their username.
     *
     * @param username the username of the trainee to delete
     */
    void deleteTrainee(String username);

    /**
     * Toggles active/passive status of a trainee.
     *
     * @param username the username of the trainee
     * @return the updated TraineeDto with new active status
     */
    TraineeDto activateOrDeactivate(String username);

    /**
     * Updates the list of trainers assigned to a trainee.
     *
     * @param username the username of the trainee
     * @param request  the request containing new trainer list
     * @return the updated TraineeDto
     */
    TraineeDto updateTrainerList(String username, UpdateTraineeTrainerListRequest request);

    /**
     * Retrieves training history of a trainee with optional filters.
     *
     * @param username     the trainee username
     * @param from         start date (optional)
     * @param to           end date (optional)
     * @param trainerName  trainer name filter (optional)
     * @param trainingType training type filter (optional)
     * @return list of matching training TraineeDto sessions
     */
    List<TraineeDto> getTrainingHistory(String username, String from, String to, String trainerName, String trainingType);

    /**
     * Returns trainers not currently assigned to the trainee.
     *
     * @param username the trainee's username
     * @return list of unassigned trainer TraineeDto's.
     */
    List<TraineeDto> getUnassignedTrainers(String username);
}
