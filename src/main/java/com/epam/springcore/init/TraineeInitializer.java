package com.epam.springcore.init;

import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.User;
import com.epam.springcore.request.TraineeInitializeRequest;
import com.epam.springcore.service.ITraineeService;
import com.epam.springcore.service.IUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class TraineeInitializer {

    @Value("${trainee.data.file}")
    private Resource traineeDataFile;

    private final ITraineeService traineeService;
    private final IUserService userService;
    private final ObjectMapper objectMapper;

    @Autowired
    public TraineeInitializer(ITraineeService traineeService, IUserService userService, ObjectMapper objectMapper) {
        this.traineeService = traineeService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }
    @PostConstruct
    public void init() {
        System.out.println("TraineeInitializer started (JSON).");
        try {
            List<TraineeInitializeRequest> trainees = objectMapper.readValue(
                    traineeDataFile.getInputStream(),
                    new TypeReference<List<TraineeInitializeRequest>>() {}
            );
             for (TraineeInitializeRequest traineeInitializeRequest : trainees) {
                User user=new User(
                        traineeInitializeRequest.getFirstName(),
                        traineeInitializeRequest.getLastName(),
                        traineeService.getAllTrainees());

                userService.save(user);
                Trainee trainee=new Trainee(traineeInitializeRequest.getDateOfBirth(),
                        traineeInitializeRequest.getAddress(),
                        user.getId());

                traineeService.createTrainee(trainee);
                System.out.println("Loaded trainee: " + trainee);
            }

        } catch (Exception e) {
            System.out.println("Error occurred while loading trainees from JSON:");
            e.printStackTrace();
        }
        System.out.println("TraineeInitializer finished.");
    }
}
