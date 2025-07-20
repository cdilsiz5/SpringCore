package com.epam.springcore.service;

import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.request.training.CreateTrainingRequest;
import com.epam.springcore.request.training.UpdateTrainingRequest;

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
     * Retrieves a training List by Trainer
     *
     * @param trainer training session ID
     * @return the corresponding TrainingDto List
     */
    List<TrainingDto> findAllByTrainer(Trainer trainer);

    /**
     * Retrieves a training List byTrainee
     *
     * @param trainee training session ID
     * @return the corresponding TrainingDto List
     */
    List<TrainingDto> findAllByTrainee(Trainee trainee);


}
