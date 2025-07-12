package com.epam.springcore.service;

import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.request.CreateTrainingRequest;

import java.util.Collection;

/**
 * Service interface for managing Training entities.
 */
public interface ITrainingService {

    /**
     * Creates a new training session based on the given request.
     *
     * @param request the request object containing training data
     * @return the created Training as DTO
     */
    TrainingDto createTraining(CreateTrainingRequest request);

    /**
     * Retrieves a training session by its ID.
     *
     * @param id the ID of the training
     * @return the found Training DTO or null if not found
     */
    TrainingDto getTraining(String id);

    /**
     * Returns all trainings in the system.
     *
     * @return a collection of Training DTOs
     */
    Collection<TrainingDto> getAllTrainings();

    /**
     * Updates an existing training session by ID.
     *
     * @param id      the ID of the training to update
     * @param request the updated data
     * @return the found Training DTO or null if not found
     */
    TrainingDto updateTraining(String id, CreateTrainingRequest request);

    /**
     * Deletes a training session by its ID.
     *
     * @param id the ID of the training to delete
     */
    void deleteTraining(String id);
}
