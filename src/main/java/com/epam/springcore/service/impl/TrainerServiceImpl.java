package com.epam.springcore.service.impl;

import com.epam.springcore.dao.TrainerDao;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.service.ITrainerService;
import com.epam.springcore.validator.TrainerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class TrainerServiceImpl implements ITrainerService {

    private TrainerDao trainerDao;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public void createTrainer(Trainer trainer) {
        TrainerValidator.validate(trainer);
        trainerDao.save(trainer);
    }

    @Override
    public Trainer getTrainer(String id) {
        return validateTrainerExistenceById(id);
    }

    @Override
    public Collection<Trainer> getAllTrainers() {
        return trainerDao.findAll();
    }

    private Trainer validateTrainerExistenceById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Trainer ID cannot be null or blank");
        }

        Trainer trainer = trainerDao.findById(id);
        if (trainer == null) {
            throw new RuntimeException("Trainer not found with id: " + id);
        }

        return trainer;
    }
}
