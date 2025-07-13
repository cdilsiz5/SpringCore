// GymFacade.java
package com.epam.springcore.facade;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.request.CreateTraineeRequest;
import com.epam.springcore.request.CreateTrainerRequest;
import com.epam.springcore.request.CreateTrainingRequest;
import com.epam.springcore.service.ITraineeService;
import com.epam.springcore.service.ITrainerService;
import com.epam.springcore.service.ITrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GymFacade {

    private final ITrainerService trainerService;
    private final ITraineeService traineeService;
    private final ITrainingService trainingService;

    @Autowired
    public GymFacade(ITrainerService trainerService, ITraineeService traineeService, ITrainingService trainingService) {
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }

    // Trainee Operations
    public TraineeDto registerTrainee(CreateTraineeRequest request) {
        return traineeService.createTrainee(request);
    }

    public List<TraineeDto> getAllTrainees() {
        return traineeService.getAllTrainees();
    }

    // Trainer Operations
    public TrainerDto registerTrainer(CreateTrainerRequest request) {
        return trainerService.createTrainer(request);
    }

    public List<TrainerDto> getAllTrainers() {
        return trainerService.getAllTrainers();
    }

    // Training Operations
    public TrainingDto scheduleTraining(CreateTrainingRequest request) {
        return trainingService.createTraining(request);
    }

    public List<TrainingDto> getAllTrainings() {
        return trainingService.getAllTrainings();
    }
}