package com.epam.gymcrm.service.impl;

import com.epam.gymcrm.dto.TrainerDto;
import com.epam.gymcrm.dto.TrainingDto;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.exception.UnauthorizedException;
import com.epam.gymcrm.mapper.TrainerMapper;
import com.epam.gymcrm.model.Trainer;
import com.epam.gymcrm.model.User;
import com.epam.gymcrm.model.enums.Specialization;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.request.trainer.CreateTrainerRequest;
import com.epam.gymcrm.request.trainer.UpdateTrainerRequest;
import com.epam.gymcrm.request.user.CreateUserRequest;
import com.epam.gymcrm.response.LoginCredentialsResponse;
import com.epam.gymcrm.response.TrainingResponse;
import com.epam.gymcrm.service.ITrainerService;
import com.epam.gymcrm.service.ITrainingService;
import com.epam.gymcrm.service.IUserService;
import com.epam.gymcrm.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements ITrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainerMapper trainerMapper;
    private final ITrainingService trainingService;
    private final IUserService userService;

 @Override
    public LoginCredentialsResponse createTrainer(CreateTrainerRequest request) {
        String txId = LogUtil.getTransactionId();
        log.info("[{}] SERVICE Layer - Creating trainer via public endpoint", txId);

        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        User savedUser = userService.createUserEntity(createUserRequest);
        createTrainerEntity(savedUser, request.getSpecialty());
        return new LoginCredentialsResponse(savedUser.getUsername(), savedUser.getPassword());
    }

    @Override
    public TrainerDto getTrainerByUsername(String username) {
        validate(username);
        log.info("[{}] SERVICE Layer - Fetching Trainer by username: {}", MDC.get("transactionId"), username);
        TrainerDto dto = trainerMapper.toTrainerDto(getTrainerEntityByUsername(username));
        userService.logout(username);
        return dto;
    }

    @Override
    public List<TrainerDto> getAllTrainers() {
        log.info("[{}] SERVICE Layer - Fetching all trainers", MDC.get("transactionId"));
        return trainerMapper.toTrainerDtoList(trainerRepository.findAll());
    }

    @Override
    public TrainerDto updateTrainer(String username, UpdateTrainerRequest request) {
        validate(username);
        log.info("[{}] SERVICE Layer - Updating Trainer with username: {}", MDC.get("transactionId"), username);

        Trainer trainer = getTrainerEntityByUsername(username);
        trainerMapper.updateTrainerRequest(request, trainer);
        Trainer updatedTrainer = trainerRepository.save(trainer);

        log.info("[{}] SERVICE Layer - Trainer updated. ID: {}", MDC.get("transactionId"), updatedTrainer.getId());
        userService.logout(username);
        return trainerMapper.toTrainerDto(updatedTrainer);
    }

    @Override
    public void deleteTrainer(String username) {
        validate(username);
        log.info("[{}] SERVICE Layer - Deleting Trainer with username: {}", MDC.get("transactionId"), username);

        Trainer trainer = getTrainerEntityByUsername(username);
        trainerRepository.delete(trainer);

        log.info("[{}] SERVICE Layer - Trainer deleted. ID: {}", MDC.get("transactionId"), trainer.getId());
        userService.logout(username);
    }

    @Override
    public void toggleActivation(String username) {
        validate(username);
        log.info("[{}] SERVICE Layer - Toggling activation for user: {}", MDC.get("transactionId"), username);
        userService.activateOrDeactivate(username);
        userService.logout(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingResponse> getTrainingHistory(
            String username, LocalDate from, LocalDate to, String traineeName, String traineeLastName) {

        validate(username);
        log.info("[{}] SERVICE Layer - Fetching training history for trainer: {}",
                MDC.get("transactionId"), username);

        List<TrainingResponse> result = trainingService
                .findHistoryForTrainer(username, from, to, traineeName, traineeLastName);

        userService.logout(username);
        return result;
    }



    private TrainerDto createTrainerEntity(User user, Specialization specialty) {
        log.info("[{}] SERVICE Layer - Creating Trainer entity for user ID: {}, specialization: {}", MDC.get("transactionId"), user.getId(), specialty);
        Trainer trainer = Trainer.builder()
                .specialization(specialty)
                .user(user)
                .build();
        Trainer savedTrainer = trainerRepository.save(trainer);
        log.info("[{}] SERVICE Layer - Trainer saved with ID: {}", MDC.get("transactionId"), savedTrainer.getId());
        return trainerMapper.toTrainerDto(savedTrainer);
    }

    @Override
    public Trainer getTrainerById(Long trainerId) {
        log.info("[{}] SERVICE Layer - Fetching trainer by ID: {}", MDC.get("transactionId"), trainerId);
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> {
                    log.warn("[{}] SERVICE Layer - Trainer not found with ID: {}", MDC.get("transactionId"), trainerId);
                    return new NotFoundException("Trainer with id " + trainerId + " not found");
                });
    }

    private Trainer getTrainerEntityByUsername(String username) {
        return trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    log.warn("[{}] SERVICE Layer - Trainer not found with username: {}", MDC.get("transactionId"), username);
                    return new NotFoundException("Trainer not found: " + username);
                });
    }

    private void validate(String username) {
        if (!userService.isAuthenticated(username)) {
            log.warn("[{}] SERVICE Layer - Unauthorized access attempt for: {}", MDC.get("transactionId"), username);
            throw new UnauthorizedException("User not authenticated: " + username);
        }
    }
}
