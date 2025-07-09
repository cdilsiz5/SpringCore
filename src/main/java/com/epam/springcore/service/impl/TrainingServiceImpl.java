package com.epam.springcore.service.impl;

import com.epam.springcore.dao.TrainingDao;
import com.epam.springcore.model.Training;
import com.epam.springcore.service.ITrainingService;
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
        trainingDao.save(training);
    }

    @Override
    public Training getTraining(String id) {
        return trainingDao.findById(id);
    }

    @Override
    public Collection<Training> getAllTrainings() {
        return trainingDao.findAll();
    }
}
