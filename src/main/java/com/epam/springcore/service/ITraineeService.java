package com.epam.springcore.service;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.model.User;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeTrainerListRequest;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing Trainee entities and related operations.
 */
public interface ITraineeService {

    /**
     * Creates a new trainee with the provided data.
     *
     * @param  user,dob,address the request containing trainee information
     * @return the created trainee as a DTO
     */
    TraineeDto createTraineeEntity(User user, LocalDate dob, String address);

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
    List<TrainingDto> getTrainingHistory(String username, String from, String to, String trainerName, String trainerLastname, String trainingType);
    /**
     * Returns trainers not currently assigned to the trainee.
     *
     * @param username the trainee's username
     * @return list of unassigned trainer TraineeDto's.
     */
    List<TrainerDto> getUnassignedTrainers(String username);
}
