package com.epam.springcore.storage;

import com.epam.springcore.model.Trainee;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TraineeStorage {

    private final Map<String, Trainee> traineeMap = new HashMap<>();

    public Map<String, Trainee> getTraineeMap() {
        return traineeMap;
    }
}
