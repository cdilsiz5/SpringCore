package com.epam.gymcrm.service.impl;

import com.epam.gymcrm.dto.TraineeDto;
import com.epam.gymcrm.dto.TrainerDto;
import com.epam.gymcrm.dto.TrainingDto;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.mapper.TraineeMapper;
import com.epam.gymcrm.mapper.TrainerMapper;
import com.epam.gymcrm.mapper.TrainingMapper;
import com.epam.gymcrm.model.*;
import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.repository.TrainingRepository;
import com.epam.gymcrm.repository.TrainingTypeRepository;
import com.epam.gymcrm.request.trainee.CreateTraineeRequest;
import com.epam.gymcrm.request.trainee.UpdateTraineeRequest;
import com.epam.gymcrm.request.trainer.TrainerUsernameRequest;
import com.epam.gymcrm.request.user.CreateUserRequest;
import com.epam.gymcrm.response.RegisterProfileResponse;
import com.epam.gymcrm.service.ITraineeService;
import com.epam.gymcrm.service.IUserService;
import com.epam.gymcrm.util.LogUtil;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements ITraineeService {

    private final TraineeRepository traineeRepository;
    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingMapper trainingMapper;
    private final TrainerMapper trainerMapper;
    private final TraineeMapper traineeMapper;
    private final IUserService userService;
    private final TrainingTypeRepository trainingTypeRepository;
    private final MeterRegistry meterRegistry;

    @Override
    @Transactional
    public RegisterProfileResponse createTrainee(CreateTraineeRequest request) {
        String txId = LogUtil.getTransactionId();
        if (txId == null) txId = "N/A";
        log.info("[{}] SERVICE Layer - Creating trainee via public endpoint", txId);
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        User savedUser = userService.createUserEntity(createUserRequest);
        createTraineeEntity(savedUser, request);

        meterRegistry.counter("gymcrm.trainee.created.count").increment();
        return new RegisterProfileResponse(savedUser.getUsername(), savedUser.getPassword());
    }

    @Override
    public TraineeDto getTraineeByUsername(String username) {
        log.info("[{}] SERVICE Layer - Fetching Trainee by username: {}", MDC.get("transactionId"), username);
        TraineeDto dto = traineeMapper.toTraineeDto(getTraineeEntityByUsername(username));
        userService.logout(username);
        return dto;
    }

    @Override
    @Transactional()
    public List<TraineeDto> getAllTrainees() {
        log.info("[{}] SERVICE Layer - Fetching all trainees", MDC.get("transactionId"));
        return traineeMapper.toTraineeDtoList(traineeRepository.findAll());
    }

    @Override
    public TraineeDto updateTrainee(String username, UpdateTraineeRequest request) {
        log.info("[{}] SERVICE Layer - Updating Trainee: {}", MDC.get("transactionId"), username);
        Trainee trainee = getTraineeEntityByUsername(username);
        traineeMapper.updateTraineeRequest(request, trainee);
        Trainee updatedTrainee = traineeRepository.save(trainee);
        userService.logout(username);
        return traineeMapper.toTraineeDto(updatedTrainee);
    }

    @Override
    public void deleteTrainee(String username) {
        log.info("[{}] SERVICE Layer - Deleting Trainee: {}", MDC.get("transactionId"), username);
        Trainee trainee = getTraineeEntityByUsername(username);
        traineeRepository.delete(trainee);
        userService.logout(username);
    }

    @Override
    public void toggleActivation(String username) {
        log.info("[{}] SERVICE Layer - Toggling activation for: {}", MDC.get("transactionId"), username);
        userService.activateOrDeactivate(username);
        userService.logout(username);
    }


    @Override
    @Transactional(readOnly = true)
    public List<TrainingDto> getTrainingHistory(String username,
                                                LocalDate from,
                                                LocalDate to,
                                                String trainerName,
                                                String trainerLastName) {
        log.info("[{}] SERVICE - Getting training history for: {}", MDC.get("transactionId"), username);

        traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found: " + username));

        List<Training> trainings = trainingRepository.findHistoryForTrainee(
                username, from, to, trainerName, trainerLastName
        );

        return trainingMapper.toTrainingDtoList(trainings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerDto> getUnassignedTrainers(String authUsername) {
        log.info("[{}] SERVICE - Getting unassigned trainers for: {}", MDC.get("transactionId"), authUsername);

        traineeRepository.findByUserUsername(authUsername)
                .orElseThrow(() -> new NotFoundException("Trainee not found: " + authUsername)); /* repo mevcut */

        List<Trainer> trainers = trainerRepository.findUnassignedForTraineeUsername(authUsername);
        return trainerMapper.toTrainerDtoList(trainers);
    }



    @Override
    @Transactional
    public List<TrainerDto> updateTrainerList(String username, List<TrainerUsernameRequest> requestList) {
        log.info("[{}] SERVICE Layer - Updating trainer list for trainee: {}",
                MDC.get("transactionId"), username);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found: " + username));

        List<Training> currentWithTrainer = trainingRepository
                .findByTraineeAndTrainerIsNotNull(trainee);
        for (Training t : currentWithTrainer) {
            t.setTrainer(null);
        }
        if (!currentWithTrainer.isEmpty()) {
            trainingRepository.saveAll(currentWithTrainer);
        }

        List<String> distinctUsernames = requestList.stream()
                .map(TrainerUsernameRequest::getUsername)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();

        List<Trainer> assigned = new ArrayList<>();

        for (String trainerUsername : distinctUsernames) {
            Trainer trainer = trainerRepository.findByUserUsername(trainerUsername)
                    .orElseThrow(() -> new NotFoundException("Trainer not found: " + trainerUsername));

            TrainingType trainingType = trainingTypeRepository
                    .findByName(trainer.getSpecialization())
                    .orElseThrow(() -> new NotFoundException(
                            "TrainingType not found for specialization: " + trainer.getSpecialization()));

            Training training = new Training();
            training.setTrainee(trainee);
            training.setTrainer(trainer);
            training.setTrainingType(trainingType);
            training.setDate(LocalDate.now());
            training.setDurationMinutes(30);

            trainingRepository.save(training);
            assigned.add(trainer);
        }
        return trainerMapper.toTrainerDtoList(assigned);
    }

    @Override
    public Trainee getTraineeById(Long traineeId) {
        log.info("[{}] SERVICE Layer - Getting trainee by ID: {}", MDC.get("transactionId"), traineeId);
        return traineeRepository.findById(traineeId)
                .orElseThrow(() -> {
                    log.warn("[{}] SERVICE Layer - Trainee not found by ID: {}", MDC.get("transactionId"), traineeId);
                    return new NotFoundException("Trainee with id " + traineeId + " not found");
                });
    }

    private Trainee getTraineeEntityByUsername(String username) {
        return traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    log.warn("[{}] SERVICE Layer - Trainee not found by username: {}", MDC.get("transactionId"), username);
                    return new NotFoundException("Trainee not found: " + username);
                });
    }


    private TraineeDto createTraineeEntity(User user, CreateTraineeRequest request) {
        log.info("[{}] SERVICE Layer - Creating Trainee entity for user ID: {}", MDC.get("transactionId"), user.getId());
        Trainee trainee = Trainee.builder()
                .user(user)
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .build();
        Trainee savedTrainee = traineeRepository.save(trainee);
        log.info("[{}] SERVICE Layer - Trainee created with ID: {}", MDC.get("transactionId"), savedTrainee.getId());
        return traineeMapper.toTraineeDto(savedTrainee);
    }
}
