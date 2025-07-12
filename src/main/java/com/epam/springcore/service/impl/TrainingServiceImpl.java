package com.epam.springcore.service.impl;

import com.epam.springcore.dao.TrainingDao;
import com.epam.springcore.dao.UserDao;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.model.Training;
import com.epam.springcore.model.User;
import com.epam.springcore.model.enums.TrainingType;
import com.epam.springcore.request.CreateTrainingRequest;
import com.epam.springcore.service.ITrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrainingServiceImpl implements ITrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TrainingDao trainingDao;
    private final UserDao userDao;

    public TrainingServiceImpl(TrainingDao trainingDao, UserDao userDao) {
        this.trainingDao = trainingDao;
        this.userDao = userDao;
    }

    @Override
    public TrainingDto createTraining(CreateTrainingRequest request) {
        log.info("Creating new training between trainerId={} and traineeId={}",
                request.getTrainerId(), request.getTraineeId());

        Training training = new Training(
                request.getTraineeId(),
                request.getTrainerId(),
                request.getDate(),
                TrainingType.valueOf(request.getType().toUpperCase()),
                request.getDurationMinutes()
        );
        Training saved = trainingDao.save(training);

        log.debug("Training saved with id={}", saved.getId());
        return toDto(saved);
    }

    @Override
    public TrainingDto getTraining(String id) {
        log.info("Fetching training with ID: {}", id);
        Training training = trainingDao.findById(id);
        if (training == null) {
            log.warn("Training with ID {} not found", id);
            return null;
        }
        return toDto(training);
    }

    @Override
    public List<TrainingDto> getAllTrainings() {
        log.info("Fetching all trainings");
        List<Training> trainings = (List<Training>) trainingDao.findAll();
        List<TrainingDto> dtos = new ArrayList<>();
        for (Training training : trainings) {
            dtos.add(toDto(training));
        }
        return dtos;
    }

    @Override
    public TrainingDto updateTraining(String id, CreateTrainingRequest request) {
        log.info("Updating training with ID: {}", id);
        Training existingTraining = checkTrainingExist(id);

        existingTraining.setTraineeId(request.getTraineeId());
        existingTraining.setTrainerId(request.getTrainerId());
        existingTraining.setDate(request.getDate());
        existingTraining.setType(TrainingType.valueOf(request.getType().toUpperCase()));
        existingTraining.setDurationMinutes(request.getDurationMinutes());

        Training updated = trainingDao.save(existingTraining);
        log.debug("Training with ID {} updated", id);
        return toDto(updated);
    }

    @Override
    public void deleteTraining(String id) {
        log.info("Deleting training with ID: {}", id);
        checkTrainingExist(id);
        trainingDao.delete(id);
        log.debug("Training with ID {} deleted", id);
    }

    private Training checkTrainingExist(String id) {
        Training existingTraining = trainingDao.findById(id);
        if (existingTraining == null) {
            log.error("Training with ID {} not found", id);
            throw new RuntimeException("Training with ID: " + id + " not found");
        }
        return existingTraining;
    }

    private TrainingDto toDto(Training training) {
        User trainer = userDao.findById(training.getTrainerId());
        User trainee = userDao.findById(training.getTraineeId());

        TrainingDto dto = new TrainingDto();
        dto.setId(training.getId());
        dto.setTrainerId(trainer.getId());
        dto.setTraineeId(trainee.getId());
        dto.setDate(training.getDate());
        dto.setType(training.getType().toString());
        dto.setDurationMinutes(training.getDurationMinutes());

        return dto;
    }
}
