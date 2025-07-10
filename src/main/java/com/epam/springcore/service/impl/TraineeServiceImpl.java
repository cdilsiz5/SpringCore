package com.epam.springcore.service.impl;

import com.epam.springcore.dao.TraineeDao;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.service.ITraineeService;
import com.epam.springcore.validator.TraineeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class TraineeServiceImpl implements ITraineeService {

    private TraineeDao traineeDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public void createTrainee(Trainee trainee) {
        TraineeValidator.validate(trainee);
        traineeDao.save(trainee);
    }

    @Override
    public Trainee getTrainee(String id) {
        return validateTraineeExistenceById(id);
    }

    @Override
    public Collection<Trainee> getAllTrainees() {
        return traineeDao.findAll();
    }

    private Trainee validateTraineeExistenceById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Trainee ID cannot be null or blank");
        }

        Trainee trainee = traineeDao.findById(id);
        if (trainee == null) {
            throw new RuntimeException("Trainee not found with id: " + id);
        }

        return trainee;
    }
}
