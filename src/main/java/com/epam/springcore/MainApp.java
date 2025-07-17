package com.epam.springcore;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.facade.GymFacade;
import com.epam.springcore.model.enums.Specialization;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainer.CreateTrainerRequest;
import com.epam.springcore.request.training.CreateTrainingRequest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class MainApp {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.epam.springcore");

        GymFacade gymFacade = context.getBean(GymFacade.class);

        System.out.println("========== MAIN CLASS TEST ==========");

        // Create a trainee
        CreateTraineeRequest traineeRequest = new CreateTraineeRequest(
                "Ali", "Yilmaz", LocalDate.of(2000, 5, 15), "Istanbul"
        );
        TraineeDto traineeDto = gymFacade.registerTrainee(traineeRequest);
        System.out.println("Created Trainee: " + traineeDto);

        // Create a trainer
        CreateTrainerRequest trainerRequest = new CreateTrainerRequest(
                "Ahmet", "Kaya", Specialization.YOGA
        );
        TrainerDto trainerDto = gymFacade.registerTrainer(trainerRequest);
        System.out.println("Created Trainer: " + trainerDto);

        // Schedule a training
        CreateTrainingRequest trainingRequest = new CreateTrainingRequest(
                traineeDto.getId(),
                trainerDto.getId(),
                "2025-08-01",
                "YOGA",
                45
        );
        TrainingDto trainingDto = gymFacade.scheduleTraining(trainingRequest);
        System.out.println("Scheduled Training: " + trainingDto);

        System.out.println("======================================");
        System.out.println("All operations completed successfully.");
        System.out.println("======================================");
        context.close();
    }
}
