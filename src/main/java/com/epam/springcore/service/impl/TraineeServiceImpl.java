package com.epam.springcore.service.impl;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.UnauthorizedException;
import com.epam.springcore.mapper.TraineeMapper;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.User;
import com.epam.springcore.repository.TraineeRepository;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import com.epam.springcore.request.user.CreateUserRequest;
import com.epam.springcore.service.ITraineeService;
import com.epam.springcore.service.ITrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class TraineeServiceImpl implements ITraineeService {

    private final TraineeRepository traineeRepository;
    private final ITrainingService trainingService;
    private final TraineeMapper traineeMapper;
    private final UserServiceImpl userService;

    @Override
    public TraineeDto createTrainee(CreateTraineeRequest request) {
        log.info("Creating trainee via public endpoint");
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        User savedUser = userService.createUserEntity(createUserRequest);
        return createTraineeEntity(savedUser);
    }

    @Override
    public TraineeDto getTraineeByUsername(String authUsername, String authPassword, String username) {
        authenticate(authUsername, authPassword);
        log.info("Fetching Trainee by username: {}", username);
        return traineeMapper.toTraineeDto(getTraineeEntityByUsername(username));
    }

    @Override
    public List<TraineeDto> getAllTrainees(String authUsername, String authPassword) {
        authenticate(authUsername, authPassword);
        log.info("Fetching all trainees");
        return traineeMapper.toTraineeDtoList(traineeRepository.findAll());
    }

    @Override
    public TraineeDto updateTrainee(String authUsername, String authPassword, String username, UpdateTraineeRequest request) {
        authenticate(authUsername, authPassword);
        log.info("Updating Trainee with username: {}", username);
        Trainee trainee = getTraineeEntityByUsername(username);
        traineeMapper.updateTraineeRequest(request, trainee);
        Trainee updatedTrainee = traineeRepository.save(trainee);
        log.info("Trainee updated. ID: {}", updatedTrainee.getId());
        return traineeMapper.toTraineeDto(updatedTrainee);
    }

    @Override
    public void deleteTrainee(String authUsername, String authPassword, String username) {
        authenticate(authUsername, authPassword);
        log.info("Deleting Trainee with username: {}", username);
        Trainee trainee = getTraineeEntityByUsername(username);
        traineeRepository.delete(trainee);
        log.info("Trainee deleted. ID: {}", trainee.getId());
    }

    @Override
    public void toggleActivation(String authUsername, String authPassword, String username) {
        authenticate(authUsername, authPassword);
        log.info("Toggling activation for user: {}", username);
        userService.activateOrDeactivate(username);
    }

    @Override
    public List<TrainingDto> getTrainingHistory(String authUsername, String authPassword, String username, LocalDate from, LocalDate to, String trainerName, String trainerLastName) {
        authenticate(authUsername, authPassword);
        log.info("Fetching training history for trainee: {}", username);
        Trainee trainee = getTraineeEntityByUsername(username);
        return trainingService.findAllByTrainee(trainee).stream()
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
    }

    @Override
    public TraineeDto createTraineeEntity(User user) {
        log.info("Creating Trainee entity for user ID: {}", user.getId());
        Trainee trainee = Trainee.builder()
                .user(user)
                .build();
        Trainee savedTrainee = traineeRepository.save(trainee);
        log.info("Trainee saved with ID: {}", savedTrainee.getId());
        return traineeMapper.toTraineeDto(savedTrainee);
    }

    @Override
    public Trainee getTraineeById(Long traineeId) {
        log.info("Fetching trainee by ID: {}", traineeId);
        return traineeRepository.findById(traineeId)
                .orElseThrow(() -> {
                    log.warn("Trainee not found with ID: {}", traineeId);
                    return new NotFoundException("Trainee with id " + traineeId + " not found");
                });
    }

    private Trainee getTraineeEntityByUsername(String username) {
        return traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainee not found with username: {}", username);
                    return new NotFoundException("Trainee not found: " + username);
                });
    }

    private void authenticate(String username, String password) {
        if (!userService.authenticate(username, password)) {
            log.warn("Authentication failed for user: {}", username);
            throw new UnauthorizedException("Unauthorized: invalid username or password");
        }
    }
}
