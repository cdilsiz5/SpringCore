package com.epam.gymcrm.service;

import com.epam.gymcrm.dto.TrainingDto;
import com.epam.gymcrm.dto.TrainingTypeDto;
import com.epam.gymcrm.request.training.CreateTrainingRequest;
import com.epam.gymcrm.request.training.UpdateTrainingRequest;
import java.util.List;

/**
 * Service interface for managing Training sessions.
 */
public interface ITrainingService {

    /**
     * Creates a new training session.
     *
     * @param request training creation request
     * @return created TrainingDto
     */
    TrainingDto createTraining(CreateTrainingRequest request);

    /**
     * Retrieves a training session by ID.
     *
     * @param id training session ID
     * @return the corresponding TrainingDto
     */
    TrainingDto getTraining(Long id);

    /**
     * Returns all training sessions.
     *
     * @return list of TrainingDto
     */
    List<TrainingDto> getAllTrainings();

    /**
     * Updates an existing training session.
     *
     * @param id      training session ID
     * @param request update request
     * @return updated TrainingDto
     */
    TrainingDto updateTraining(Long id, UpdateTrainingRequest request);

    /**
     * Deletes a training session by ID.
     *
     * @param id training session ID
     */
    void deleteTraining(Long id);


    /**
     * Retrieves all available training types in the system.
     *
     * @return list of TrainingType entities
     */
    List<TrainingTypeDto> getAllTrainingTypes();




}
