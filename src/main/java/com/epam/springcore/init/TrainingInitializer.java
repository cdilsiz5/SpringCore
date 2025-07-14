package com.epam.springcore.init;

import com.epam.springcore.dao.TrainingDao;
import com.epam.springcore.model.Training;
import com.epam.springcore.model.enums.TrainingType;
import com.epam.springcore.request.init.TrainingInitializeRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class TrainingInitializer {

    @Value("${training.data.file}")
    private Resource trainingDataFile;

    private final ObjectMapper objectMapper;
    private final TrainingDao trainingDao;

    @Autowired
    public TrainingInitializer(ObjectMapper objectMapper,TrainingDao trainingDao) {
        this.objectMapper = objectMapper;
        this.trainingDao = trainingDao;
    }
    @PostConstruct
    public void init() {
        System.out.println("TrainingInitializer starting (JSON)...");
        try {
            List<TrainingInitializeRequest> trainings = objectMapper.readValue(
                    trainingDataFile.getInputStream(),
                    new TypeReference<List<TrainingInitializeRequest>>() {}
            );

            for (TrainingInitializeRequest request : trainings) {

                Training training = new Training(request.getTraineeId(),
                        request.getTrainerId(),
                        request.getDate(),
                        TrainingType.valueOf(request.getType().toUpperCase()),
                        request.getDurationMinutes());

                trainingDao.save(training);
                System.out.println("Loaded training: " + training);
            }

        } catch (Exception e) {
            System.out.println("Error loading training data from JSON:");
            e.printStackTrace();
        }
        System.out.println("TrainingInitializer finished.");
    }
}
