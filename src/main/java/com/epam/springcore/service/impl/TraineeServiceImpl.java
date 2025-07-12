package com.epam.springcore.service.impl;

import com.epam.springcore.dao.TraineeDao;
import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.request.CreateTraineeRequest;
import com.epam.springcore.service.ITraineeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.epam.springcore.mapper.TraineeMapper.TRAINEE_MAPPER;

@Service
public class TraineeServiceImpl implements ITraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private final TraineeDao traineeDao;

    public TraineeServiceImpl(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public TraineeDto createTrainee(CreateTraineeRequest request) {
        log.info("Creating new trainee: {} {}", request.getFirstName(), request.getLastName());
        Trainee trainee = TRAINEE_MAPPER.toTrainee(request);
        Trainee saved = traineeDao.save(trainee);
        log.debug("Trainee saved with userId={}", saved.getUserId());
        return TRAINEE_MAPPER.toTraineDto(saved);
    }

    @Override
    public TraineeDto getTrainee(String id) {
        log.info("Fetching trainee with ID: {}", id);
        Trainee trainee = traineeDao.findById(id);
        if (trainee == null) {
            log.warn("Trainee with ID {} not found", id);
            return null;
        }
        return TRAINEE_MAPPER.toTraineDto(trainee);
    }

    @Override
    public List<TraineeDto> getAllTrainees() {
        log.info("Fetching all trainees");
        return TRAINEE_MAPPER.toTraineeList((List<Trainee>) traineeDao.findAll());
    }

    @Override
    public TraineeDto updateTrainee(String id, CreateTraineeRequest request) {
        log.info("Updating trainee with ID: {}", id);
        Trainee existingTrainee = checkTraineeExist(id);
        TRAINEE_MAPPER.updateTrainee(request, existingTrainee);
        Trainee updated = traineeDao.save(existingTrainee);
        log.debug("Trainee with ID {} updated", id);
        return TRAINEE_MAPPER.toTraineDto(updated);
    }

    @Override
    public void deleteTrainee(String id) {
        log.info("Deleting trainee with ID: {}", id);
        checkTraineeExist(id);
        traineeDao.delete(id);
        log.debug("Trainee with ID {} deleted", id);
    }

    private Trainee checkTraineeExist(String id) {
        Trainee existingTrainee = traineeDao.findById(id);
        if (existingTrainee == null) {
            log.error("Trainee with ID {} not found", id);
            throw new RuntimeException("Trainee with ID: " + id + " not found");
        }
        return existingTrainee;
    }
}
