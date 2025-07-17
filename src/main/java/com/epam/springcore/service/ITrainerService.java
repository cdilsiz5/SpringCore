package com.epam.springcore.service;

import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.request.trainer.CreateTrainerRequest;
import com.epam.springcore.request.trainer.UpdateTrainerRequest;

import java.util.List;

/**
 * Service interface for managing Trainer entities and their operations.
 */
public interface ITrainerService {

    /**
     * Creates a new trainer profile.
     *
     * @param request trainer creation request
     * @return the created TrainerDto
     */
    TrainerDto createTrainer(CreateTrainerRequest request);

    /**
     * Retrieves a trainer by their username.
     *
     * @param username trainer's username
     * @return the corresponding TrainerDto
     */
    TrainerDto getTrainerByUsername(String username);

    /**
     * Returns all registered trainers.
     *
     * @return list of TrainerDto
     */
    List<TrainerDto> getAllTrainers();

    /**
     * Updates trainer information.
     *
     * @param username trainer's username
     * @param request  update request
     * @return updated TrainerDto
     */
    TrainerDto updateTrainer(String username, UpdateTrainerRequest request);

    /**
     * Deletes a trainer by their username.
     *
     * @param username the trainer's username
     */
    void deleteTrainer(String username);

    /**
     * Toggles the active/passive status of a trainer.
     *
     * @param username trainer's username
     * @return updated TrainerDto
     */
    TrainerDto activateOrDeactivate(String username);

    /**
     * Returns a filtered list of trainings provided by this trainer.
     *
     * @param username     trainer’s username
     * @param from         start date (optional)
     * @param to           end date (optional)
     * @param traineeName  trainee name filter (optional)
     * @return list of matching trainings TrainerDto's.
     */
    List<TrainerDto> getTrainingHistory(String username, String from, String to, String traineeName);
}
