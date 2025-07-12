package com.epam.springcore.service.impl;

import com.epam.springcore.dao.TrainingDao;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.model.Training;
import com.epam.springcore.request.CreateTrainingRequest;
import com.epam.springcore.service.ITrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.epam.springcore.mapper.TrainingMapper.TRAINING_MAPPER;

@Service
public class TrainingServiceImpl implements ITrainingService {
    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);


    private final TrainingDao trainingDao;

    public TrainingServiceImpl(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Override
    public TrainingDto createTraining(CreateTrainingRequest request) {
        log.info("Creating new training between trainerId={} and traineeId={}", request.getTrainerId(), request.getTraineeId());

        Training training = TRAINING_MAPPER.toTraining(request);
        Training saved = trainingDao.save(training);

        log.debug("Training saved with id={}", saved.getId());
        return TRAINING_MAPPER.toTrainingDto(saved);
    }

    @Override
    public TrainingDto getTraining(String id) {
        log.info("Fetching training with ID: {}", id);
        Training training = trainingDao.findById(id);
        if (training == null) {
            log.warn("Training with ID {} not found", id);
            return null;
        }
        return TRAINING_MAPPER.toTrainingDto(training);
    }

    @Override
    public List<TrainingDto> getAllTrainings() {
        log.info("Fetching all trainings");
        return TRAINING_MAPPER.toTrainingDtoList((List<Training>) trainingDao.findAll());
    }

    @Override
    public TrainingDto updateTraining(String id, CreateTrainingRequest request) {
        log.info("Updating training with ID: {}", id);
        Training existingTraining = checkTrainingExist(id);
        TRAINING_MAPPER.updateTraining(request, existingTraining);
        Training updated = trainingDao.save(existingTraining);
        log.debug("Training with ID {} updated", id);
        return TRAINING_MAPPER.toTrainingDto(updated);
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
}
