package com.epam.springcore.service.impl;

import com.epam.springcore.dao.TrainingDao;
import com.epam.springcore.model.Training;
import com.epam.springcore.service.ITrainingService;
import com.epam.springcore.validator.TrainingValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class TrainingServiceImpl implements ITrainingService {

    private TrainingDao trainingDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Override
    public void createTraining(Training training) {
        TrainingValidator.validate(training);
        trainingDao.save(training);
    }

    @Override
    public Training getTraining(String id) {
        return validateTrainingExistenceById(id);
    }

    @Override
    public Collection<Training> getAllTrainings() {
        return trainingDao.findAll();
    }

    private Training validateTrainingExistenceById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Training ID cannot be null or blank");
        }

        Training training = trainingDao.findById(id);
        if (training == null) {
            throw new RuntimeException("Training not found with id: " + id);
        }

        return training;
    }
}
