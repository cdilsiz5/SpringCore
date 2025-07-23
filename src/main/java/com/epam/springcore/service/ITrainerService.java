package com.epam.springcore.service;

import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.User;
import com.epam.springcore.model.enums.Specialization;
import com.epam.springcore.request.trainer.CreateTrainerRequest;
import com.epam.springcore.request.trainer.UpdateTrainerRequest;

import java.time.LocalDate;
import java.util.List;

public interface ITrainerService {

    /**
     * Creates a new trainer profile (public endpoint).
     *
     * @param request trainer creation request
     * @return created TrainerDto
     */
    TrainerDto createTrainer(CreateTrainerRequest request);

    /**
     * Retrieves a trainer by username.
     *
     * @param authUsername requester username (from header)
     * @param authPassword requester password (from header)
     * @param username trainer username
     * @return trainer dto
     */
    TrainerDto getTrainerByUsername(String authUsername, String authPassword, String username);

    /**
     * Returns all trainers (requires authentication).
     *
     * @param authUsername requester username
     * @param authPassword requester password
     * @return list of all trainers
     */
    List<TrainerDto> getAllTrainers(String authUsername, String authPassword);

    /**
     * Updates trainer by username (requires authentication).
     *
     * @param authUsername requester username
     * @param authPassword requester password
     * @param username trainer username to update
     * @param request update payload
     * @return updated trainer dto
     */
    TrainerDto updateTrainer(String authUsername, String authPassword, String username, UpdateTrainerRequest request);

    /**
     * Deletes a trainer by username (requires authentication).
     *
     * @param authUsername requester username
     * @param authPassword requester password
     * @param username trainer username to delete
     */
    void deleteTrainer(String authUsername, String authPassword, String username);

    /**
     * Toggles trainer's active status (requires authentication).
     *
     * @param authUsername requester username
     * @param authPassword requester password
     * @param username trainer username to toggle
     */
    void toggleActivation(String authUsername, String authPassword, String username);

    /**
     * Returns a trainer's training history with optional filters (requires authentication).
     *
     * @param authUsername requester username
     * @param authPassword requester password
     * @param username trainer username whose trainings to fetch
     * @param from start date (optional)
     * @param to end date (optional)
     * @param traineeName filter by trainee first name (optional)
     * @param traineeLastName filter by trainee last name (optional)
     * @return list of matching trainings
     */
    List<TrainingDto> getTrainingHistory(
            String authUsername,
            String authPassword,
            String username,
            LocalDate from,
            LocalDate to,
            String traineeName,
            String traineeLastName
    );

    /**
     * Creates a trainer entity from a given user and specialization (used internally).
     *
     * @param user user entity
     * @param specialty specialization
     * @return created trainer dto
     */
    TrainerDto createTrainerEntity(User user, Specialization specialty);

    /**
     * Returns the Trainer entity by ID (used internally).
     *
     * @param trainerId trainer id
     * @return trainer entity
     */
    Trainer getTrainerById(Long trainerId);
}
