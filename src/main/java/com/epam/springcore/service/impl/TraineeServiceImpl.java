package com.epam.springcore.service.impl;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.UnauthorizedException;
import com.epam.springcore.mapper.TraineeMapper;
import com.epam.springcore.model.*;
import com.epam.springcore.repository.TraineeRepository;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import com.epam.springcore.request.trainer.TrainerUsernameRequest;
import com.epam.springcore.request.training.CreateTrainingRequest;
import com.epam.springcore.request.user.CreateUserRequest;
import com.epam.springcore.response.LoginCredentialsResponse;
import com.epam.springcore.service.ITraineeService;
import com.epam.springcore.service.ITrainerService;
import com.epam.springcore.service.ITrainingService;
import com.epam.springcore.service.IUserService;
import com.epam.springcore.util.LogUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements ITraineeService {

    private final TraineeRepository traineeRepository;
    private final TraineeMapper traineeMapper;
    private final ITrainingService trainingService;
    private final ITrainerService trainerService;
    private final IUserService userService;

    @Override
    @Transactional
    public LoginCredentialsResponse createTrainee(CreateTraineeRequest request) {
        String txId = LogUtil.getTransactionId();
        if (txId == null) txId = "N/A";
        log.info("[{}] SERVICE Layer - Creating trainee via public endpoint", txId);
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        User savedUser = userService.createUserEntity(createUserRequest);
        createTraineeEntity(savedUser, request);
        return new LoginCredentialsResponse(savedUser.getUsername(), savedUser.getPassword());
    }

    @Override
    public TraineeDto getTraineeByUsername(String username) {
        validate(username);
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
        validate(username);
        log.info("[{}] SERVICE Layer - Updating Trainee: {}", MDC.get("transactionId"), username);
        Trainee trainee = getTraineeEntityByUsername(username);
        traineeMapper.updateTraineeRequest(request, trainee);
        Trainee updatedTrainee = traineeRepository.save(trainee);
        userService.logout(username);
        return traineeMapper.toTraineeDto(updatedTrainee);
    }

    @Override
    public void deleteTrainee(String username) {
        validate(username);
        log.info("[{}] SERVICE Layer - Deleting Trainee: {}", MDC.get("transactionId"), username);
        Trainee trainee = getTraineeEntityByUsername(username);
        traineeRepository.delete(trainee);
        userService.logout(username);
    }

    @Override
    public void toggleActivation(String username) {
        validate(username);
        log.info("[{}] SERVICE Layer - Toggling activation for: {}", MDC.get("transactionId"), username);
        userService.activateOrDeactivate(username);
        userService.logout(username);
    }

    @Override
    public List<TrainingDto> getTrainingHistory(String username, LocalDate from, LocalDate to, String trainerName, String trainerLastName) {
        validate(username);
        log.info("[{}] SERVICE Layer - Getting training history for: {}", MDC.get("transactionId"), username);

        Trainee trainee = getTraineeEntityByUsername(username);

        List<TrainingDto> result = trainingService.findAllByTrainee(trainee).stream()
                .filter(t -> from == null || !t.getDate().isBefore(from))
                .filter(t -> to == null || !t.getDate().isAfter(to))
                .filter(t -> {
                    if (trainerName == null && trainerLastName == null) return true;
                    String first = t.getTrainer().getUser().getFirstName().toLowerCase();
                    String last = t.getTrainer().getUser().getLastName().toLowerCase();
                    boolean firstMatch = trainerName == null || first.contains(trainerName.toLowerCase());
                    boolean lastMatch = trainerLastName == null || last.contains(trainerLastName.toLowerCase());
                    return firstMatch && lastMatch;
                })
                .collect(Collectors.toList());

        userService.logout(username);
        return result;
    }

    @Override
    public List<TrainerDto> getUnassignedTrainers(String authUsername) {
        validate(authUsername);
        log.info("[{}] SERVICE Layer - Getting unassigned trainers for: {}", MDC.get("transactionId"), authUsername);

        Trainee trainee = getTraineeEntityByUsername(authUsername);
        List<TrainerDto> allTrainers = trainerService.getAllTrainers();

        List<TrainerDto> unassigned = allTrainers.stream()
                .filter(trainer -> !trainer.getTrainings().stream()
                        .anyMatch(training -> training.getTrainee().equals(trainee)))
                .collect(Collectors.toList());

        userService.logout(authUsername);
        return unassigned;
    }

    @Override
    @Transactional
    public List<TrainerDto> updateTrainerList(String username, List<TrainerUsernameRequest> requestList) {
        validate(username);
        log.info("[{}] SERVICE Layer - Updating trainer list for trainee: {}", MDC.get("transactionId"), username);

        Trainee trainee = getTraineeEntityByUsername(username);

        // Remove current assignments
        List<TrainingDto> currentTrainings = trainingService.findAllByTrainee(trainee).stream()
                .filter(training -> training.getTrainer() != null)
                .collect(Collectors.toList());

        for (TrainingDto training : currentTrainings) {
            training.setTrainer(null);
        }

        // Assign new trainers
        List<TrainerDto> assignedTrainers = requestList.stream()
                .map(req -> {
                    TrainerDto trainerDto = trainerService.getTrainerByUsername(req.getUsername());
                    Trainer trainer = trainerService.getTrainerById(trainerDto.getId());

                    TrainingType trainingType = trainingService.findTrainingTypeByName(trainer.getSpecialization());

                    CreateTrainingRequest createRequest = CreateTrainingRequest.builder()
                            .traineeId(trainee.getId())
                            .trainerId(trainer.getId())
                            .type(trainingType.getId())
                            .date(LocalDate.now().toString())
                            .durationMinutes(30)
                            .build();

                    trainingService.createTraining(createRequest);
                    return trainerDto;
                })
                .collect(Collectors.toList());

        userService.logout(username);
        return assignedTrainers;
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

    private void validate(String authUsername) {
        if (!userService.isAuthenticated(authUsername)) {
            log.warn("[{}] SERVICE Layer - Unauthorized access attempt for: {}", MDC.get("transactionId"), authUsername);
            throw new UnauthorizedException("User not authenticated: " + authUsername);
        }
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
