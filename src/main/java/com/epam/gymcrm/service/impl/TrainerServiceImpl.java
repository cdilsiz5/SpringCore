package com.epam.gymcrm.service.impl;

import com.epam.gymcrm.dto.TrainerDto;
import com.epam.gymcrm.dto.TrainingDto;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.mapper.TrainerMapper;
import com.epam.gymcrm.mapper.TrainingMapper;
import com.epam.gymcrm.model.Trainer;
import com.epam.gymcrm.model.Training;
import com.epam.gymcrm.model.User;
import com.epam.gymcrm.model.enums.Specialization;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.repository.TrainingRepository;
import com.epam.gymcrm.request.trainer.CreateTrainerRequest;
import com.epam.gymcrm.request.trainer.UpdateTrainerRequest;
import com.epam.gymcrm.request.user.CreateUserRequest;
import com.epam.gymcrm.response.RegisterProfileResponse;
import com.epam.gymcrm.service.ITrainerService;
import com.epam.gymcrm.service.IUserService;
import com.epam.gymcrm.util.LogUtil;
import io.micrometer.core.instrument.MeterRegistry;
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
    private final TrainingRepository trainingRepository;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;
    private final IUserService userService;
    private final MeterRegistry meterRegistry;


    @Override
    public RegisterProfileResponse createTrainer(CreateTrainerRequest request) {
        String txId = LogUtil.getTransactionId();
        log.info("[{}] SERVICE Layer - Creating trainer via public endpoint", txId);

        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        User savedUser = userService.createUserEntity(createUserRequest);
        createTrainerEntity(savedUser, request.getSpecialty());
        meterRegistry.counter("gymcrm.trainer.created.count").increment();
        return new RegisterProfileResponse(savedUser.getUsername(), savedUser.getPlainPassword());
    }

    @Override
    public TrainerDto getTrainerByUsername(String username) {
        log.info("[{}] SERVICE Layer - Fetching Trainer by username: {}", MDC.get("transactionId"), username);
        Trainer trainer = findTrainerByUsername(username);
        TrainerDto dto = trainerMapper.toTrainerDto(trainer);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerDto> getAllTrainers() {
        log.info("[{}] SERVICE Layer - Fetching all trainers", MDC.get("transactionId"));
        return trainerMapper.toTrainerDtoList(trainerRepository.findAll());
    }

    @Override
    @Transactional
    public TrainerDto updateTrainer(String username, UpdateTrainerRequest request) {
        log.info("[{}] SERVICE Layer - Updating Trainer with username: {}", MDC.get("transactionId"), username);

        Trainer trainer = findTrainerByUsername(username);
        trainerMapper.updateTrainerRequest(request, trainer);
        Trainer updatedTrainer = trainerRepository.save(trainer);

        log.info("[{}] SERVICE Layer - Trainer updated. ID: {}", MDC.get("transactionId"), updatedTrainer.getId());
        return trainerMapper.toTrainerDto(updatedTrainer);
    }

    @Override
    @Transactional
    public void deleteTrainer(String username) {
        log.info("[{}] SERVICE Layer - Deleting Trainer with username: {}", MDC.get("transactionId"), username);

        Trainer trainer = findTrainerByUsername(username);
        trainerRepository.delete(trainer);

        log.info("[{}] SERVICE Layer - Trainer deleted. ID: {}", MDC.get("transactionId"), trainer.getId());
    }

    @Override
    @Transactional
    public void changeTrainerActivation(String username,boolean activate) {
        log.info("[{}] SERVICE Layer - Toggling activation for user: {}", MDC.get("transactionId"), username);
        Trainer trainer = findTrainerByUsername(username);
        userService.activateOrDeactivate(username, activate);
        log.info("[{}] SERVICE Layer - User '{}' is now {}",
                MDC.get("transactionId"), username, trainer.getUser().isUserActive() ? "ACTIVE" : "INACTIVE");
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingDto> getTrainingHistory(
            String username, LocalDate from, LocalDate to, String traineeName, String traineeLastName) {

        log.info("[{}] SERVICE Layer - Fetching training history for trainer: {}",
                MDC.get("transactionId"), username);

        List<Training> responses = trainingRepository
                .findHistoryForTrainer(username, from, to, traineeName, traineeLastName);
        
        List<TrainingDto> result = trainingMapper.toTrainingDtoList(responses);

        return result;
    }



    private TrainerDto createTrainerEntity(User user, Specialization specialty) {
        log.info("[{}] SERVICE Layer - Creating Trainer entity for user ID: {}, specialization: {}", MDC.get("transactionId"), user.getId(), specialty);
        meterRegistry.counter("gymcrm.trainer.created.count").increment();
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

    private Trainer findTrainerByUsername(String username) {
        return trainerRepository.findByUser_Username(username)
                .orElseThrow(() -> {
                    log.warn("[{}] SERVICE Layer - Trainer not found with username: {}", 
                            MDC.get("transactionId"), username);
                    return new NotFoundException("Trainer not found: " + username);
                });
    }

  
}
