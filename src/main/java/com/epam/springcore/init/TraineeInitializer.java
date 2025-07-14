package com.epam.springcore.init;

import com.epam.springcore.dao.TraineeDao;
import com.epam.springcore.dao.UserDao;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.User;
import com.epam.springcore.request.init.TraineeInitializeRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class TraineeInitializer {

    @Value("${trainee.data.file}")
    private Resource traineeDataFile;

    private final TraineeDao traineeDao;
    private final UserDao userDao;
    private final ObjectMapper objectMapper;

    @Autowired
    public TraineeInitializer(TraineeDao traineeDao, UserDao userDao, ObjectMapper objectMapper) {
        this.traineeDao = traineeDao;
        this.userDao = userDao;
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
                        traineeDao.findAll());
                userDao.save(user);
                Trainee trainee=new Trainee(traineeInitializeRequest.getDateOfBirth(),
                        traineeInitializeRequest.getAddress(),
                        user.getId());

                 traineeDao.save(trainee);
                System.out.println("Loaded trainee: " + trainee);
            }

        } catch (Exception e) {
            System.out.println("Error occurred while loading trainees from JSON:");
            e.printStackTrace();
        }
        System.out.println("TraineeInitializer finished.");
    }
}
