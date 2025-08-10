package com.epam.gymcrm.service.impl;

import com.epam.gymcrm.dto.TrainingTypeDto;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.exception.UnauthorizedException;
import com.epam.gymcrm.mapper.TrainingMapper;
import com.epam.gymcrm.mapper.TrainingTypeMapper;
import com.epam.gymcrm.model.Trainee;
import com.epam.gymcrm.model.Trainer;
import com.epam.gymcrm.model.TrainingType;
import com.epam.gymcrm.model.enums.Specialization;
import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.repository.TrainingRepository;
import com.epam.gymcrm.dto.TrainingDto;
import com.epam.gymcrm.model.Training;
import com.epam.gymcrm.repository.TrainingTypeRepository;
import com.epam.gymcrm.request.training.CreateTrainingRequest;
import com.epam.gymcrm.request.training.UpdateTrainingRequest;
import com.epam.gymcrm.response.TrainingResponse;
import com.epam.gymcrm.service.ITrainingService;
import com.epam.gymcrm.service.IUserService;
import com.epam.gymcrm.util.LogUtil;
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
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;



    @Override
    public TrainingDto createTraining(CreateTrainingRequest request) {
        String txId = LogUtil.getTransactionId();
        log.info("[{}] SERVICE Layer - Creating training: trainerId={}, traineeId={}, date={}, type={}, duration={}",
                txId, request.getTrainerId(), request.getTraineeId(), request.getDate(), request.getType(), request.getDurationMinutes());
        Trainer trainer = trainerRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new NotFoundException("Trainer not found"));

        Trainee trainee = traineeRepository.findById(request.getTraineeId())
                .orElseThrow(() -> new NotFoundException("Trainee not found"));

        TrainingType trainingType = trainingTypeRepository.findById(request.getType())
                .orElseThrow(() -> {
                    log.warn("[{}] SERVICE Layer - TrainingType not found for id: {}", txId, request.getType());
                    return new NotFoundException("Training type not found for id: " + request.getType());
                });
        Training training = Training.builder()
                .date(LocalDate.parse(request.getDate()))
                .durationMinutes(request.getDurationMinutes())
                .trainer(trainer)
                .trainee(trainee)
                .trainingType(trainingType)
                .build();

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
    private TrainingResponse mapToTrainingResponse(Training training) {
        return TrainingResponse.builder()
                .id(training.getId())
                .traineeId(training.getTrainee().getId())
                .traineeFirstName(training.getTrainee().getUser().getFirstName())
                .traineeLastName(training.getTrainee().getUser().getLastName())
                .trainerId(training.getTrainer().getId())
                .trainerFirstName(training.getTrainer().getUser().getFirstName())
                .trainerLastName(training.getTrainer().getUser().getLastName())
                .trainingTypeId(training.getTrainingType().getId())
                .trainingTypeName(training.getTrainingType().getName().toString())
                .date(training.getDate())
                .durationMinutes(training.getDurationMinutes())
                .build();
    }

}
