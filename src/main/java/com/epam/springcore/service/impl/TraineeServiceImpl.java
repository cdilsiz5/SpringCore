package com.epam.springcore.service;

import com.epam.springcore.dao.TraineeDao;
import com.epam.springcore.model.Trainee;
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
        traineeDao.save(trainee);
    }

    @Override
    public Trainee getTrainee(String id) {
        return traineeDao.findById(id);
    }

    @Override
    public Collection<Trainee> getAllTrainees() {
        return traineeDao.findAll();
    }
}
