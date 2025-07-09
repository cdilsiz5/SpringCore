package com.epam.springcore.facade;

import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.Training;
import com.epam.springcore.model.User;
import com.epam.springcore.service.ITraineeService;
import com.epam.springcore.service.ITrainerService;
import com.epam.springcore.service.ITrainingService;
import com.epam.springcore.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class GymFacade {

    private final ITrainerService trainerService;
    private final ITraineeService traineeService;
    private final ITrainingService trainingService;
    private final IUserService userService;


    @Autowired
    public GymFacade(ITrainerService trainerService, ITraineeService traineeService, ITrainingService trainingService,IUserService userService) {
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingService = trainingService;
        this.userService = userService;
    }
    public void registerUser(User user) {
        userService.save(user);
    }

    public  Collection<User>  getAllUsers(){
        return userService.findAll();
    }

    // Trainer
    public void registerTrainer(Trainer trainer) {
        trainerService.createTrainer(trainer);
    }

    public Trainer getTrainer(String id) {
        return trainerService.getTrainer(id);
    }

    // Trainee
    public void registerTrainee(Trainee trainee) {
        traineeService.createTrainee(trainee);
    }

    public Trainee getTrainee(String id) {
        return traineeService.getTrainee(id);
    }

    // Training
    public void createTraining(Training training) {
        trainingService.createTraining(training);
    }

    public Training getTraining(String id) {
        return trainingService.getTraining(id);
    }

    public Collection<Training> listTrainings() {
        return trainingService.getAllTrainings();
    }
}