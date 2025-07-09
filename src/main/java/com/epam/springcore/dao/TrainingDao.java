package com.epam.springcore.dao;

import com.epam.springcore.model.Training;
import com.epam.springcore.storage.TrainingStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TrainingDao {

    private TrainingStorage trainingStorage;

    @Autowired
    public void setTrainingStorage(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    public void save(Training training) {
        trainingStorage.getTrainingMap().put(training.getId(), training);
    }

    public Training findById(String id) {
        return trainingStorage.getTrainingMap().get(id);
    }

    public Collection<Training> findAll() {
        return trainingStorage.getTrainingMap().values();
    }
}
