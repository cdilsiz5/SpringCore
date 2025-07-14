package com.epam.springcore.dao;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.storage.TraineeStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TraineeDao {

    private TraineeStorage traineeStorage;

    @Autowired
    public void setTraineeStorage(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    public Trainee save(Trainee trainee) {
        traineeStorage.getTraineeMap().put(trainee.getUserId(), trainee);
        return traineeStorage.getTraineeMap().get(trainee.getUserId());
    }

    public Trainee findById(String id) {
        return traineeStorage.getTraineeMap().get(id);
    }

    public Collection<Trainee> findAll() {
        return traineeStorage.getTraineeMap().values().stream().toList();
    }

    public void delete(String id) {
        traineeStorage.getTraineeMap().remove(id);
    }

}
