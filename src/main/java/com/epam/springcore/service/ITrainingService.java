package com.epam.springcore.service;

import com.epam.springcore.model.Training;
import java.util.Collection;

/**
 * Service interface for managing training operations.
 */
public interface ITrainingService {

    /**
     * Saves a new training to the system.
     *
     * @param training the training to save
     */
    void createTraining(Training training);

    /**
     * Retrieves a training by its ID.
     *
     * @param id the ID of the training
     * @return the found Training or null if not found
     */
    Training getTraining(String id);

    /**
     * Returns all trainings in the system.
     *
     * @return a collection of all trainings
     */
    Collection<Training> getAllTrainings();
}
