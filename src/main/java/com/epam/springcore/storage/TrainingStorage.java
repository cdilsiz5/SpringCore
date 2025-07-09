package com.epam.springcore.storage;

import com.epam.springcore.model.Training;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrainingStorage {

    private final Map<String, Training> trainingMap = new HashMap<>();

    public Map<String, Training> getTrainingMap() {
        return trainingMap;
    }
}
