package com.epam.springcore.service;

import com.epam.springcore.model.Trainer;
import java.util.Collection;

/**
 * Service interface for managing Trainer entities.
 */
public interface ITrainerService {

    /**
     * Saves a new trainer to the system.
     *
     * @param trainer the trainer to be saved
     */
    void createTrainer(Trainer trainer);

    /**
     * Retrieves a trainer by their ID.
     *
     * @param id the ID of the trainer
     * @return the found Trainer or null if not found
     */
    Trainer getTrainer(String id);

    /**
     * Returns all trainers in the system.
     *
     * @return a collection of all trainers
     */
    Collection<Trainer> getAllTrainers();
}
