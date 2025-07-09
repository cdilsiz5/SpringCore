package com.epam.springcore.init;

import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.User;
import com.epam.springcore.request.TrainerInitializeRequest;
import com.epam.springcore.service.ITraineeService;
import com.epam.springcore.service.ITrainerService;
import com.epam.springcore.service.IUserService;
import com.epam.springcore.service.impl.TrainerServiceImpl;
import com.epam.springcore.service.impl.UserServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class TrainerInitializer {

    @Value("${trainer.data.file}")
    private Resource trainerDataFile;

    private final ITrainerService trainerService;
    private final IUserService userService;
    private final ObjectMapper objectMapper;

    @Autowired
    public TrainerInitializer( ITrainerService trainerService, IUserService userService, ObjectMapper objectMapper) {
        this.trainerService = trainerService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }


    @PostConstruct
    public void init() {
        System.out.println("TrainerInitializer started (JSON).");
        try {
            List<TrainerInitializeRequest> trainers = objectMapper.readValue(
                    trainerDataFile.getInputStream(),
                    new TypeReference<List<TrainerInitializeRequest>>() {}
            );
            for (TrainerInitializeRequest trainerInitializeRequest : trainers) {
                User user=new User(
                        trainerInitializeRequest.getFirstName(),
                        trainerInitializeRequest.getLastName(),
                        trainerService.getAllTrainers());

                userService.save(user);
                Trainer trainer=new Trainer( trainerInitializeRequest.getSpecialty(),user.getId());

                trainerService.createTrainer(trainer);
                System.out.println("Loaded trainer: " + trainer);
            }

        } catch (Exception e) {
            System.out.println("Error occurred while loading trainers from JSON:");
            e.printStackTrace();
        }
}
}
