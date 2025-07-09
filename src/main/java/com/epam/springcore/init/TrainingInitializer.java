package com.epam.springcore.init;

import com.epam.springcore.model.Training;
import com.epam.springcore.request.TrainingInitializeRequest;
import com.epam.springcore.service.ITrainingService;
import com.epam.springcore.storage.TrainingStorage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;

@Component
public class TrainingInitializer {

    @Value("${training.data.file}")
    private Resource trainingDataFile;

    private final ObjectMapper objectMapper;
    private final ITrainingService trainingService;

    @Autowired
    public TrainingInitializer(ObjectMapper objectMapper,ITrainingService trainingService) {
        this.objectMapper = objectMapper;
        this.trainingService = trainingService;
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
                        request.getType(),
                        request.getDurationMinutes());
                trainingService.createTraining(training);
                System.out.println("Loaded training: " + training);
            }

        } catch (Exception e) {
            System.out.println("Error loading training data from JSON:");
            e.printStackTrace();
        }
        System.out.println("TrainingInitializer finished.");
    }
}
