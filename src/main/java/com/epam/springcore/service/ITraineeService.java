package com.epam.springcore.service;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.request.create.CreateTraineeRequest;

import java.util.List;

/**
 * Service interface for managing Trainee entities.
 */
public interface ITraineeService {

    /**
     * Creates a new trainee based on the given request.
     *
     * @param request the request object containing trainee data
     * @return the created Trainee as DTO
     */
    TraineeDto createTrainee(CreateTraineeRequest request);

    /**
     * Retrieves a trainee by their ID.
     *
     * @param id the ID of the trainee
     * @return the found Trainee DTO or null if not found
     */
    TraineeDto getTrainee(String id);

    /**
     * Returns all trainees in the system.
     *
     * @return a collection of Trainee DTOs
     */
    List<TraineeDto> getAllTrainees();

    /**
     * Updates an existing trainee by ID.
     *
     * @param id the ID of the trainee to update
     * @param request the updated data
     * @return the created Trainee as DTO
     */
    TraineeDto updateTrainee(String id, CreateTraineeRequest request);

    /**
     * Deletes a trainee by their ID.
     *
     * @param id the ID of the trainee to delete
     */
    void deleteTrainee(String id);
}
