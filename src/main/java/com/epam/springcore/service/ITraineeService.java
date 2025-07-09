package com.epam.springcore.service;

import com.epam.springcore.model.Trainee;
import java.util.Collection;

/**
 * Service interface for managing Trainee entities.
 */
public interface ITraineeService {

    /**
     * Saves a new trainee to the system.
     *
     * @param trainee the trainee to be saved
     */
    void createTrainee(Trainee trainee);

    /**
     * Retrieves a trainee by their ID.
     *
     * @param id the ID of the trainee
     * @return the found Trainee or null if not found
     */
    Trainee getTrainee(String id);

    /**
     * Returns all trainees in the system.
     *
     * @return a collection of all trainees
     */
    Collection<Trainee> getAllTrainees();
}
