package com.epam.springcore.service.impl;

import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.mapper.TrainingMapper;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.TrainingType;
import com.epam.springcore.repository.TrainingRepository;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.model.Training;
import com.epam.springcore.repository.TrainingTypeRepository;
import com.epam.springcore.request.training.CreateTrainingRequest;
import com.epam.springcore.request.training.UpdateTrainingRequest;
import com.epam.springcore.service.ITrainingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements ITrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TrainingRepository trainingRepository;
    private final TrainingMapper trainingMapper;
    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public TrainingDto createTraining(CreateTrainingRequest request) {
        log.info("Creating training between trainerId={} and traineeId={}",
                request.getTrainerId(), request.getTraineeId());
        Training training = trainingMapper.createTraining(request);
        TrainingType trainingType=trainingTypeRepository.findById(request.getType()).orElseThrow(() -> new NotFoundException("Training type not found"));
        training.setTrainingType(trainingType);
        Training savedTraining = trainingRepository.save(training);
        log.info("Training created with ID: {}", savedTraining.getId());
        return trainingMapper.toTrainingDto(savedTraining);
    }

    @Override
    public TrainingDto getTraining(Long id) {
        log.info("Fetching training with ID: {}", id);
        Training training = getTrainingEntityById(id);
        return trainingMapper.toTrainingDto(training);
    }

    @Override
    public List<TrainingDto> getAllTrainings() {
        log.info("Fetching all trainings");
        List<Training> trainings = trainingRepository.findAll();
        log.debug("Total trainings fetched: {}", trainings.size());
        return trainingMapper.toTrainingDtoList(trainings);
    }

    @Override
    public TrainingDto updateTraining(Long id, UpdateTrainingRequest request) {
        log.info("Updating training with ID: {}", id);
        Training training = getTrainingEntityById(id);
        trainingMapper.updateTrainingRequest(request, training);
        Training savedTraining = trainingRepository.save(training);
        log.info("Training updated. ID: {}", savedTraining.getId());
        return trainingMapper.toTrainingDto(savedTraining);
    }

    @Override
    public void deleteTraining(Long id) {
        log.info("Deleting training with ID: {}", id);
        Training training = getTrainingEntityById(id);
        trainingRepository.delete(training);
        log.info("Training deleted. ID: {}", training.getId());
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

    private Training getTrainingEntityById(Long id) {
        return trainingRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Training not found with ID: {}", id);
                    return new NotFoundException("Training with id: " + id + " not found");
                });
    }
}
