package com.epam.springcore.dao;

import com.epam.springcore.model.Trainer;
import com.epam.springcore.storage.TrainerStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TrainerDao {

    private TrainerStorage trainerStorage;

    @Autowired
    public void setTrainerStorage(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    public Trainer save(Trainer trainer) {
        trainerStorage.getTrainerMap().put(trainer.getUserId(), trainer);
        return trainerStorage.getTrainerMap().get(trainer.getUserId());
    }

    public Trainer findById(String id) {
        return trainerStorage.getTrainerMap().get(id);
    }

    public Collection<Trainer> findAll() {
        return trainerStorage.getTrainerMap().values();
    }

    public void delete(String id) {
        trainerStorage.getTrainerMap().remove(id);
    }
}
