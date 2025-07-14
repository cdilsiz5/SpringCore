package com.epam.springcore.service;

import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.request.create.CreateTrainerRequest;

import java.util.List;

/**
 * Service interface for managing Trainer entities.
 */
public interface ITrainerService {

    /**
     * Creates a new trainer based on the given request.
     *
     * @param request the request object containing trainer data
     * @return the created Trainer as DTO
     */
    TrainerDto createTrainer(CreateTrainerRequest request);

    /**
     * Retrieves a trainer by their ID.
     *
     * @param id the ID of the trainer
     * @return the found Trainer DTO or null if not found
     */
    TrainerDto getTrainer(String id);

    /**
     * Returns all trainers in the system.
     *
     * @return a collection of Trainer DTOs
     */
    List<TrainerDto> getAllTrainers();

    /**
     * Updates an existing trainer by ID.
     *
     * @param id the ID of the trainer to update
     * @param request the updated data
     * @return the found Trainer DTO or null if not found
     */
    TrainerDto updateTrainer(String id, CreateTrainerRequest request);

    /**
     * Deletes a trainer by their ID.
     *
     * @param id the ID of the trainer to delete
     */
    void deleteTrainer(String id);
}
