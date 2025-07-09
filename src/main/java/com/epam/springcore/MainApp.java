package com.epam.springcore;

import com.epam.springcore.config.AppConfig;
import com.epam.springcore.facade.GymFacade;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.Training;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainApp {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        GymFacade facade = context.getBean(GymFacade.class);

        System.out.println("===== TRAINERS FROM JSON =====");
        for (int i = 6; i <= 10; i++) {
            Trainer trainer = facade.getTrainer(String.valueOf(i));
            System.out.println("Trainer ID " + i + ": " + trainer);
        }

        System.out.println("\n===== TRAINEES FROM JSON =====");
        for (int i = 1; i <= 5; i++) {
            Trainee trainee = facade.getTrainee(String.valueOf(i));
            System.out.println("Trainee ID " + i + ": " + trainee);
        }

        System.out.println("\n===== TRAININGS FROM JSON =====");
        for (Training training : facade.listTrainings()) {
            System.out.println(training);
        }

        context.close();
    }
}
