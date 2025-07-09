package com.epam.springcore.storage;

import com.epam.springcore.model.Trainer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrainerStorage {

    private final Map<String, Trainer> trainerMap = new HashMap<>();

    public Map<String, Trainer> getTrainerMap() {
        return trainerMap;
    }
}
