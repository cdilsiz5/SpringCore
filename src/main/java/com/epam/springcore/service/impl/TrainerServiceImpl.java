package com.epam.springcore.service.impl;

import com.epam.springcore.dao.TrainerDao;
import com.epam.springcore.dao.UserDao;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.User;
import com.epam.springcore.request.CreateTrainerRequest;
import com.epam.springcore.service.ITrainerService;
import com.epam.springcore.util.CredentialGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrainerServiceImpl implements ITrainerService {
    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private final TrainerDao trainerDao;
    private final UserDao userDao;

    public TrainerServiceImpl(TrainerDao trainerDao, UserDao userDao) {
        this.trainerDao = trainerDao;
        this.userDao = userDao;
    }

    @Override
    public TrainerDto createTrainer(CreateTrainerRequest request) {
        log.info("Creating new trainer: {} {}", request.getFirstName(), request.getLastName());

        User user = new User(request.getFirstName(), request.getLastName(), trainerDao.findAll());
        userDao.save(user);

        Trainer trainer = new Trainer(request.getSpecialty(), user.getId());
        Trainer savedTrainer = trainerDao.save(trainer);

        log.debug("Trainer saved with userId={}", user.getId());
        return getTrainerDto(savedTrainer, user);
    }

    @Override
    public TrainerDto getTrainer(String id) {
        log.info("Fetching trainer with ID: {}", id);
        Trainer trainer = trainerDao.findById(id);
        if (trainer == null) {
            log.warn("Trainer with ID {} not found", id);
            throw  new NotFoundException("Trainer not found");

        }

        User user = userDao.findById(trainer.getUserId());
        return getTrainerDto(trainer, user);
    }

    @Override
    public List<TrainerDto> getAllTrainers() {
        log.info("Fetching all trainers");
        List<Trainer> trainers = (List<Trainer>) trainerDao.findAll();
        List<TrainerDto> dtoList = new ArrayList<>();

        for (Trainer trainer : trainers) {
            User user = userDao.findById(trainer.getUserId());
            dtoList.add(getTrainerDto(trainer, user));
        }

        return dtoList;
    }

    @Override
    public TrainerDto updateTrainer(String id, CreateTrainerRequest request) {
        log.info("Updating trainer with ID: {}", id);
        Trainer existingTrainer = checkTrainerExist(id);

        User existingUser = userDao.findById(existingTrainer.getUserId());
        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setUsername( CredentialGenerator.generateUsername(request
                .getFirstName(), request.getLastName(),trainerDao.findAll()));
        userDao.save(existingUser);
        existingTrainer.setSpecialization(request.getSpecialty());
        Trainer updatedTrainer = trainerDao.save(existingTrainer);

        User user = userDao.findById(existingTrainer.getUserId());

        log.debug("Trainer with ID {} updated", id);
        return getTrainerDto(updatedTrainer, user);
    }

    @Override
    public void deleteTrainer(String id) {
        log.info("Deleting trainer with ID: {}", id);
        checkTrainerExist(id);
        trainerDao.delete(id);
        log.debug("Trainer with ID {} deleted", id);
    }

    private Trainer checkTrainerExist(String id) {
        Trainer existingTrainer = trainerDao.findById(id);
        if (existingTrainer == null) {
            log.error("Trainer with ID {} not found", id);
            throw new NotFoundException("Trainer with ID: " + id + " not found");
        }
        return existingTrainer;
    }

    private TrainerDto getTrainerDto(Trainer trainer, User user) {
        TrainerDto dto = new TrainerDto();
        dto.setId(trainer.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setUsername(user.getUsername());
        dto.setSpecialization(trainer.getSpecialization().toString());
        return dto;
    }
}
