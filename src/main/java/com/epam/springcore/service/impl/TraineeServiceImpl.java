package com.epam.springcore.service.impl;

import com.epam.springcore.dao.TraineeDao;
import com.epam.springcore.dao.UserDao;
import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.User;
import com.epam.springcore.request.CreateTraineeRequest;
import com.epam.springcore.service.ITraineeService;
import com.epam.springcore.util.CredentialGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TraineeServiceImpl implements ITraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private final TraineeDao traineeDao;
    private final UserDao userDao;

    public TraineeServiceImpl(TraineeDao traineeDao, UserDao userDao) {
        this.traineeDao = traineeDao;
        this.userDao = userDao;
    }

    @Override
    public TraineeDto createTrainee(CreateTraineeRequest request) {
        log.info("Creating new trainee: {} {}", request.getFirstName(), request.getLastName());

        User user = new User(request.getFirstName(), request.getLastName(), traineeDao.findAll());
        userDao.save(user);

        Trainee trainee = new Trainee(request.getDateOfBirth(), request.getAddress(), user.getId());
        Trainee saved = traineeDao.save(trainee);

        log.debug("Trainee saved with userId={}", user.getId());
        return toDto(saved, user);
    }

    @Override
    public TraineeDto getTrainee(String id) {
        log.info("Fetching trainee with ID: {}", id);
        Trainee trainee = traineeDao.findById(id);
        if (trainee == null) {
            log.warn("Trainee with ID {} not found", id);
            return null;
        }
        User user = userDao.findById(trainee.getUserId());
        return toDto(trainee, user);
    }

    @Override
    public List<TraineeDto> getAllTrainees() {
        log.info("Fetching all trainees");
        List<Trainee> trainees = (List<Trainee>) traineeDao.findAll();
        List<TraineeDto> dtoList = new ArrayList<>();

        for (Trainee trainee : trainees) {
            User user = userDao.findById(trainee.getUserId());
            dtoList.add(toDto(trainee, user));
        }

        return dtoList;
    }

    @Override
    public TraineeDto updateTrainee(String id, CreateTraineeRequest request) {
        log.info("Updating trainee with ID: {}", id);
        Trainee existingTrainee = checkTraineeExist(id);
        User existingUser = userDao.findById(existingTrainee.getUserId());
        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setUsername( CredentialGenerator.generateUsername(request
                .getFirstName(), request.getLastName(),traineeDao.findAll()));
        userDao.save(existingUser);
        existingTrainee.setDateOfBirth(request.getDateOfBirth());
        existingTrainee.setAddress(request.getAddress());

        Trainee updated = traineeDao.save(existingTrainee);

        User user = userDao.findById(existingTrainee.getUserId());
        log.debug("Trainee with ID {} updated", id);
        return toDto(updated, user);
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

    private TraineeDto toDto(Trainee trainee, User user) {
        TraineeDto dto = new TraineeDto();
        dto.setId(trainee.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setUsername(user.getUsername());
        dto.setDateOfBirth(trainee.getDateOfBirth());
        dto.setAddress(trainee.getAddress());
        return dto;
    }
}
