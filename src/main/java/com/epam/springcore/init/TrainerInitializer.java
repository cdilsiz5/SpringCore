package com.epam.springcore.init;

import com.epam.springcore.dao.TrainerDao;
import com.epam.springcore.dao.UserDao;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.User;
import com.epam.springcore.request.TrainerInitializeRequest;
import com.epam.springcore.service.ITrainerService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainerInitializer {

    @Value("${trainer.data.file}")
    private Resource trainerDataFile;

    private final TrainerDao trainerDao;
    private final UserDao userDao;
    private final ObjectMapper objectMapper;

    @Autowired
    public TrainerInitializer( TrainerDao trainerDao, UserDao userDao, ObjectMapper objectMapper) {
        this.trainerDao = trainerDao;
        this.userDao = userDao;
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
                        trainerDao.findAll());

                userDao.save(user);
                Trainer trainer=new Trainer( trainerInitializeRequest.getSpecialty(),user.getId());

                trainerDao.save(trainer);
                System.out.println("Loaded trainer: " + trainer);
            }

        } catch (Exception e) {
            System.out.println("Error occurred while loading trainers from JSON:");
            e.printStackTrace();
        }
}
}
