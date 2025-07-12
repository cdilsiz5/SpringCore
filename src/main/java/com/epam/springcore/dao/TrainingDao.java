package com.epam.springcore.dao;

import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.model.Training;
import com.epam.springcore.storage.TrainingStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class TrainingDao {

    private TrainingStorage trainingStorage;

    @Autowired
    public void setTrainingStorage(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    public Training save(Training training) {
        trainingStorage.getTrainingMap().put(training.getId(), training);
        return trainingStorage.getTrainingMap().get(training.getId()) ;
    }
    public Training findById(String id) {
        return trainingStorage.getTrainingMap().get(id);
    }

    public List<Training> findAll() {
        return trainingStorage.getTrainingMap().values().stream().toList();
    }

    public void delete(String id) {
        trainingStorage.getTrainingMap().remove(id);
    }


}
