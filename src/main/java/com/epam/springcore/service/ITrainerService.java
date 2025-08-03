package com.epam.springcore.service;

import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.request.trainer.CreateTrainerRequest;
import com.epam.springcore.request.trainer.UpdateTrainerRequest;
import com.epam.springcore.response.LoginCredentialsResponse;

import java.time.LocalDate;
import java.util.List;

public interface ITrainerService {

    /**
     * Creates a new trainer profile (public endpoint).
     *
     * @param request trainer creation request
     * @return created TrainerDto
     */
    LoginCredentialsResponse createTrainer(CreateTrainerRequest request);

    /**
     * Retrieves a trainer by username.
     *
     * @param username trainer username
     * @return trainer dto
     */
    TrainerDto getTrainerByUsername(String username);

    /**
     * Returns all trainers (requires authentication).
     *
     * @return list of all trainers
     */
    List<TrainerDto> getAllTrainers();

    /**
     * Updates trainer by username (requires authentication).
     *
     * @param username trainer username to update
     * @param request update payload
     * @return updated trainer dto
     */
    TrainerDto updateTrainer( String username, UpdateTrainerRequest request);

    /**
     * Deletes a trainer by username (requires authentication).
     *
     * @param username trainer username to delete
     */
    void deleteTrainer( String username);

    /**
     * Toggles trainer's active status (requires authentication).
     *
     * @param username trainer username to toggle
     */
    void toggleActivation( String username);

    /**
     * Returns a trainer's training history with optional filters (requires authentication).
     *
     * @param username trainer username whose trainings to fetch
     * @param from start date (optional)
     * @param to end date (optional)
     * @param traineeName filter by trainee first name (optional)
     * @param traineeLastName filter by trainee last name (optional)
     * @return list of matching trainings
     */
    List<TrainingDto> getTrainingHistory(
            String username,
            LocalDate from,
            LocalDate to,
            String traineeName,
            String traineeLastName
    );


    /**
     * Returns the Trainer entity by ID (used internally).
     *
     * @param trainerId trainer id
     * @return trainer entity
     */
    Trainer getTrainerById(Long trainerId);


}
