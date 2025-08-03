package com.epam.springcore.service.impl;

import com.epam.springcore.dto.TrainingTypeDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.UnauthorizedException;
import com.epam.springcore.mapper.TrainingMapper;
import com.epam.springcore.mapper.TrainingTypeMapper;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.TrainingType;
import com.epam.springcore.model.enums.Specialization;
import com.epam.springcore.repository.TrainingRepository;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.model.Training;
import com.epam.springcore.repository.TrainingTypeRepository;
import com.epam.springcore.request.training.CreateTrainingRequest;
import com.epam.springcore.request.training.UpdateTrainingRequest;
import com.epam.springcore.service.ITrainingService;
import com.epam.springcore.service.IUserService;
import com.epam.springcore.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements ITrainingService {

    private static final String SYSTEM_ADMIN_USERNAME = "system-admin";

    private final TrainingRepository trainingRepository;
    private final TrainingMapper trainingMapper;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingTypeMapper trainingTypeMapper;
    private final IUserService userService;

    @Override
    public TrainingDto createTraining(CreateTrainingRequest request) {
        String txId = LogUtil.getTransactionId();
        log.info("[{}] SERVICE Layer - Creating training: trainerId={}, traineeId={}, date={}, type={}, duration={}",
                txId, request.getTrainerId(), request.getTraineeId(), request.getDate(), request.getType(), request.getDurationMinutes());

        Training training = new Training();
        TrainingType trainingType = trainingTypeRepository.findById(request.getType())
                .orElseThrow(() -> {
                    log.warn("[{}] SERVICE Layer - TrainingType not found for id: {}", txId, request.getType());
                    return new NotFoundException("Training type not found for id: " + request.getType());
                });
        training.setTrainingType(trainingType);
        training.setDurationMinutes(request.getDurationMinutes());
        training.setDate(LocalDate.parse(request.getDate()));

        Training saved = trainingRepository.save(training);
        log.info("[{}] SERVICE Layer - Training saved successfully with ID: {}", txId, saved.getId());

        return trainingMapper.toTrainingDto(saved);
    }

    @Override
    public TrainingDto getTraining(Long id) {
        validate();
        log.info("[{}] SERVICE Layer - Fetching training with ID: {}", MDC.get("transactionId"), id);
        Training training = getTrainingEntityById(id);
        userService.logout(SYSTEM_ADMIN_USERNAME);
        return trainingMapper.toTrainingDto(training);
    }

    @Override
    public List<TrainingDto> getAllTrainings() {
        validate();
        log.info("[{}] SERVICE Layer - Fetching all trainings", MDC.get("transactionId"));
        List<Training> trainings = trainingRepository.findAll();
        log.debug("[{}] SERVICE Layer - Total trainings fetched: {}", MDC.get("transactionId"), trainings.size());
        userService.logout(SYSTEM_ADMIN_USERNAME);
        return trainingMapper.toTrainingDtoList(trainings);
    }

    @Override
    public TrainingDto updateTraining(Long id, UpdateTrainingRequest request) {
        validate();
        log.info("[{}] SERVICE Layer - Updating training with ID: {}", MDC.get("transactionId"), id);
        Training training = getTrainingEntityById(id);
        trainingMapper.updateTrainingRequest(request, training);
        Training savedTraining = trainingRepository.save(training);
        log.info("[{}] SERVICE Layer - Training updated. ID: {}", MDC.get("transactionId"), savedTraining.getId());
        userService.logout(SYSTEM_ADMIN_USERNAME);
        return trainingMapper.toTrainingDto(savedTraining);
    }

    @Override
    public void deleteTraining(Long id) {
        validate();
        log.info("[{}] SERVICE Layer - Deleting training with ID: {}", MDC.get("transactionId"), id);
        Training training = getTrainingEntityById(id);
        trainingRepository.delete(training);
        log.info("[{}] SERVICE Layer - Training deleted. ID: {}", MDC.get("transactionId"), training.getId());
        userService.logout(SYSTEM_ADMIN_USERNAME);
    }

    @Override
    public List<TrainingDto> findAllByTrainer(Trainer trainer) {
        log.info("[{}] SERVICE Layer - Fetching trainings for trainer ID: {}", MDC.get("transactionId"), trainer.getId());
        List<Training> trainingList = trainingRepository.findAllByTrainer(trainer);
        log.debug("[{}] SERVICE Layer - Trainings found for trainer ID {}: {}", MDC.get("transactionId"), trainer.getId(), trainingList.size());
        return trainingMapper.toTrainingDtoList(trainingList);
    }

    @Override
    public List<TrainingDto> findAllByTrainee(Trainee trainee) {
        log.info("[{}] SERVICE Layer - Fetching trainings for trainee ID: {}", MDC.get("transactionId"), trainee.getId());
        List<Training> trainingList = trainingRepository.findAllByTrainee(trainee);
        log.debug("[{}] SERVICE Layer - Trainings found for trainee ID {}: {}", MDC.get("transactionId"), trainee.getId(), trainingList.size());
        return trainingMapper.toTrainingDtoList(trainingList);
    }

    @Override
    public TrainingType findTrainingTypeByName(Specialization name) {
        log.info("[{}] SERVICE Layer - Fetching TrainingType by specialization: {}", MDC.get("transactionId"), name);
        return trainingTypeRepository.findByName(name)
                .orElseThrow(() -> {
                    log.warn("[{}] SERVICE Layer - TrainingType not found for specialization: {}", MDC.get("transactionId"), name);
                    return new NotFoundException("Training type not found for specialization: " + name);
                });
    }

    @Override
    public List<TrainingTypeDto> getAllTrainingTypes() {
        validate();
        log.info("[{}] SERVICE Layer - Fetching all training types", MDC.get("transactionId"));
        List<TrainingType> trainingType = trainingTypeRepository.findAll();
        return trainingTypeMapper.toTrainingTypeDtoList(trainingType);
    }

    private Training getTrainingEntityById(Long id) {
        return trainingRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[{}] SERVICE Layer - Training not found with ID: {}", MDC.get("transactionId"), id);
                    return new NotFoundException("Training with id: " + id + " not found");
                });
    }

    private void validate() {
        if (!userService.isAuthenticated(SYSTEM_ADMIN_USERNAME)) {
            log.warn("[{}] SERVICE Layer - Unauthorized access by system-admin", MDC.get("transactionId"));
            throw new UnauthorizedException("SystemAdmin not authenticated");
        }
    }
}
