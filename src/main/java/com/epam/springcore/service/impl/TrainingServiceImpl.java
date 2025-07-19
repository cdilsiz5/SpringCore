package com.epam.springcore.service.impl;

import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.repository.TrainingRepository;
import com.epam.springcore.repository.UserRepository;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.model.Training;
import com.epam.springcore.request.training.CreateTrainingRequest;
import com.epam.springcore.service.ITrainingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.epam.springcore.mapper.TrainingMapper.TRAINING_MAPPER;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements ITrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;


    @Override
    public TrainingDto createTraining(CreateTrainingRequest request) {
        return null;
    }

    @Override
    public TrainingDto getTraining(String id) {
        return null;
    }

    @Override
    public List<TrainingDto> getAllTrainings() {
        return List.of();
    }

    @Override
    public TrainingDto updateTraining(String id, CreateTrainingRequest request) {
        return null;
    }

    @Override
    public void deleteTraining(String id) {

    }

    @Override
    public List<TrainingDto> findAllByTrainer(Trainer trainer) {
        List<Training> trainingList=trainingRepository.findAllByTrainer(trainer);
        return TRAINING_MAPPER.toTrainingDtoList(trainingList);
    }

    @Override
    public List<TrainingDto> findAllByTrainee(Trainee trainee) {
        List<Training> trainingList=trainingRepository.findAllByTrainee(trainee);
        return TRAINING_MAPPER.toTrainingDtoList(trainingList);
    }


}