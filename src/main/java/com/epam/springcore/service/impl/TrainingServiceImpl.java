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
import com.epam.springcore.service.ITraineeService;
import com.epam.springcore.service.ITrainerService;
import com.epam.springcore.service.ITrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingServiceImpl implements ITrainingService {

    private static final String SYSTEM_ADMIN_USERNAME = "system-admin";

    private final TrainingRepository trainingRepository;
    private final TrainingMapper trainingMapper;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserServiceImpl userService;
    private final ITrainerService trainerService;
    private final ITraineeService traineeService;
    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    public TrainingDto createTraining(CreateTrainingRequest request) {
        log.info("Creating training: trainerId={}, traineeId={}, date={}, type={}, duration={}",
                request.getTrainerId(), request.getTraineeId(), request.getDate(), request.getType(), request.getDurationMinutes());

        Training training = new Training();

        training.setTrainee(traineeService.getTraineeById(request.getTraineeId()));
        training.setTrainer(trainerService.getTrainerById(request.getTrainerId()));

        TrainingType trainingType = trainingTypeRepository.findById(request.getType())
                .orElseThrow(() -> new NotFoundException("Training type not found for id: " + request.getType()));
        training.setTrainingType(trainingType);

        training.setDurationMinutes(request.getDurationMinutes());
        training.setDate(LocalDate.parse(request.getDate()));

        Training saved = trainingRepository.save(training);

        log.info("Training saved successfully with ID: {}", saved.getId());

        return trainingMapper.toTrainingDto(saved);
    }

    @Override
    public TrainingDto getTraining(Long id) {
        validate();
        log.info("Fetching training with ID: {}", id);
        Training training = getTrainingEntityById(id);
        userService.logout(SYSTEM_ADMIN_USERNAME);
        return trainingMapper.toTrainingDto(training);
    }

    @Override
    public List<TrainingDto> getAllTrainings() {
        validate();
        log.info("Fetching all trainings");
        List<Training> trainings = trainingRepository.findAll();
        log.debug("Total trainings fetched: {}", trainings.size());
        userService.logout(SYSTEM_ADMIN_USERNAME);
        return trainingMapper.toTrainingDtoList(trainings);
    }

    @Override
    public TrainingDto updateTraining(Long id, UpdateTrainingRequest request) {
        validate();
        log.info("Updating training with ID: {}", id);
        Training training = getTrainingEntityById(id);
        trainingMapper.updateTrainingRequest(request, training);
        Training savedTraining = trainingRepository.save(training);
        log.info("Training updated. ID: {}", savedTraining.getId());
        userService.logout(SYSTEM_ADMIN_USERNAME);
        return trainingMapper.toTrainingDto(savedTraining);
    }

    @Override
    public void deleteTraining(Long id) {
        validate();
        log.info("Deleting training with ID: {}", id);
        Training training = getTrainingEntityById(id);
        trainingRepository.delete(training);
        log.info("Training deleted. ID: {}", training.getId());
        userService.logout(SYSTEM_ADMIN_USERNAME);
    }

    @Override
    public List<TrainingDto> findAllByTrainer(Trainer trainer) {
        log.info("Fetching trainings for trainer ID: {}", trainer.getId());
        List<Training> trainingList = trainingRepository.findAllByTrainer(trainer);
        log.debug("Trainings found for trainer ID {}: {}", trainer.getId(), trainingList.size());
        return trainingMapper.toTrainingDtoList(trainingList);
    }

    @Override
    public List<TrainingDto> findAllByTrainee(Trainee trainee) {
        log.info("Fetching trainings for trainee ID: {}", trainee.getId());
        List<Training> trainingList = trainingRepository.findAllByTrainee(trainee);
        log.debug("Trainings found for trainee ID {}: {}", trainee.getId(), trainingList.size());
        return trainingMapper.toTrainingDtoList(trainingList);
    }

    @Override
    public TrainingType findTrainingTypeByName(Specialization name) {
        log.info("Fetching TrainingType by specialization: {}", name);
        return trainingTypeRepository.findByName(name)
                .orElseThrow(() -> {
                    log.warn("TrainingType not found for specialization: {}", name);
                    return new NotFoundException("Training type not found for specialization: " + name);
                });
    }
    @Override
    public List<TrainingTypeDto> getAllTrainingTypes() {
        validate();
        log.info("Fetching all training types");
        List<TrainingType> trainingType=trainingTypeRepository.findAll();
        return trainingTypeMapper.toTrainingTypeDtoList(trainingType);
    }


    private Training getTrainingEntityById(Long id) {
        return trainingRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Training not found with ID: {}", id);
                    return new NotFoundException("Training with id: " + id + " not found");
                });
    }

    private void validate() {
        if (!userService.isAuthenticated(SYSTEM_ADMIN_USERNAME)) {
            throw new UnauthorizedException("SystemAdmin not authenticated");
        }
    }
}
