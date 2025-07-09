package com.epam.springcore.service.impl;

import com.epam.springcore.dao.TrainerDao;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.service.ITrainerService;
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
        trainerDao.save(trainer);
    }

    @Override
    public Trainer getTrainer(String id) {
        return trainerDao.findById(id);
    }

    @Override
    public Collection<Trainer> getAllTrainers() {
        return trainerDao.findAll();
    }
}
